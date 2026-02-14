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

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for loading images from various sources.
 */
final class ImageLoader {

    private ImageLoader() {
        // Utility class
    }

    /**
     * Loads an image from a file.
     *
     * @param file the source file
     * @return the loaded BufferedImage
     * @throws IOException if the file cannot be read
     */
    static BufferedImage load(File file) throws IOException {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }
        if (!file.exists()) {
            throw new IOException("File does not exist: " + file.getAbsolutePath());
        }
        BufferedImage image = ImageIO.read(file);
        if (image == null) {
            throw new IOException("Unable to read image file: " + file.getAbsolutePath());
        }
        return image;
    }

    /**
     * Loads an image from a file path.
     *
     * @param path the file path
     * @return the loaded BufferedImage
     * @throws IOException if the file cannot be read
     */
    static BufferedImage load(Path path) throws IOException {
        return load(path.toFile());
    }

    /**
     * Loads an image from an InputStream.
     *
     * @param stream the input stream
     * @return the loaded BufferedImage
     * @throws IOException if the stream cannot be read
     */
    static BufferedImage load(InputStream stream) throws IOException {
        if (stream == null) {
            throw new IllegalArgumentException("InputStream cannot be null");
        }
        BufferedImage image = ImageIO.read(stream);
        if (image == null) {
            throw new IOException("Unable to read image from InputStream");
        }
        return image;
    }

    /**
     * Loads an image from a URL.
     *
     * @param url the source URL
     * @return the loaded BufferedImage
     * @throws IOException if the URL cannot be read
     */
    static BufferedImage load(URL url) throws IOException {
        if (url == null) {
            throw new IllegalArgumentException("URL cannot be null");
        }
        BufferedImage image = ImageIO.read(url);
        if (image == null) {
            throw new IOException("Unable to read image from URL: " + url);
        }
        return image;
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
}
