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
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

/**
 * Builder for configuring batch processing operations.
 * Provides a fluent API for specifying images and operations to apply.
 *
 * @author Scale4j
 * @version 5.0.0
 */
public final class BatchProcessorBuilder {

    private List<BufferedImage> images = new ArrayList<>();
    private Function<Scale4jBuilder, Scale4jBuilder> operationChain = builder -> builder;
    private ExecutorService executor;
    private int parallelism = 1;
    private boolean preserveOrder = true;

    // Resize settings
    private ResizeMode resizeMode = ResizeMode.AUTOMATIC;
    private ResizeQuality resizeQuality = ResizeQuality.MEDIUM;

    /**
     * Creates a new BatchProcessorBuilder.
     */
    public BatchProcessorBuilder() {
    }

    // ==================== Image Input Methods ====================

    /**
     * Sets the list of images to process.
     *
     * @param images the list of BufferedImage objects
     * @return this builder
     */
    public BatchProcessorBuilder images(List<BufferedImage> images) {
        this.images = new ArrayList<>(Objects.requireNonNull(images, "Images list cannot be null"));
        return this;
    }

    /**
     * Adds an image to the batch.
     *
     * @param image the BufferedImage to add
     * @return this builder
     */
    public BatchProcessorBuilder addImage(BufferedImage image) {
        this.images.add(Objects.requireNonNull(image, "Image cannot be null"));
        return this;
    }

    /**
     * Loads images from files.
     *
     * @param files the list of image files
     * @return this builder
     * @throws IOException if a file cannot be read
     */
    public BatchProcessorBuilder imagesFromFiles(List<File> files) throws IOException {
        this.images = new ArrayList<>(files.size());
        for (File file : files) {
            this.images.add(ImageLoader.load(file));
        }
        return this;
    }

    /**
     * Loads images from file paths.
     *
     * @param paths the list of image file paths
     * @return this builder
     * @throws IOException if a file cannot be read
     */
    public BatchProcessorBuilder imagesFromPaths(List<Path> paths) throws IOException {
        this.images = new ArrayList<>(paths.size());
        for (Path path : paths) {
            this.images.add(ImageLoader.load(path));
        }
        return this;
    }

    // ==================== Parallelism Settings ====================

    /**
     * Sets the number of parallel threads for processing.
     *
     * @param parallelism the number of parallel threads (default is 1)
     * @return this builder
     */
    public BatchProcessorBuilder parallel(int parallelism) {
        if (parallelism < 1) {
            throw new IllegalArgumentException("Parallelism must be at least 1");
        }
        this.parallelism = parallelism;
        return this;
    }

    /**
     * Enables parallel processing using the specified executor service.
     *
     * @param executor the executor service to use
     * @return this builder
     */
    public BatchProcessorBuilder executor(ExecutorService executor) {
        this.executor = Objects.requireNonNull(executor, "Executor cannot be null");
        return this;
    }

    /**
     * Sets whether to preserve the order of images in the output.
     * When true, output images maintain the same order as input.
     * When false, images may complete in any order (potentially faster).
     *
     * @param preserveOrder whether to preserve order (default is true)
     * @return this builder
     */
    public BatchProcessorBuilder preserveOrder(boolean preserveOrder) {
        this.preserveOrder = preserveOrder;
        return this;
    }

    // ==================== Resize Operations ====================

    /**
     * Resizes all images to the specified dimensions.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @return this builder
     */
    public BatchProcessorBuilder resize(int targetWidth, int targetHeight) {
        return resize(targetWidth, targetHeight, resizeMode, resizeQuality);
    }

    /**
     * Resizes all images to the specified dimensions with the given mode.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param mode the resize mode
     * @return this builder
     */
    public BatchProcessorBuilder resize(int targetWidth, int targetHeight, ResizeMode mode) {
        return resize(targetWidth, targetHeight, mode, resizeQuality);
    }

    /**
     * Resizes all images with full control.
     *
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param mode the resize mode
     * @param quality the resize quality
     * @return this builder
     */
    public BatchProcessorBuilder resize(int targetWidth, int targetHeight, ResizeMode mode, ResizeQuality quality) {
        this.resizeMode = mode;
        this.resizeQuality = quality;
        appendOperation(builder -> builder.resize(targetWidth, targetHeight, mode, quality));
        return this;
    }

