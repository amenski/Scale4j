/*
 * Copyright 2024 Scale4j
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.scale4j;

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.WatermarkPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for AsyncScale4j.
 */
class AsyncScale4jTest {

    @TempDir
    Path tempDir;

    @Test
    void async_load_fromBufferedImage_returnsCompletedFuture() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source);

        assertThat(future).isCompletedWithValue(source);
    }

    @Test
    void async_load_fromFile_returnsLoadedImage() throws IOException {
        // Create a temporary PNG file
        BufferedImage source = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        File tempFile = tempDir.resolve("source.png").toFile();
        ImageSaver.write(source, "png", tempFile);

        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(tempFile);

        assertThat(future).isCompleted();
        BufferedImage loaded = future.join();
        assertThat(loaded.getWidth()).isEqualTo(50);
        assertThat(loaded.getHeight()).isEqualTo(50);
    }

    @Test
    void async_load_fromFile_missingFile_throwsException() {
        File missing = new File("/non/existent/file.png");
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(missing);

        assertThat(future).isCompletedExceptionally();
        assertThatThrownBy(future::join)
                .hasRootCauseInstanceOf(IOException.class)
                .hasMessageContaining("Failed to load image");
    }

    @Test
    void async_resize_chaining_works() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source)
                .resize(100, 50)
                .mode(ResizeMode.FIT)
                .quality(ResizeQuality.HIGH)
                .apply(source);

        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        // FIT with aspect ratio 2:1, target 100x50 => width 100, height 50 (no change)
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void async_resize_withoutMode_usesDefault() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source)
                .resize(100, 50)
                .automatic()
                .medium()
                .apply(source);

        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void async_watermark_text() {
        BufferedImage source = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source)
                .watermark("Test")
                .apply()
                .apply(source);

        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        assertThat(result).isNotNull();
        // Watermark applied, no easy assertion beyond non‑null
    }

    @Test
    void async_watermark_withOptions() {
        BufferedImage source = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source)
                .watermark("Hello", WatermarkPosition.BOTTOM_RIGHT, 0.5f)
                .apply()
                .apply(source);

        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        assertThat(result).isNotNull();
    }

    @Test
    void async_toFile_savesFile() throws IOException {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();
        Path output = tempDir.resolve("output.png");

        CompletableFuture<Void> future = async.toFile(source, output, "png");

        assertThat(future).isCompleted();
        future.join(); // ensure no exception
        assertThat(output).exists();
        // verify file can be loaded
        BufferedImage loaded = ImageLoader.load(output.toFile());
        assertThat(loaded.getWidth()).isEqualTo(100);
        assertThat(loaded.getHeight()).isEqualTo(100);
    }

    @Test
    void async_toFile_invalidFormat_throwsException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();
        Path output = tempDir.resolve("output.xyz");

        CompletableFuture<Void> future = async.toFile(source, output, "xyz");

        assertThat(future).isCompletedExceptionally();
        assertThatThrownBy(future::join)
                .hasRootCauseInstanceOf(IOException.class)
                .hasMessageContaining("Failed to save image");
    }

    @Test
    void async_shutdown_doesNotAffectAlreadySubmittedTasks() throws Exception {
        // Use a custom single-thread executor to control execution
        var executor = Executors.newSingleThreadExecutor();
        AsyncScale4j async = AsyncScale4j.create(executor);
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        CompletableFuture<BufferedImage> future = async.load(source)
                .resize(50, 50)
                .automatic()
                .medium()
                .apply(source);

        // Shutdown executor before task might have run
        executor.shutdown();

        // The task was already submitted, so it should still complete
        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void async_customExecutor_respected() {
        var executor = Executors.newSingleThreadExecutor();
        AsyncScale4j async = AsyncScale4j.create(executor);
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        CompletableFuture<BufferedImage> future = async.load(source);

        assertThat(future).isCompletedWithValue(source);
        executor.shutdown();
    }

    @Test
    void async_chainMultipleOperations() {
        BufferedImage source = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
        AsyncScale4j async = AsyncScale4j.create();

        CompletableFuture<BufferedImage> future = async.load(source)
                .resize(100, 75)
                .automatic()
                .medium()
                .apply(source)
                .thenCompose(image -> async.watermark("Watermark").apply().apply(image))
                .thenCompose(image -> async.toFile(image, tempDir.resolve("final.png"), "png")
                        .thenApply(v -> image));

        assertThat(future).isCompleted();
        BufferedImage result = future.join();
        assertThat(result).isNotNull();
    }
}