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

import com.scale4j.ops.CropOperation;
import com.scale4j.ops.PadOperation;
import com.scale4j.ops.ResizeOperation;
import com.scale4j.ops.RotateOperation;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.ImageWatermark;
import com.scale4j.watermark.TextWatermark;
import com.scale4j.watermark.Watermark;
import com.scale4j.watermark.WatermarkPosition;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.function.UnaryOperator;

/**
 * Fluent builder for composing image processing operations.
 * All methods return the builder for method chaining.
 */
public final class Scale4jBuilder {

    private final BufferedImage sourceImage;
    private final List<UnaryOperator<BufferedImage>> operations = new ArrayList<>();

    private ResizeMode resizeMode = ResizeMode.AUTOMATIC;
    private ResizeQuality resizeQuality = ResizeQuality.MEDIUM;

    Scale4jBuilder(BufferedImage sourceImage) {
        if (sourceImage == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        this.sourceImage = sourceImage;
    }

    // ==================== Resize Operations ====================

    /**
     * Resizes the image to the specified dimensions using the current mode and quality.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @return this builder
     */
    public Scale4jBuilder resize(int targetWidth, int targetHeight) {
        operations.add(image -> ResizeOperation.resize(image, targetWidth, targetHeight, resizeMode, resizeQuality));
        return this;
    }

    /**
     * Resizes the image using the specified mode.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param mode the resize mode
     * @return this builder
     */
    public Scale4jBuilder resize(int targetWidth, int targetHeight, ResizeMode mode) {
        this.resizeMode = mode;
        operations.add(image -> ResizeOperation.resize(image, targetWidth, targetHeight, mode, resizeQuality));
        return this;
    }

    /**
     * Resizes the image using the specified mode and quality.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param mode the resize mode
     * @param quality the resize quality
     * @return this builder
     */
    public Scale4jBuilder resize(int targetWidth, int targetHeight, ResizeMode mode, ResizeQuality quality) {
        this.resizeMode = mode;
        this.resizeQuality = quality;
        operations.add(image -> ResizeOperation.resize(image, targetWidth, targetHeight, mode, quality));
        return this;
    }

    /**
     * Scales the image by the specified factor.
     *
     * @param factor the scale factor (e.g., 0.5 for half size, 2.0 for double)
     * @return this builder
     */
    public Scale4jBuilder scale(double factor) {
        operations.add(image -> {
            int width = (int) (image.getWidth() * factor);
            int height = (int) (image.getHeight() * factor);
            return ResizeOperation.resize(image, width, height, resizeMode, resizeQuality);
        });
        return this;
    }

    /**
     * Sets the resize mode for subsequent resize operations.
     *
     * @param mode the resize mode
     * @return this builder
     */
    public Scale4jBuilder mode(ResizeMode mode) {
        this.resizeMode = mode;
        return this;
    }

    /**
     * Sets the resize quality for subsequent resize operations.
     *
     * @param quality the resize quality
     * @return this builder
     */
    public Scale4jBuilder quality(ResizeQuality quality) {
        this.resizeQuality = quality;
        return this;
    }

    // ==================== Crop Operations ====================

    /**
     * Crops the image to the specified region.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the crop region
     * @param height the height of the crop region
     * @return this builder
     */
    public Scale4jBuilder crop(int x, int y, int width, int height) {
        operations.add(image -> CropOperation.crop(image, x, y, width, height));
        return this;
    }

    /**
     * Crops the image to the specified rectangle.
     *
     * @param rectangle the crop rectangle
     * @return this builder
     */
    public Scale4jBuilder crop(Rectangle rectangle) {
        return crop(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
    }

    // ==================== Rotate Operations ====================

    /**
     * Rotates the image by the specified degrees.
     *
     * @param degrees the rotation angle in degrees
     * @return this builder
     */
    public Scale4jBuilder rotate(double degrees) {
        operations.add(image -> RotateOperation.rotate(image, degrees));
        return this;
    }

    /**
     * Rotates the image by the specified degrees with a background color.
     *
     * @param degrees the rotation angle in degrees
     * @param backgroundColor the background color for empty spaces
     * @return this builder
     */
    public Scale4jBuilder rotate(double degrees, Color backgroundColor) {
        operations.add(image -> RotateOperation.rotate(image, degrees, backgroundColor));
        return this;
    }

    // ==================== Pad Operations ====================

    /**
     * Pads the image with the specified number of pixels on all sides.
     *
     * @param padding the padding in pixels
     * @return this builder
     */
    public Scale4jBuilder pad(int padding) {
        return pad(padding, padding, padding, padding);
    }

    /**
     * Pads the image with the specified padding on all sides and color.
     *
     * @param padding the padding in pixels
     * @param color the padding color
     * @return this builder
     */
    public Scale4jBuilder pad(int padding, Color color) {
        return pad(padding, padding, padding, padding, color);
    }

    /**
     * Pads the image with different padding on each side.
     *
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     * @return this builder
     */
    public Scale4jBuilder pad(int top, int right, int bottom, int left) {
        return pad(top, right, bottom, left, Color.WHITE);
    }

    /**
     * Pads the image with different padding on each side and a specified color.
     *
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     * @param color the padding color
     * @return this builder
     */
    public Scale4jBuilder pad(int top, int right, int bottom, int left, Color color) {
        operations.add(image -> PadOperation.pad(image, top, right, bottom, left, color));
        return this;
    }

    // ==================== Watermark Operations ====================

    /**
     * Adds a text watermark to the image.
     *
     * @param text the watermark text
     * @return this builder
     */
    public Scale4jBuilder watermark(String text) {
        return watermark(TextWatermark.of(text));
    }

    /**
     * Adds a text watermark with the specified options.
     *
     * @param text the watermark text
     * @param position the position
     * @param opacity the opacity (0.0 to 1.0)
     * @return this builder
     */
    public Scale4jBuilder watermark(String text, WatermarkPosition position, float opacity) {
        return watermark(TextWatermark.builder()
                .text(text)
                .position(position)
                .opacity(opacity)
                .build());
    }

    /**
     * Adds a watermark to the image.
     *
     * @param watermark the watermark to add
     * @return this builder
     */
    public Scale4jBuilder watermark(Watermark watermark) {
        operations.add(image -> {
            watermark.apply(image);
            return image;
        });
        return this;
    }

    /**
     * Adds an image watermark.
     *
     * @param watermarkImage the watermark image
     * @param position the position
     * @param opacity the opacity (0.0 to 1.0)
     * @return this builder
     */
    public Scale4jBuilder watermark(BufferedImage watermarkImage, WatermarkPosition position, float opacity) {
        return watermark(ImageWatermark.builder()
                .image(watermarkImage)
                .position(position)
                .opacity(opacity)
                .build());
    }

    // ==================== Output Operations ====================

    /**
     * Builds and returns the processed image.
     *
     * @return the processed BufferedImage
     */
    public BufferedImage build() {
        BufferedImage result = sourceImage;
        for (UnaryOperator<BufferedImage> operation : operations) {
            result = operation.apply(result);
        }
        return result;
    }

    /**
     * Saves the processed image to a file.
     *
     * @param output the output file
     * @throws IOException if the file cannot be written
     */
    public void toFile(File output) throws IOException {
        toFile(output.toPath());
    }

    /**
     * Saves the processed image to a file path.
     *
     * @param path the output path
     * @throws IOException if the file cannot be written
     */
    public void toFile(Path path) throws IOException {
        String format = getFormatFromPath(path);
        try (OutputStream os = Files.newOutputStream(path)) {
            toOutputStream(os, format);
        }
    }

    /**
     * Saves the processed image to a file path with the specified format.
     *
     * @param path the output path
     * @param format the image format (e.g., "png", "jpg")
     * @throws IOException if the file cannot be written
     */
    public void toFile(Path path, String format) throws IOException {
        try (OutputStream os = Files.newOutputStream(path)) {
            toOutputStream(os, format);
        }
    }

    /**
     * Writes the processed image to an OutputStream.
     *
     * @param output the output stream
     * @param format the image format
     * @throws IOException if the image cannot be written
     */
    public void toOutputStream(OutputStream output, String format) throws IOException {
        BufferedImage result = build();
        String imageFormat = format != null ? format.toLowerCase() : getFormatFromPath(Path.of("output." + format));
        if (!ImageSaver.isWritableFormat(imageFormat)) {
            imageFormat = "png";
        }
        if (!ImageSaver.write(result, imageFormat, output)) {
            throw new IOException("Failed to write image in format: " + imageFormat);
        }
    }

    /**
     * Returns the processed image as a byte array.
     *
     * @param format the image format
     * @return the image bytes
     * @throws IOException if the image cannot be written
     */
    public byte[] toByteArray(String format) throws IOException {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            toOutputStream(baos, format);
            return baos.toByteArray();
        }
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
