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
import com.scale4j.log.Scale4jLogger;
import com.scale4j.log.Scale4jLoggerFactory;
import com.scale4j.metadata.ExifMetadata;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
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
 * @version 1.0.0
 */
public final class Scale4j {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(Scale4j.class);

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
        LOGGER.debug("Creating builder from BufferedImage ({}x{})", 
                image != null ? image.getWidth() : 0, image != null ? image.getHeight() : 0);
        return new Scale4jBuilder(image);
    }

    /**
     * Creates a new builder by loading an image from a file.
     *
     * @param file the source file
     * @return a new builder instance
     * @throws ImageLoadException if the file cannot be read
     */
    public static Scale4jBuilder load(File file) throws ImageLoadException {
        LOGGER.debug("Loading image from file: {}", file);
        return new Scale4jBuilder(ImageLoader.load(file));
    }

    /**
     * Creates a new builder by loading an image from a file path.
     *
     * @param path the file path
     * @return a new builder instance
     * @throws ImageLoadException if the file cannot be read
     */
    public static Scale4jBuilder load(Path path) throws ImageLoadException {
        LOGGER.debug("Loading image from path: {}", path);
        return new Scale4jBuilder(ImageLoader.load(path));
    }

    /**
     * Creates a new builder by loading an image from an InputStream.
     *
     * @param stream the input stream
     * @return a new builder instance
     * @throws ImageLoadException if the stream cannot be read
     */
    public static Scale4jBuilder load(InputStream stream) throws ImageLoadException {
        LOGGER.debug("Loading image from input stream");
        return new Scale4jBuilder(ImageLoader.load(stream));
    }

    /**
     * Creates a new builder by loading an image from a URL.
     *
     * @param url the source URL
     * @return a new builder instance
     * @throws ImageLoadException if the URL cannot be read
     */
    public static Scale4jBuilder load(URL url) throws ImageLoadException {
        LOGGER.debug("Loading image from URL: {}", url);
        return new Scale4jBuilder(ImageLoader.load(url));
    }

    // ==================== Load with Metadata Operations ====================

    /**
     * Creates a new builder from an ImageWithMetadata.
     *
     * @param imageWithMetadata the source image with metadata
     * @return a new builder instance
     */
    public static Scale4jBuilder load(ImageWithMetadata imageWithMetadata) {
        LOGGER.debug("Creating builder from ImageWithMetadata ({}x{})", 
                imageWithMetadata != null ? imageWithMetadata.getImage().getWidth() : 0,
                imageWithMetadata != null ? imageWithMetadata.getImage().getHeight() : 0);
        return new Scale4jBuilder(
            imageWithMetadata.getImage(),
            imageWithMetadata.getMetadata(),
            imageWithMetadata.getSourceFormat()
        );
    }

    /**
     * Creates a new builder by loading an image from a file, preserving metadata.
     *
     * @param file the source file
     * @return a new builder instance with metadata
     * @throws ImageLoadException if the file cannot be read
     */
    public static Scale4jBuilder loadWithMetadata(File file) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from file: {}", file);
        ImageWithMetadata iwm = ImageLoader.loadWithMetadata(file);
        return new Scale4jBuilder(iwm.getImage(), iwm.getMetadata(), iwm.getSourceFormat());
    }

    /**
     * Creates a new builder by loading an image from a file path, preserving metadata.
     *
     * @param path the file path
     * @return a new builder instance with metadata
     * @throws ImageLoadException if the file cannot be read
     */
    public static Scale4jBuilder loadWithMetadata(Path path) throws ImageLoadException {
        return loadWithMetadata(path.toFile());
    }

    /**
     * Creates a new builder by loading an image from an InputStream, preserving metadata.
     *
     * @param stream the input stream
     * @return a new builder instance with metadata
     * @throws ImageLoadException if the stream cannot be read
     */
    public static Scale4jBuilder loadWithMetadata(InputStream stream) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from input stream");
        ImageWithMetadata iwm = ImageLoader.loadWithMetadata(stream);
        return new Scale4jBuilder(iwm.getImage(), iwm.getMetadata(), iwm.getSourceFormat());
    }

    /**
     * Creates a new builder by loading an image from a URL, preserving metadata.
     *
     * @param url the source URL
     * @return a new builder instance with metadata
     * @throws ImageLoadException if the URL cannot be read
     */
    public static Scale4jBuilder loadWithMetadata(URL url) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from URL: {}", url);
        ImageWithMetadata iwm = ImageLoader.loadWithMetadata(url);
        return new Scale4jBuilder(iwm.getImage(), iwm.getMetadata(), iwm.getSourceFormat());
    }

    // ==================== Async Entry Points ====================

    /**
     * Creates a new async processor using virtual threads (Java 21+)
     * or a default thread pool.
     *
     * @return a new AsyncScale4j instance
     */
    public static AsyncScale4j async() {
        LOGGER.debug("Creating async processor");
        return AsyncScale4j.create();
    }

    /**
     * Creates a new async processor with a custom executor service.
     *
     * @param executor the executor service to use
     * @return a new AsyncScale4j instance
     */
    public static AsyncScale4j async(java.util.concurrent.ExecutorService executor) {
        LOGGER.debug("Creating async processor with custom executor");
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
