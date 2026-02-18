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

import com.scale4j.exception.ImageSaveException;
import com.scale4j.log.Scale4jLogger;
import com.scale4j.log.Scale4jLoggerFactory;
import com.scale4j.metadata.ExifMetadata;
import com.scale4j.metadata.ExifOrientation;
import com.scale4j.metadata.MetadataUtils;
import com.scale4j.util.ImageFormatUtils;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * Utility class for saving images to various formats.
 */
public final class ImageSaver {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(ImageSaver.class);

    private ImageSaver() {
        // Utility class
    }

    /**
     * Writes a BufferedImage to a file.
     *
     * @param image the image to save
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean write(BufferedImage image, File file) throws ImageSaveException {
        LOGGER.debug("Writing image to file: {}", file);
        String format = getFormatFromFile(file);
        return write(image, format, file);
    }

    /**
     * Writes a BufferedImage to a file path.
     *
     * @param image the image to save
     * @param path the target path
     * @return true if the image was saved successfully
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean write(BufferedImage image, Path path) throws ImageSaveException {
        LOGGER.debug("Writing image to path: {}", path);
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
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean write(BufferedImage image, String format, File file) throws ImageSaveException {
        LOGGER.debug("Writing image to file: {} with format: {}", file, format);
        try {
            boolean result = ImageIO.write(image, format, file);
            if (result) {
                LOGGER.info("Successfully wrote image to file: {} ({}x{}) format: {}", 
                        file.getName(), image.getWidth(), image.getHeight(), format);
            } else {
                LOGGER.warn("No writer found for format: {} when saving to file: {}", format, file);
            }
            return result;
        } catch (IOException e) {
            LOGGER.error("Failed to write image to file: {}", file.getAbsolutePath(), e);
            throw new ImageSaveException("Failed to write image to file: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), format, e);
        }
    }

    /**
     * Writes a BufferedImage to an OutputStream.
     *
     * @param image the image to save
     * @param format the image format
     * @param output the output stream
     * @return true if the image was written successfully
     * @throws ImageSaveException if the image cannot be written
     */
    public static boolean write(BufferedImage image, String format, OutputStream output) throws ImageSaveException {
        LOGGER.debug("Writing image to output stream with format: {}", format);
        try {
            boolean result = ImageIO.write(image, format, output);
            if (result) {
                LOGGER.info("Successfully wrote image to output stream ({}x{}) format: {}", 
                        image.getWidth(), image.getHeight(), format);
            }
            return result;
        } catch (IOException e) {
            LOGGER.error("Failed to write image to output stream", e);
            throw new ImageSaveException("Failed to write image to output stream", null, format, e);
        }
    }

    // ==================== Write with Metadata ====================

    /**
     * Writes an ImageWithMetadata to a file.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, File file) throws ImageSaveException {
        LOGGER.debug("Writing image with metadata to file: {}", file);
        String format = getFormatFromFile(file);
        return writeWithMetadata(imageWithMetadata, format, file);
    }

    /**
     * Writes an ImageWithMetadata to a file path.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param path the target path
     * @return true if the image was saved successfully
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, Path path) throws ImageSaveException {
        LOGGER.debug("Writing image with metadata to path: {}", path);
        String format = getFormatFromPath(path);
        return writeWithMetadata(imageWithMetadata, format, path.toFile());
    }

    /**
     * Writes an ImageWithMetadata to a file with the specified format.
     * Note: PNG format may not support EXIF metadata; metadata may be ignored or cause warnings.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param format the image format (e.g., "png", "jpg")
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws ImageSaveException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, String format, File file) throws ImageSaveException {
        BufferedImage image = imageWithMetadata.getImage();
        ExifMetadata metadata = imageWithMetadata.getMetadata();
        
        // If no metadata, use regular write
        if (metadata == null) {
            return write(image, format, file);
        }

        LOGGER.debug("Writing image with metadata to file: {} format: {} orientation: {}", 
                file, format, metadata.getOrientation());
        
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            return writeWithMetadata(image, metadata, format, ios);
        } catch (IOException e) {
            LOGGER.error("Failed to write image with metadata to file: {}", file.getAbsolutePath(), e);
            throw new ImageSaveException("Failed to write image with metadata to file: " + file.getAbsolutePath(), 
                    file.getAbsolutePath(), format, e);
        }
    }

    /**
     * Writes an ImageWithMetadata to an OutputStream.
     * Note: PNG format may not support EXIF metadata; metadata may be ignored or cause warnings.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param format the image format
     * @param output the output stream
     * @return true if the image was written successfully
     * @throws ImageSaveException if the image cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, String format, OutputStream output) throws ImageSaveException {
        BufferedImage image = imageWithMetadata.getImage();
        ExifMetadata metadata = imageWithMetadata.getMetadata();
        
        // If no metadata, use regular write
        if (metadata == null) {
            return write(image, format, output);
        }

        LOGGER.debug("Writing image with metadata to output stream format: {}", format);
        
        try (ImageOutputStream ios = ImageIO.createImageOutputStream(output)) {
            return writeWithMetadata(image, metadata, format, ios);
        } catch (IOException e) {
            LOGGER.error("Failed to write image with metadata to output stream", e);
            throw new ImageSaveException("Failed to write image with metadata to output stream", null, format, e);
        }
    }

    /**
     * Writes an image with EXIF metadata to an ImageOutputStream.
     */
    private static boolean writeWithMetadata(BufferedImage image, ExifMetadata metadata, String format, ImageOutputStream ios) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            // Fall back to regular write if no writer found
            LOGGER.warn("No writer found for format: {}, falling back to default write", format);
            return ImageIO.write(image, format, ios);
        }

        ImageWriter writer = writers.next();
        try {
            writer.setOutput(ios);

            ImageTypeSpecifier type = ImageTypeSpecifier.createFromRenderedImage(image);
            IIOMetadata imageMetadata = writer.getDefaultImageMetadata(type, null);

            // Merge EXIF orientation if supported
            if (imageMetadata != null && imageMetadata.isStandardMetadataFormatSupported()) {
                mergeExifOrientation(imageMetadata, metadata.getOrientation());
            }

            IIOImage iioImage = new IIOImage(image, null, imageMetadata);
            writer.write(iioImage);
            LOGGER.trace("Successfully wrote image with metadata format: {} orientation: {}", format, metadata.getOrientation());
            return true;
        } finally {
            writer.dispose();
        }
    }

    /**
     * Merges EXIF orientation into the image metadata.
     */
    private static void mergeExifOrientation(IIOMetadata metadata, ExifOrientation orientation) {
        MetadataUtils.mergeExifOrientation(metadata, orientation);
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

    static String getFormatFromFile(File file) {
        return ImageFormatUtils.getFormatFromFile(file);
    }

    static String getFormatFromPath(Path path) {
        return ImageFormatUtils.getFormatFromPath(path);
    }
}
