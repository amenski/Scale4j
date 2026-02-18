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
import com.scale4j.util.ImageFormatUtils;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class for loading images from various sources.
 */
final class ImageLoader {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(ImageLoader.class);

    private ImageLoader() {
        // Utility class
    }

    /**
     * Loads an image from a file.
     *
     * @param file the source file
     * @return the loaded BufferedImage
     * @throws ImageLoadException if the file cannot be read
     */
    static BufferedImage load(File file) throws ImageLoadException {
        LOGGER.debug("Loading image from file: {}", file);
        if (file == null) {
            throw new ImageLoadException("File cannot be null", null, "file");
        }
        if (!file.exists()) {
            throw new ImageLoadException("File does not exist: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), "file");
        }
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new ImageLoadException("Unable to read image file: " + file.getAbsolutePath(), 
                        file.getAbsolutePath(), "file");
            }
            LOGGER.info("Successfully loaded image from file: {} ({}x{})", 
                    file.getName(), image.getWidth(), image.getHeight());
            return image;
        } catch (IOException e) {
            LOGGER.error("Failed to load image from file: {}", file.getAbsolutePath(), e);
            throw new ImageLoadException("Failed to read image file: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), "file", e);
        }
    }

    /**
     * Loads an image from a file path.
     *
     * @param path the file path
     * @return the loaded BufferedImage
     * @throws ImageLoadException if the file cannot be read
     */
    static BufferedImage load(Path path) throws ImageLoadException {
        LOGGER.debug("Loading image from path: {}", path);
        return load(path.toFile());
    }

