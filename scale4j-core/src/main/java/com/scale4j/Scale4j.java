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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.Set;

/**
 * Main entry point for the Scale4j image processing library.
 * Provides a fluent API for image manipulation operations.
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * BufferedImage result = Scale4j.load(image)
 *     .resize(300, 200)
 *     .mode(ResizeMode.FIT)
 *     .quality(ResizeQuality.HIGH)
 *     .watermark(TextWatermark.of("© 2024"))
 *     .build();
 * }</pre>
 *
 * @author Scale4j
 * @version 5.0.0
 */
public final class Scale4j {

    private Scale4j() {
        // Utility class - prevent instantiation
    }

    // ==================== Load Operations ====================

    /**
     * Creates a new builder from a BufferedImage.
     *
     * @param image the source image
     * @return a new builder instance
     */
    public static Scale4jBuilder load(BufferedImage image) {
        return new Scale4jBuilder(image);
    }

    /**
     * Creates a new builder by loading an image from a file.
     *
     * @param file the source file
     * @return a new builder instance
     * @throws IOException if the file cannot be read
     */
    public static Scale4jBuilder load(File file) throws IOException {
        return new Scale4jBuilder(ImageLoader.load(file));
    }

    /**
     * Creates a new builder by loading an image from a file path.
     *
     * @param path the file path
     * @return a new builder instance
     * @throws IOException if the file cannot be read
     */
    public static Scale4jBuilder load(Path path) throws IOException {
        return new Scale4jBuilder(ImageLoader.load(path));
    }

    /**
     * Creates a new builder by loading an image from an InputStream.
     *
     * @param stream the input stream
     * @return a new builder instance
     * @throws IOException if the stream cannot be read
     */
    public static Scale4jBuilder load(InputStream stream) throws IOException {
        return new Scale4jBuilder(ImageLoader.load(stream));
    }

    /**
     * Creates a new builder by loading an image from a URL.
     *
     * @param url the source URL
     * @return a new builder instance
     * @throws IOException if the URL cannot be read
     */
    public static Scale4jBuilder load(URL url) throws IOException {
        return new Scale4jBuilder(ImageLoader.load(url));
    }

    // ==================== Async Entry Points ====================

    /**
     * Creates a new async processor using virtual threads (Java 21+)
     * or a default thread pool.
     *
     * @return a new AsyncScale4j instance
     */
    public static AsyncScale4j async() {
        return AsyncScale4j.create();
    }

    /**
     * Creates a new async processor with a custom executor service.
     *
     * @param executor the executor service to use
     * @return a new AsyncScale4j instance
     */
    public static AsyncScale4j async(java.util.concurrent.ExecutorService executor) {
        return AsyncScale4j.create(executor);
    }

    // ==================== Batch Processing Entry Points ====================

    /**
     * Creates a new batch processor for processing multiple images.
     * Use this for applying the same operations to multiple images.
     *
     * <p>Example usage:</p>
     * <pre>{@code
     * List<BufferedImage> results = Scale4j.batch()
     *     .images(listOfImages)
     *     .resize(300, 200)
     *     .parallel(4)
     *     .execute();
     * }</pre>
     *
     * @return a new BatchProcessorBuilder instance
     */
    public static BatchProcessorBuilder batch() {
        return new BatchProcessorBuilder();
    }

    /**
     * Creates a new batch processor with the specified images.
     *
     * @param images the list of images to process
     * @return a new BatchProcessorBuilder instance
     */
    public static BatchProcessorBuilder batch(java.util.List<BufferedImage> images) {
        return new BatchProcessorBuilder().images(images);
    }

    // ==================== Utility Methods ====================

    /**
     * Checks if the specified format is supported for reading.
     *
     * @param format the format name (e.g., "png", "jpg", "webp")
     * @return true if the format is supported
     */
    public static boolean isSupportedFormat(String format) {
        return ImageLoader.isSupportedFormat(format);
    }

    /**
     * Returns the set of supported image formats.
     *
     * @return a set of supported format names
     */
    public static Set<String> getSupportedFormats() {
        return ImageLoader.getSupportedFormats();
    }

    /**
     * Gets the current version of Scale4j.
     *
     * @return the version string
     */
    public static String getVersion() {
        return "5.0.0-SNAPSHOT";
    }
}
