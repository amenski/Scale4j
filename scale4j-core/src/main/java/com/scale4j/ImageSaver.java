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
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for saving images to various formats.
 */
public final class ImageSaver {

    private ImageSaver() {
        // Utility class
    }

    /**
     * Writes a BufferedImage to a file.
     *
     * @param image the image to save
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean write(BufferedImage image, File file) throws IOException {
        String format = getFormatFromFile(file);
        return write(image, format, file);
    }

    /**
     * Writes a BufferedImage to a file path.
     *
     * @param image the image to save
     * @param path the target path
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean write(BufferedImage image, Path path) throws IOException {
        String format = getFormatFromPath(path);
        return write(image, format, path.toFile());
    }

    /**
     * Writes a BufferedImage to a file with the specified format.
     *
     * @param image the image to save
     * @param format the image format (e.g., "png", "jpg")
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean write(BufferedImage image, String format, File file) throws IOException {
        return ImageIO.write(image, format, file);
    }

    /**
     * Writes a BufferedImage to an OutputStream.
     *
     * @param image the image to save
     * @param format the image format
     * @param output the output stream
     * @return true if the image was written successfully
     */
    public static boolean write(BufferedImage image, String format, OutputStream output) {
        try {
            return ImageIO.write(image, format, output);
        } catch (IOException e) {
            throw new RuntimeException("Failed to write image", e);
        }
    }

    /**
     * Checks if the specified format is writable.
     *
     * @param format the format name
     * @return true if the format is writable
     */
    public static boolean isWritableFormat(String format) {
        if (format == null) {
            return false;
        }
        return ImageIO.getImageWritersBySuffix(format.toLowerCase()).hasNext();
    }

    /**
     * Returns the set of writable image formats.
     *
     * @return a set of writable format names
     */
    public static Set<String> getWritableFormats() {
        Set<String> formats = new HashSet<>();
        String[] suffixes = ImageIO.getWriterFileSuffixes();
        for (String suffix : suffixes) {
            formats.add(suffix.toLowerCase());
        }
        return formats;
    }

    private static String getFormatFromFile(File file) {
        return getFormatFromPath(file.toPath());
    }

    private static String getFormatFromPath(Path path) {
        String pathStr = path.toString();
        int lastDot = pathStr.lastIndexOf('.');
        if (lastDot > 0) {
            return pathStr.substring(lastDot + 1).toLowerCase();
        }
        return "png";
    }
}