    /**
     * Loads an image from an InputStream.
     *
     * @param stream the input stream
     * @return the loaded BufferedImage
     * @throws ImageLoadException if the stream cannot be read
     */
    static BufferedImage load(InputStream stream) throws ImageLoadException {
        LOGGER.debug("Loading image from input stream");
        if (stream == null) {
            throw new ImageLoadException("InputStream cannot be null", null, "stream");
        }
        try {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new ImageLoadException("Unable to read image from InputStream", null, "stream");
            }
            LOGGER.info("Successfully loaded image from stream ({}x{})", 
                    image.getWidth(), image.getHeight());
            return image;
        } catch (IOException e) {
            LOGGER.error("Failed to load image from stream", e);
            throw new ImageLoadException("Failed to read image from InputStream", null, "stream", e);
        }
    }

    /**
     * Loads an image from a URL.
     *
     * @param url the source URL
     * @return the loaded BufferedImage
     * @throws ImageLoadException if the URL cannot be read
     */
    static BufferedImage load(URL url) throws ImageLoadException {
        LOGGER.debug("Loading image from URL: {}", url);
        if (url == null) {
            throw new ImageLoadException("URL cannot be null", null, "url");
        }
        try {
            BufferedImage image = ImageIO.read(url);
            if (image == null) {
                throw new ImageLoadException("Unable to read image from URL: " + url, 
                        url.toString(), "url");
            }
            LOGGER.info("Successfully loaded image from URL: {} ({}x{})", 
                    url, image.getWidth(), image.getHeight());
            return image;
        } catch (IOException e) {
            LOGGER.error("Failed to load image from URL: {}", url, e);
            throw new ImageLoadException("Failed to read image from URL: " + url, 
                    url.toString(), "url", e);
        }
    }

    /**
     * Checks if the specified format is supported for reading.
     *
     * @param format the format name
     * @return true if the format is supported
     */
    static boolean isSupportedFormat(String format) {
        if (format == null) {
            return false;
        }
        return ImageIO.getImageReadersBySuffix(format.toLowerCase()).hasNext();
    }

    /**
     * Returns the set of supported image formats.
     *
     * @return a set of supported format names
     */
    static Set<String> getSupportedFormats() {
        Set<String> formats = new HashSet<>();
        String[] suffixes = ImageIO.getReaderFileSuffixes();
        for (String suffix : suffixes) {
            formats.add(suffix.toLowerCase());
        }
        return formats;
    }

    // ==================== Load with Metadata ====================

    /**
     * Loads an image from a file with its metadata.
     *
     * @param file the source file
     * @return the loaded BufferedImage with metadata
     * @throws ImageLoadException if the file cannot be read
     */
    static ImageWithMetadata loadWithMetadata(File file) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from file: {}", file);
        if (file == null) {
            throw new ImageLoadException("File cannot be null", null, "file");
        }
        if (!file.exists()) {
            throw new ImageLoadException("File does not exist: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), "file");
        }

        String format = getFormatFromFile(file);
        ExifMetadata metadata = readMetadata(file);
        
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                throw new ImageLoadException("Unable to read image file: " + file.getAbsolutePath(), 
                        file.getAbsolutePath(), "file");
            }
            LOGGER.info("Successfully loaded image with metadata from file: {} ({}x{})", 
                    file.getName(), image.getWidth(), image.getHeight());
            return new ImageWithMetadata(image, metadata, format);
        } catch (IOException e) {
            LOGGER.error("Failed to load image with metadata from file: {}", file.getAbsolutePath(), e);
            throw new ImageLoadException("Failed to read image file with metadata: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), "file", e);
        }
    }

    /**
     * Loads an image from a file path with its metadata.
     *
     * @param path the file path
     * @return the loaded BufferedImage with metadata
     * @throws ImageLoadException if the file cannot be read
     */
    static ImageWithMetadata loadWithMetadata(Path path) throws ImageLoadException {
        return loadWithMetadata(path.toFile());
    }

    /**
     * Loads an image from an InputStream with its metadata.
     *
     * @param stream the input stream
     * @return the loaded BufferedImage with metadata
     * @throws ImageLoadException if the stream cannot be read
     */
    static ImageWithMetadata loadWithMetadata(InputStream stream) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from input stream");
        if (stream == null) {
            throw new ImageLoadException("InputStream cannot be null", null, "stream");
        }
        // Note: InputStream may not support mark/reset, so metadata reading is limited
        try {
            BufferedImage image = ImageIO.read(stream);
            if (image == null) {
                throw new ImageLoadException("Unable to read image from InputStream", null, "stream");
            }
            LOGGER.info("Successfully loaded image with metadata from stream ({}x{})", 
                    image.getWidth(), image.getHeight());
            return new ImageWithMetadata(image, null, null);
        } catch (IOException e) {
            LOGGER.error("Failed to load image with metadata from stream", e);
            throw new ImageLoadException("Failed to read image from InputStream", null, "stream", e);
        }
    }

    /**
     * Loads an image from a URL with its metadata.
     *
     * @param url the source URL
     * @return the loaded BufferedImage with metadata
     * @throws ImageLoadException if the URL cannot be read
     */
    static ImageWithMetadata loadWithMetadata(URL url) throws ImageLoadException {
        LOGGER.debug("Loading image with metadata from URL: {}", url);
        if (url == null) {
            throw new ImageLoadException("URL cannot be null", null, "url");
        }
        try {
            BufferedImage image = ImageIO.read(url);
            if (image == null) {
                throw new ImageLoadException("Unable to read image from URL: " + url, 
                        url.toString(), "url");
            }
            LOGGER.info("Successfully loaded image with metadata from URL: {} ({}x{})", 
                    url, image.getWidth(), image.getHeight());
            return new ImageWithMetadata(image, null, null);
        } catch (IOException e) {
            LOGGER.error("Failed to load image with metadata from URL: {}", url, e);
            throw new ImageLoadException("Failed to read image from URL: " + url, 
                    url.toString(), "url", e);
        }
    }

    /**
     * Reads EXIF metadata from a file.
     *
     * @param file the source file
     * @return the ExifMetadata, or null if no metadata could be read
     */
    private static ExifMetadata readMetadata(File file) {
        LOGGER.trace("Reading EXIF metadata from file: {}", file);
        try (ImageInputStream iis = ImageIO.createImageInputStream(file)) {
            Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
            if (readers.hasNext()) {
                ImageReader reader = readers.next();
                try {
                    reader.setInput(iis);
                    IIOMetadata imageMetadata = reader.getImageMetadata(0);
                    ExifMetadata exif = new ExifMetadata();
                    exif.setOrientation(exif.readOrientationFromMetadata(imageMetadata));
                    // Note: We're storing just the orientation, not the full metadata
                    // to avoid issues with metadata serialization
                    LOGGER.trace("Successfully read EXIF metadata: orientation={}", exif.getOrientation());
                    return exif;
                } finally {
                    reader.dispose();
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to read EXIF metadata from file: {}", file, e);
        }
        return null;
    }

    private static String getFormatFromFile(File file) {
        if (file == null) {
            return null;
        }
        return ImageFormatUtils.getFormatFromFile(file);
    }
}
