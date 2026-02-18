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

import com.scale4j.exception.ImageLoadException;
import com.scale4j.exception.ImageSaveException;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.TextWatermark;
import com.scale4j.watermark.Watermark;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

/**
 * Asynchronous image processing using CompletableFuture.
 * Supports virtual threads (Java 21+) and custom executor services.
 */
public final class AsyncScale4j {

    private final ExecutorService executor;
    
    private static final ExecutorService DEFAULT_EXECUTOR = createDefaultExecutor();

    private AsyncScale4j(ExecutorService executor) {
        this.executor = executor;
    }

    /**
     * Creates a new async processor using a default virtual thread executor (Java 21+)
     * or a work-stealing thread pool.
     *
     * @return a new AsyncScale4j instance
     */
    static AsyncScale4j create() {
        return new AsyncScale4j(DEFAULT_EXECUTOR);
    }

    /**
     * Creates a new async processor with a custom executor service.
     *
     * @param executor the executor service to use
     * @return a new AsyncScale4j instance
     */
    static AsyncScale4j create(ExecutorService executor) {
        return new AsyncScale4j(executor);
    }

    /**
     * Creates a default executor using virtual threads if available (Java 21+).
     */
    private static ExecutorService createDefaultExecutor() {
        // Try to use virtual threads (Java 21+)
        try {
            var executorClass = Class.forName("java.util.concurrent.Executors");
            var newVirtualThreadPerTaskExecutor = executorClass.getMethod("newVirtualThreadPerTaskExecutor");
            return (ExecutorService) newVirtualThreadPerTaskExecutor.invoke(null);
        } catch (ClassNotFoundException | NoSuchMethodException |
                 IllegalAccessException | InvocationTargetException e) {
            // Fall back to a work-stealing pool with named threads
            return Executors.newWorkStealingPool();
        }
    }

    /**
     * Loads an image asynchronously.
     *
     * @param file the source file
     * @return a CompletableFuture containing the loaded image
     */
    public CompletableFuture<BufferedImage> load(File file) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return ImageLoader.load(file);
            } catch (ImageLoadException e) {
                throw new RuntimeException("Failed to load image: " + file.getAbsolutePath(), e);
            }
        }, executor);
    }

    /**
     * Creates a completed future with the provided image.
     *
     * @param image the source image
     * @return a completed CompletableFuture
     */
    public CompletableFuture<BufferedImage> load(BufferedImage image) {
        return CompletableFuture.completedFuture(image);
    }

    /**
     * Resizes the image asynchronously.
     *
     * @param width the target width
     * @param height the target height
     * @return this async processor
     */
    public AsyncResizeOperation resize(int width, int height) {
        return new AsyncResizeOperation(this, width, height);
    }

    /**
     * Applies a watermark asynchronously.
     *
     * @param watermark the watermark to apply
     * @return this async processor
     */
    public AsyncWatermarkOperation watermark(Watermark watermark) {
        return new AsyncWatermarkOperation(this, watermark);
    }

    /**
     * Applies a text watermark asynchronously.
     *
     * @param text the watermark text
     * @return this async processor
     */
    public AsyncWatermarkOperation watermark(String text) {
        return watermark(TextWatermark.of(text));
    }

    /**
     * Saves the image asynchronously.
     *
     * @param output the output file
     * @param format the image format
     * @return a CompletableFuture that completes when the file is written
     */
    public CompletableFuture<Void> toFile(BufferedImage image, Path output, String format) {
        return CompletableFuture.runAsync(() -> {
            try {
                ImageSaver.write(image, format, output.toFile());
            } catch (ImageSaveException e) {
                throw new RuntimeException("Failed to save image: " + output, e);
            }
        }, executor);
    }

    /**
     * Shuts down the executor service.
     */
    public void shutdown() {
        executor.shutdown();
    }

    /**
     * Shuts down the executor service immediately.
     */
    public void shutdownNow() {
        executor.shutdownNow();
    }

    ExecutorService getExecutor() {
        return executor;
    }

    /**
     * Helper class for chaining resize operations.
     */
    public static final class AsyncResizeOperation {

        private final AsyncScale4j asyncProcessor;
        private final int width;
        private final int height;

        AsyncResizeOperation(AsyncScale4j asyncProcessor, int width, int height) {
            this.asyncProcessor = asyncProcessor;
            this.width = width;
            this.height = height;
        }

        /**
         * Specifies the resize mode.
         *
         * @param mode the resize mode
         * @return this operation
         */
        public AsyncProcessorWithMode mode(ResizeMode mode) {
            return new AsyncProcessorWithMode(asyncProcessor, width, height, mode);
        }

        /**
         * Uses automatic resize mode.
         *
         * @return this operation
         */
        public AsyncProcessorWithMode automatic() {
            return mode(ResizeMode.AUTOMATIC);
        }
    }

    /**
     * Helper class for chaining resize operations with mode.
     */
    public static final class AsyncProcessorWithMode {

        private final AsyncScale4j asyncProcessor;
        private final int width;
        private final int height;
        private final ResizeMode mode;

        AsyncProcessorWithMode(AsyncScale4j asyncProcessor, int width, int height, ResizeMode mode) {
            this.asyncProcessor = asyncProcessor;
            this.width = width;
            this.height = height;
            this.mode = mode;
        }

        /**
         * Specifies the resize quality.
         *
         * @param quality the resize quality
         * @return a function that applies the resize
         */
        public Function<BufferedImage, CompletableFuture<BufferedImage>> quality(ResizeQuality quality) {
            return image -> CompletableFuture.supplyAsync(
                    () -> com.scale4j.ops.ResizeOperation.resize(image, width, height, mode, quality),
                    asyncProcessor.getExecutor()
            );
        }

        /**
         * Uses medium quality.
         *
         * @return a function that applies the resize
         */
        public Function<BufferedImage, CompletableFuture<BufferedImage>> medium() {
            return quality(ResizeQuality.MEDIUM);
        }
    }

    /**
     * Helper class for chaining watermark operations.
     */
    public static final class AsyncWatermarkOperation {

        private final AsyncScale4j asyncProcessor;
        private final Watermark watermark;

        AsyncWatermarkOperation(AsyncScale4j asyncProcessor, Watermark watermark) {
            this.asyncProcessor = asyncProcessor;
            this.watermark = watermark;
        }

        /**
         * Returns a function that applies the watermark.
         *
         * @return a function that applies the watermark asynchronously
         */
        public Function<BufferedImage, CompletableFuture<BufferedImage>> apply() {
            return image -> CompletableFuture.supplyAsync(
                    () -> {
                        watermark.apply(image);
                        return image;
                    },
                    asyncProcessor.getExecutor()
            );
        }
    }
}
