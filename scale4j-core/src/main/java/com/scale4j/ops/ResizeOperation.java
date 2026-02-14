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
package com.scale4j.ops;

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;

import java.awt.Image;
import java.awt.image.BufferedImage;

/**
 * Operation for resizing images.
 */
public final class ResizeOperation {

    private ResizeOperation() {
        // Utility class
    }

    /**
     * Resizes a BufferedImage to the specified dimensions.
     *
     * @param source the source image
     * @param targetWidth the target width
     * @param targetHeight the target height
     * @param mode the resize mode
     * @param quality the resize quality
     * @return the resized image
     */
    public static BufferedImage resize(BufferedImage source, int targetWidth, int targetHeight,
                                       ResizeMode mode, ResizeQuality quality) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (targetWidth <= 0 || targetHeight <= 0) {
            throw new IllegalArgumentException("Target dimensions must be positive");
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // If dimensions are the same, return the original
        if (sourceWidth == targetWidth && sourceHeight == targetHeight) {
            return source;
        }

        // Calculate actual dimensions based on mode
        int[] dimensions = calculateDimensions(sourceWidth, sourceHeight, targetWidth, targetHeight, mode);

        return scaleImage(source, dimensions[0], dimensions[1], quality);
    }

    /**
     * Calculates the actual dimensions based on the resize mode.
     */
    private static int[] calculateDimensions(int sourceWidth, int sourceHeight,
                                             int targetWidth, int targetHeight,
                                             ResizeMode mode) {
        double sourceAspect = (double) sourceWidth / sourceHeight;
        double targetAspect = (double) targetWidth / targetHeight;

        int resultWidth;
        int resultHeight;

        switch (mode) {
            case FIT:
                if (sourceAspect > targetAspect) {
                    resultWidth = targetWidth;
                    resultHeight = (int) (targetWidth / sourceAspect);
                } else {
                    resultHeight = targetHeight;
                    resultWidth = (int) (targetHeight * sourceAspect);
                }
                break;

            case FILL:
                if (sourceAspect > targetAspect) {
                    resultHeight = targetHeight;
                    resultWidth = (int) (targetHeight * sourceAspect);
                } else {
                    resultWidth = targetWidth;
                    resultHeight = (int) (targetWidth / sourceAspect);
                }
                break;

            case EXACT:
                resultWidth = targetWidth;
                resultHeight = targetHeight;
                break;

            case AUTOMATIC:
            default:
                // Default to FIT behavior
                if (sourceAspect > targetAspect) {
                    resultWidth = targetWidth;
                    resultHeight = (int) (targetWidth / sourceAspect);
                } else {
                    resultHeight = targetHeight;
                    resultWidth = (int) (targetHeight * sourceAspect);
                }
                break;
        }

        return new int[]{Math.max(1, resultWidth), Math.max(1, resultHeight)};
    }

    /**
     * Scales the image using the appropriate algorithm based on quality.
     */
    private static BufferedImage scaleImage(BufferedImage source, int targetWidth, int targetHeight,
                                            ResizeQuality quality) {
        // Create a new image with the target dimensions
        BufferedImage scaledImage = new BufferedImage(targetWidth, targetHeight, source.getType());

        // Use the best available scaling method
        int smoothingHint = getSmoothingHint(quality);
        scaledImage.getGraphics().setRenderingHint(java.awt.RenderingHints.KEY_INTERPOLATION, smoothingHint);
        scaledImage.getGraphics().setRenderingHint(java.awt.RenderingHints.KEY_RENDERING, java.awt.RenderingHints.VALUE_RENDER_QUALITY);
        scaledImage.getGraphics().setRenderingHint(java.awt.RenderingHints.KEY_ANTIALIASING, java.awt.RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the scaled image
        java.awt.Image scaledInstance = source.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        scaledImage.getGraphics().drawImage(scaledInstance, 0, 0, null);

        return scaledImage;
    }

    /**
     * Returns the appropriate rendering hint based on quality.
     */
    private static java.awt.RenderingHints.Key getSmoothingHint(ResizeQuality quality) {
        switch (quality) {
            case LOW:
                return java.awt.RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case MEDIUM:
                return java.awt.RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case HIGH:
            case ULTRA:
            default:
                return java.awt.RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
    }
}
