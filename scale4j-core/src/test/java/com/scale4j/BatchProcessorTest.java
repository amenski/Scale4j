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

import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for BatchProcessor.
 */
class BatchProcessorTest {

    @TempDir
    Path tempDir;

    @Test
    void batch_emptyImages_returnsEmptyList() {
        List<BufferedImage> results = Scale4j.batch()
                .images(new ArrayList<>())
                .execute();

        assertThat(results).isEmpty();
    }

    @Test
    void batch_singleImage_resize() {
        // Create test images
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        // Process
        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(0).getHeight()).isEqualTo(50);
    }

    @Test
    void batch_multipleImages_resize() {
        // Create test images
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(400, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage image3 = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2, image3);

        // Process
        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .execute();

        assertThat(results).hasSize(3);
        for (BufferedImage result : results) {
            assertThat(result.getWidth()).isEqualTo(100);
            assertThat(result.getHeight()).isEqualTo(50);
        }
    }

    @Test
    void batch_preservesOrder() {
        // Create test images with different sizes
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage image3 = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2, image3);

        // Process with no resize to keep original sizes
        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .execute();

        assertThat(results).hasSize(3);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(1).getWidth()).isEqualTo(200);
        assertThat(results.get(2).getWidth()).isEqualTo(300);
    }

    @Test
    void batch_chainedOperations() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .rotate(90)
                .pad(10)
                .execute();

        assertThat(results).hasSize(1);
        // After rotate 90, width and height swap: 50x100, then pad 10 adds 20 each: 70x120
        assertThat(results.get(0).getWidth()).isEqualTo(70);
        assertThat(results.get(0).getHeight()).isEqualTo(120);
    }

    @Test
    void batch_withResizeModeAndQuality() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .resize(100, 50, ResizeMode.FIT, ResizeQuality.HIGH)
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(0).getHeight()).isEqualTo(50);
    }

    @Test
    void batch_scale() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .scale(0.5)
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(0).getHeight()).isEqualTo(50);
    }

    @Test
    void batch_crop() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .crop(50, 25, 100, 50)
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(0).getHeight()).isEqualTo(50);
    }

    @Test
    void batch_rotate() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .rotate(90)
                .execute();

        assertThat(results).hasSize(1);
        // After 90 degree rotation, dimensions swap
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(0).getHeight()).isEqualTo(200);
    }

    @Test
    void batch_pad() {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .pad(10)
                .execute();

        assertThat(results).hasSize(1);
        // Padding adds 10 to each side = 20 total
        assertThat(results.get(0).getWidth()).isEqualTo(120);
        assertThat(results.get(0).getHeight()).isEqualTo(120);
    }

    @Test
    void batch_watermark() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .watermark("Test")
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0)).isNotNull();
    }

    @Test
    void batch_addImage() {
        BufferedImage image1 = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

        List<BufferedImage> results = Scale4j.batch()
                .addImage(image1)
                .addImage(image2)
                .execute();

        assertThat(results).hasSize(2);
    }

    @Test
    void batch_noImages_returnsEmptyList() {
        List<BufferedImage> results = Scale4j.batch().execute();
        assertThat(results).isEmpty();
    }

    @Test
    void batch_parallelism() {
        // Create test images
        List<BufferedImage> images = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            images.add(new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB));
        }

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .parallel(4)
                .execute();

        assertThat(results).hasSize(10);
    }

    @Test
    void batch_parallelInvalid_throwsException() {
        assertThatThrownBy(() -> Scale4j.batch().parallel(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Parallelism must be at least 1");
    }

    @Test
    void batch_executeAsync() throws Exception {
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2);

        List<CompletableFuture<BufferedImage>> futures = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .executeAsync();

        assertThat(futures).hasSize(2);
        
        // Wait for all to complete
        List<BufferedImage> results = new ArrayList<>();
        for (CompletableFuture<BufferedImage> future : futures) {
            results.add(future.join());
        }
        
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(1).getWidth()).isEqualTo(100);
    }

    @Test
    void batch_executeAndJoin() {
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2);

        CompletableFuture<List<BufferedImage>> future = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .executeAndJoin();

        List<BufferedImage> results = future.join();
        
        assertThat(results).hasSize(2);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        assertThat(results.get(1).getWidth()).isEqualTo(100);
    }

    @Test
    void batch_customExecutor() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);
        
        var executor = Executors.newFixedThreadPool(2);

        List<BufferedImage> results = Scale4j.batch()
                .images(images)
                .executor(executor)
                .resize(100, 50)
                .execute();

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getWidth()).isEqualTo(100);
        
        executor.shutdown();
    }

    @Test
    void batch_batchWithList() {
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2);

        List<BufferedImage> results = Scale4j.batch(images)
                .resize(100, 50)
                .execute();

        assertThat(results).hasSize(2);
    }

    @Test
    void batch_processorGetImageCount() {
        BufferedImage image1 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage image2 = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image1, image2);

        BatchProcessor processor = Scale4j.batch()
                .images(images)
                .resize(100, 50)
                .build();

        assertThat(processor.getImageCount()).isEqualTo(2);
    }

    @Test
    void batch_processorGetParallelism() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        BatchProcessor processor = Scale4j.batch()
                .images(images)
                .parallel(4)
                .build();

        assertThat(processor.getParallelism()).isEqualTo(4);
    }

    @Test
    void batch_processorIsPreserveOrder() {
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        List<BufferedImage> images = Arrays.asList(image);

        BatchProcessor processor = Scale4j.batch()
                .images(images)
                .preserveOrder(true)
                .build();

        assertThat(processor.isPreserveOrder()).isTrue();
    }
}