    /**
     * Scales all images by the specified factor.
     *
     * @param factor the scale factor
     * @return this builder
     */
    public BatchProcessorBuilder scale(double factor) {
        appendOperation(builder -> builder.scale(factor));
        return this;
    }

    /**
     * Sets the resize mode for subsequent resize operations.
     *
     * @param mode the resize mode
     * @return this builder
     */
    public BatchProcessorBuilder mode(ResizeMode mode) {
        this.resizeMode = mode;
        return this;
    }

    /**
     * Sets the resize quality for subsequent resize operations.
     *
     * @param quality the resize quality
     * @return this builder
     */
    public BatchProcessorBuilder quality(ResizeQuality quality) {
        this.resizeQuality = quality;
        return this;
    }

    // ==================== Crop Operations ====================

    /**
     * Crops all images to the specified region.
     *
     * @param x the x coordinate
     * @param y the y coordinate
     * @param width the width of the crop region
     * @param height the height of the crop region
     * @return this builder
     */
    public BatchProcessorBuilder crop(int x, int y, int width, int height) {
        appendOperation(builder -> builder.crop(x, y, width, height));
        return this;
    }

    /**
     * Crops all images to the specified rectangle.
     *
     * @param rectangle the crop rectangle
     * @return this builder
     */
    public BatchProcessorBuilder crop(Rectangle rectangle) {
        appendOperation(builder -> builder.crop(rectangle));
        return this;
    }

    // ==================== Rotate Operations ====================

    /**
     * Rotates all images by the specified degrees.
     *
     * @param degrees the rotation angle in degrees
     * @return this builder
     */
    public BatchProcessorBuilder rotate(double degrees) {
        appendOperation(builder -> builder.rotate(degrees));
        return this;
    }

    /**
     * Rotates all images by the specified degrees with a background color.
     *
     * @param degrees the rotation angle in degrees
     * @param backgroundColor the background color for empty spaces
     * @return this builder
     */
    public BatchProcessorBuilder rotate(double degrees, Color backgroundColor) {
        appendOperation(builder -> builder.rotate(degrees, backgroundColor));
        return this;
    }

    // ==================== Pad Operations ====================

    /**
     * Pads all images with the specified number of pixels on all sides.
     *
     * @param padding the padding in pixels
     * @return this builder
     */
    public BatchProcessorBuilder pad(int padding) {
        appendOperation(builder -> builder.pad(padding));
        return this;
    }

    /**
     * Pads all images with the specified padding and color.
     *
     * @param padding the padding in pixels
     * @param color the padding color
     * @return this builder
     */
    public BatchProcessorBuilder pad(int padding, Color color) {
        appendOperation(builder -> builder.pad(padding, color));
        return this;
    }

    /**
     * Pads all images with different padding on each side.
     *
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     * @return this builder
     */
    public BatchProcessorBuilder pad(int top, int right, int bottom, int left) {
        appendOperation(builder -> builder.pad(top, right, bottom, left));
        return this;
    }

    /**
     * Pads all images with different padding on each side and a specified color.
     *
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     * @param color the padding color
     * @return this builder
     */
    public BatchProcessorBuilder pad(int top, int right, int bottom, int left, Color color) {
        appendOperation(builder -> builder.pad(top, right, bottom, left, color));
        return this;
    }

    // ==================== Filter Operations ====================

    /**
     * Applies a Gaussian blur filter to all images.
     *
     * @param radius the blur radius (greater than 0)
     * @return this builder
     */
    public BatchProcessorBuilder blur(float radius) {
        appendOperation(builder -> builder.blur(radius));
        return this;
    }

    /**
     * Applies a sharpening filter to all images.
     *
     * @return this builder
     */
    public BatchProcessorBuilder sharpen() {
        appendOperation(builder -> builder.sharpen());
        return this;
    }

