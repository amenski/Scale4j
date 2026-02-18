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

import java.awt.image.BufferedImage;

/**
 * A wrapper class that holds a BufferedImage along with its associated metadata.
 * This allows metadata to be preserved through the image processing pipeline.
 */
public final class ImageWithMetadata {

    private final BufferedImage image;
    private final ExifMetadata metadata;
    private final String sourceFormat;

    /**
     * Creates a new ImageWithMetadata with the given image and no metadata.
     *
     * @param image the BufferedImage
     */
    public ImageWithMetadata(BufferedImage image) {
        this(image, null, null);
    }

    /**
     * Creates a new ImageWithMetadata with the given image and metadata.
     *
     * @param image the BufferedImage
     * @param metadata the EXIF metadata (may be null)
     */
    public ImageWithMetadata(BufferedImage image, ExifMetadata metadata) {
        this(image, metadata, null);
    }

    /**
     * Creates a new ImageWithMetadata with the given image, metadata, and source format.
     *
     * @param image the BufferedImage
     * @param metadata the EXIF metadata (may be null)
     * @param sourceFormat the source format (e.g., "jpg", "png") for metadata writing
     */
    public ImageWithMetadata(BufferedImage image, ExifMetadata metadata, String sourceFormat) {
        if (image == null) {
            throw new IllegalArgumentException("Image cannot be null");
        }
        this.image = image;
        this.metadata = metadata;
        this.sourceFormat = sourceFormat;
    }

    /**
     * Returns the underlying BufferedImage.
     *
     * @return the BufferedImage
     */
    public BufferedImage getImage() {
        return image;
    }

    /**
     * Returns the EXIF metadata, or null if no metadata was loaded.
     *
     * @return the ExifMetadata, or null
     */
    public ExifMetadata getMetadata() {
        return metadata;
    }

    /**
     * Returns the source format, or null if not specified.
     *
     * @return the source format string, or null
     */
    public String getSourceFormat() {
        return sourceFormat;
    }

    /**
     * Returns the width of the image.
     *
     * @return the image width
     */
    public int getWidth() {
        return image.getWidth();
    }

    /**
     * Returns the height of the image.
     *
     * @return the image height
     */
    public int getHeight() {
        return image.getHeight();
    }

    /**
     * Returns true if this wrapper has metadata.
     *
     * @return true if metadata is present
     */
    public boolean hasMetadata() {
        return metadata != null;
    }

    /**
     * Creates a new ImageWithMetadata with a different image but the same metadata.
     *
     * @param newImage the new image
     * @return a new ImageWithMetadata with the new image and existing metadata
     */
    public ImageWithMetadata withImage(BufferedImage newImage) {
        return new ImageWithMetadata(newImage, metadata, sourceFormat);
    }

    /**
     * Creates a new ImageWithMetadata with the image auto-rotated based on orientation.
     * If metadata contains orientation information, the image will be rotated accordingly
     * and the orientation will be reset to TOP_LEFT.
     * All other metadata (camera settings, geotags, etc.) is preserved.
     *
     * @return a new ImageWithMetadata with auto-rotated image
     */
    public ImageWithMetadata withAutoRotation() {
        if (metadata == null) {
            return this;
        }
        BufferedImage rotatedImage = metadata.applyAutoRotation(image);
        ExifMetadata newMetadata = metadata.withOrientation(ExifOrientation.TOP_LEFT);
        return new ImageWithMetadata(rotatedImage, newMetadata, sourceFormat);
    }

    @Override
    public String toString() {
        return "ImageWithMetadata{" +
                "width=" + image.getWidth() +
                ", height=" + image.getHeight() +
                ", hasMetadata=" + hasMetadata() +
                ", sourceFormat='" + sourceFormat + '\'' +
                '}';
    }
}
