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

import com.scale4j.metadata.ExifMetadata;
import com.scale4j.metadata.ExifOrientation;

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

    // ==================== Write with Metadata ====================

    /**
     * Writes an ImageWithMetadata to a file.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, File file) throws IOException {
        String format = getFormatFromFile(file);
        return writeWithMetadata(imageWithMetadata, format, file);
    }

    /**
     * Writes an ImageWithMetadata to a file path.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param path the target path
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, Path path) throws IOException {
        String format = getFormatFromPath(path);
        return writeWithMetadata(imageWithMetadata, format, path.toFile());
    }

    /**
     * Writes an ImageWithMetadata to a file with the specified format.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param format the image format (e.g., "png", "jpg")
     * @param file the target file
     * @return true if the image was saved successfully
     * @throws IOException if the file cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, String format, File file) throws IOException {
        BufferedImage image = imageWithMetadata.getImage();
        ExifMetadata metadata = imageWithMetadata.getMetadata();
        
        // If no metadata, use regular write
        if (metadata == null) {
            return ImageIO.write(image, format, file);
        }

        try (ImageOutputStream ios = ImageIO.createImageOutputStream(file)) {
            return writeWithMetadata(image, metadata, format, ios);
        }
    }

    /**
     * Writes an ImageWithMetadata to an OutputStream.
     *
     * @param imageWithMetadata the image with metadata to save
     * @param format the image format
     * @param output the output stream
     * @return true if the image was written successfully
     * @throws IOException if the image cannot be written
     */
    public static boolean writeWithMetadata(ImageWithMetadata imageWithMetadata, String format, OutputStream output) throws IOException {
        BufferedImage image = imageWithMetadata.getImage();
        ExifMetadata metadata = imageWithMetadata.getMetadata();
        
        // If no metadata, use regular write
        if (metadata == null) {
            return ImageIO.write(image, format, output);
        }

        ImageOutputStream ios = ImageIO.createImageOutputStream(output);
        try {
            return writeWithMetadata(image, metadata, format, ios);
        } finally {
            ios.close();
        }
    }

    /**
     * Writes an image with EXIF metadata to an ImageOutputStream.
     */
    private static boolean writeWithMetadata(BufferedImage image, ExifMetadata metadata, String format, ImageOutputStream ios) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName(format);
        if (!writers.hasNext()) {
            // Fall back to regular write if no writer found
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
            return true;
        } finally {
            writer.dispose();
        }
    }

    /**
     * Merges EXIF orientation into the image metadata.
     */
    private static void mergeExifOrientation(IIOMetadata metadata, ExifOrientation orientation) {
        if (orientation == null) {
            orientation = ExifOrientation.TOP_LEFT;
        }
        
        try {
            String exifFormat = "http://ns.adobe.com/exif/1.0/";
            IIOMetadataNode root = (IIOMetadataNode) metadata.getAsTree(exifFormat);
            IIOMetadataNode orientationNode = getOrCreateChildNode(root, "Orientation");
            orientationNode.setAttribute("value", String.valueOf(orientation.getTagValue()));
            metadata.mergeTree(exifFormat, root);
        } catch (Exception e) {
            // Silently fail if metadata cannot be merged
        }
    }

    /**
     * Gets or creates a child node with the given name.
     */
    private static IIOMetadataNode getOrCreateChildNode(IIOMetadataNode parent, String nodeName) {
        if (parent == null) {
            return null;
        }
        
        // Try to find existing node
        for (int i = 0; i < parent.getLength(); i++) {
            IIOMetadataNode child = (IIOMetadataNode) parent.item(i);
            if (child.getNodeName().equals(nodeName)) {
                return child;
            }
        }
        
        // Create new node if not found
        IIOMetadataNode newNode = new IIOMetadataNode(nodeName);
        parent.appendChild(newNode);
        return newNode;
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
