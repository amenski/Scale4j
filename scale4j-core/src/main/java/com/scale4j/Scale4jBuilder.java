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

import com.scale4j.exception.ImageProcessException;
import com.scale4j.exception.ImageSaveException;
import com.scale4j.log.Scale4jLogger;
import com.scale4j.log.Scale4jLoggerFactory;
import com.scale4j.metadata.ExifMetadata;
import com.scale4j.metadata.ExifOrientation;
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

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(Scale4jBuilder.class);

    private final BufferedImage sourceImage;
    private final List<UnaryOperator<BufferedImage>> operations = new ArrayList<>();

    private ResizeMode resizeMode = ResizeMode.AUTOMATIC;
    private ResizeQuality resizeQuality = ResizeQuality.MEDIUM;

    // Metadata tracking
    private ExifMetadata metadata;
    private String sourceFormat;

    Scale4jBuilder(BufferedImage sourceImage) {
        this(sourceImage, null, null);
    }

    Scale4jBuilder(BufferedImage sourceImage, ExifMetadata metadata, String sourceFormat) {
        if (sourceImage == null) {
            throw new ImageProcessException("Source image cannot be null", "constructor");
        }
        LOGGER.debug("Created Scale4jBuilder with source image ({}x{})", 
                sourceImage.getWidth(), sourceImage.getHeight());
        this.sourceImage = sourceImage;
        this.metadata = metadata;
        this.sourceFormat = sourceFormat;
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
        LOGGER.debug("Adding resize operation: {}x{}", targetWidth, targetHeight);
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
        LOGGER.debug("Adding resize operation: {}x{} mode: {}", targetWidth, targetHeight, mode);
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
        LOGGER.debug("Adding resize operation: {}x{} mode: {} quality: {}", targetWidth, targetHeight, mode, quality);
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
        LOGGER.debug("Adding scale operation: {}x", factor);
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
        LOGGER.debug("Setting resize mode: {}", mode);
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
        LOGGER.debug("Setting resize quality: {}", quality);
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
        LOGGER.debug("Adding crop operation: x={} y={} width={} height={}", x, y, width, height);
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
        LOGGER.debug("Adding rotate operation: {} degrees", degrees);
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
        LOGGER.debug("Adding rotate operation: {} degrees with background color", degrees);
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
        LOGGER.debug("Adding pad operation: top={} right={} bottom={} left={} color={}", 
                top, right, bottom, left, color);
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
        LOGGER.debug("Adding watermark operation");
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
        LOGGER.debug("Building image with {} operations", operations.size());
        BufferedImage result = sourceImage;
        for (UnaryOperator<BufferedImage> operation : operations) {
            result = operation.apply(result);
        }
        LOGGER.info("Built image: {}x{}", result.getWidth(), result.getHeight());
        return result;
    }

    /**
     * Builds and returns the processed image with its metadata.
     *
     * @return the processed ImageWithMetadata
     */
    public ImageWithMetadata buildWithMetadata() {
        LOGGER.debug("Building image with metadata");
        BufferedImage result = build();
        return new ImageWithMetadata(result, metadata, sourceFormat);
    }

    /**
     * Returns the metadata associated with the source image.
     *
     * @return the ExifMetadata, or null if no metadata was loaded
     */
    public ExifMetadata getMetadata() {
        return metadata;
    }

    /**
     * Returns the source format.
     *
     * @return the source format string, or null
     */
    public String getSourceFormat() {
        return sourceFormat;
    }

    /**
     * Applies automatic rotation based on EXIF orientation metadata.
     * This will rotate/flip the image according to the orientation tag
     * and reset the orientation to normal (TOP_LEFT).
     *
     * @return this builder
     */
    public Scale4jBuilder autoRotate() {
        LOGGER.debug("Adding auto-rotate operation based on EXIF orientation");
        if (metadata != null) {
            operations.add(image -> {
                ExifMetadata rotatedMeta = new ExifMetadata(ExifOrientation.TOP_LEFT);
                ImageWithMetadata iwm = new ImageWithMetadata(image, metadata, sourceFormat);
                return iwm.withAutoRotation().getImage();
            });
            // Reset metadata to TOP_LEFT after auto-rotation
            metadata = new ExifMetadata(ExifOrientation.TOP_LEFT);
        }
        return this;
    }

    /**
     * Saves the processed image to a file.
     *
     * @param output the output file
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFile(File output) throws ImageSaveException {
        toFile(output.toPath());
    }

    /**
     * Saves the processed image to a file path.
     *
     * @param path the output path
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFile(Path path) throws ImageSaveException {
        LOGGER.debug("Saving image to file: {}", path);
        String format = getFormatFromPath(path);
        try (OutputStream os = Files.newOutputStream(path)) {
            toOutputStream(os, format);
        } catch (ImageSaveException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageSaveException("Failed to save image to file: " + path, path.toString(), 
                    getFormatFromPath(path), e);
        }
    }

    /**
     * Saves the processed image to a file path with the specified format.
     *
     * @param path the output path
     * @param format the image format (e.g., "png", "jpg")
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFile(Path path, String format) throws ImageSaveException {
        LOGGER.debug("Saving image to file: {} with format: {}", path, format);
        try (OutputStream os = Files.newOutputStream(path)) {
            toOutputStream(os, format);
        } catch (ImageSaveException e) {
            throw e;
        } catch (Exception e) {
            throw new ImageSaveException("Failed to save image to file: " + path, path.toString(), 
                    format, e);
        }
    }

    /**
     * Saves the processed image with metadata to a file.
     * This method writes the EXIF metadata to the output file.
     *
     * @param output the output file
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFileWithMetadata(File output) throws ImageSaveException {
        LOGGER.debug("Saving image with metadata to file: {}", output);
        ImageWithMetadata result = buildWithMetadata();
        ImageSaver.writeWithMetadata(result, output);
    }

    /**
     * Saves the processed image with metadata to a file path.
     * This method writes the EXIF metadata to the output file.
     *
     * @param path the output path
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFileWithMetadata(Path path) throws ImageSaveException {
        LOGGER.debug("Saving image with metadata to file: {}", path);
        ImageWithMetadata result = buildWithMetadata();
        ImageSaver.writeWithMetadata(result, path);
    }

    /**
     * Saves the processed image with metadata to a file path with the specified format.
     * This method writes the EXIF metadata to the output file.
     *
     * @param path the output path
     * @param format the image format (e.g., "png", "jpg")
     * @throws ImageSaveException if the file cannot be written
     */
    public void toFileWithMetadata(Path path, String format) throws ImageSaveException {
        LOGGER.debug("Saving image with metadata to file: {} with format: {}", path, format);
        ImageWithMetadata result = buildWithMetadata();
        ImageSaver.writeWithMetadata(result, format, path.toFile());
    }

    /**
     * Writes the processed image to an OutputStream.
     *
     * @param output the output stream
     * @param format the image format
     * @throws ImageSaveException if the image cannot be written
     */
    public void toOutputStream(OutputStream output, String format) throws ImageSaveException {
        LOGGER.debug("Writing image to output stream with format: {}", format);
        BufferedImage result = build();
        String imageFormat = format != null ? format.toLowerCase() : getFormatFromPath(Path.of("output." + format));
        if (!ImageSaver.isWritableFormat(imageFormat)) {
            imageFormat = "png";
        }
        if (!ImageSaver.write(result, imageFormat, output)) {
            throw new ImageSaveException("Failed to write image in format: " + imageFormat, null, imageFormat);
        }
    }

    /**
     * Writes the processed image with metadata to an OutputStream.
     *
     * @param output the output stream
     * @param format the image format
     * @throws ImageSaveException if the image cannot be written
     */
    public void toOutputStreamWithMetadata(OutputStream output, String format) throws ImageSaveException {
        LOGGER.debug("Writing image with metadata to output stream with format: {}", format);
        ImageWithMetadata result = buildWithMetadata();
        ImageSaver.writeWithMetadata(result, format, output);
    }

    /**
     * Returns the processed image as a byte array.
     *
     * @param format the image format
     * @return the image bytes
     * @throws ImageSaveException if the image cannot be written
     */
    public byte[] toByteArray(String format) throws ImageSaveException {
        LOGGER.debug("Converting image to byte array with format: {}", format);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toOutputStream(baos, format);
        return baos.toByteArray();
    }

    /**
     * Returns the processed image with metadata as a byte array.
     *
     * @param format the image format
     * @return the image bytes
     * @throws ImageSaveException if the image cannot be written
     */
    public byte[] toByteArrayWithMetadata(String format) throws ImageSaveException {
        LOGGER.debug("Converting image with metadata to byte array with format: {}", format);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        toOutputStreamWithMetadata(baos, format);
        return baos.toByteArray();
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