    /**
     * Applies a sharpening filter with custom strength to all images.
     *
     * @param strength the sharpening strength (1.0 = normal, higher = stronger)
     * @return this builder
     */
    public BatchProcessorBuilder sharpen(float strength) {
        appendOperation(builder -> builder.sharpen(strength));
        return this;
    }

    /**
     * Converts all images to grayscale.
     *
     * @return this builder
     */
    public BatchProcessorBuilder grayscale() {
        appendOperation(builder -> builder.grayscale());
        return this;
    }

    /**
     * Adjusts the brightness of all images.
     *
     * @param factor the brightness factor (1.0 = no change, 2.0 = twice as bright, 0.5 = half as bright)
     * @return this builder
     */
    public BatchProcessorBuilder brightness(float factor) {
        appendOperation(builder -> builder.brightness(factor));
        return this;
    }

    /**
     * Adjusts the brightness of all images by adding an offset.
     *
     * @param offset the brightness offset (-255 to 255)
     * @return this builder
     */
    public BatchProcessorBuilder brightnessOffset(float offset) {
        appendOperation(builder -> builder.brightnessOffset(offset));
        return this;
    }

    /**
     * Adjusts the contrast of all images.
     *
     * @param factor the contrast factor (1.0 = no change, 2.0 = double contrast, 0.5 = half contrast)
     * @return this builder
     */
    public BatchProcessorBuilder contrast(float factor) {
        appendOperation(builder -> builder.contrast(factor));
        return this;
    }

    /**
     * Flips all images horizontally (left to right).
     *
     * @return this builder
     */
    public BatchProcessorBuilder flip() {
        appendOperation(builder -> builder.flip());
        return this;
    }

    /**
     * Flops all images vertically (top to bottom).
     *
     * @return this builder
     */
    public BatchProcessorBuilder flop() {
        appendOperation(builder -> builder.flop());
        return this;
    }

    // ==================== Watermark Operations ====================

    /**
     * Adds a text watermark to all images.
     *
     * @param text the watermark text
     * @return this builder
     */
    public BatchProcessorBuilder watermark(String text) {
        appendOperation(builder -> builder.watermark(text));
        return this;
    }

    /**
     * Adds a text watermark with the specified options.
     *
     * @param text the watermark text
     * @param position the position
     * @param opacity the opacity (0.0 to 1.0)
     * @return this builder
     */
    public BatchProcessorBuilder watermark(String text, WatermarkPosition position, float opacity) {
        appendOperation(builder -> builder.watermark(text, position, opacity));
        return this;
    }

    /**
     * Adds a watermark to all images.
     *
     * @param watermark the watermark to add
     * @return this builder
     */
    public BatchProcessorBuilder watermark(Watermark watermark) {
        appendOperation(builder -> builder.watermark(watermark));
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
    public BatchProcessorBuilder watermark(BufferedImage watermarkImage, WatermarkPosition position, float opacity) {
        appendOperation(builder -> builder.watermark(watermarkImage, position, opacity));
        return this;
    }

    // ==================== Build Methods ====================

    /**
     * Builds and returns the BatchProcessor.
     *
     * @return the configured BatchProcessor
     */
    public BatchProcessor build() {
        return new BatchProcessor(images, operationChain, executor, parallelism, preserveOrder);
    }

    /**
     * Executes the batch processing synchronously.
     *
     * @return list of processed images
     */
    public List<BufferedImage> execute() {
        return build().execute();
    }

    /**
     * Executes the batch processing asynchronously.
     *
     * @return list of CompletableFuture containing processed images
     */
    public List<CompletableFuture<BufferedImage>> executeAsync() {
        return build().executeAsync();
    }

    /**
     * Executes the batch processing and returns a single CompletableFuture
     * that completes when all images are processed.
     *
     * @return CompletableFuture that completes with list of processed images
     */
    public CompletableFuture<List<BufferedImage>> executeAndJoin() {
        return build().executeAndJoin();
    }

    // ==================== Private Helpers ====================

    private void appendOperation(Function<Scale4jBuilder, Scale4jBuilder> operation) {
        Function<Scale4jBuilder, Scale4jBuilder> previous = operationChain;
        operationChain = builder -> operation.apply(previous.apply(builder));
    }
}
