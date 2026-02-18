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

import com.scale4j.exception.ImageProcessException;
import com.scale4j.log.Scale4jLogger;
import com.scale4j.log.Scale4jLoggerFactory;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

/**
 * Operation for resizing images.
 */
public final class ResizeOperation {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(ResizeOperation.class);

    private ResizeOperation() {
        // Utility class
    }

    /**
     * Resizes a BufferedImage to the specified dimensions.
     *
     * @param source       the source image
     * @param targetWidth  the target width
     * @param targetHeight the target height
     * @param mode         the resize mode
     * @param quality      the resize quality
     * @return the resized image
     * @throws ImageProcessException if the resize operation fails
     */
    public static BufferedImage resize(BufferedImage source, int targetWidth, int targetHeight,
                                       ResizeMode mode, ResizeQuality quality) throws ImageProcessException {
        LOGGER.debug("Resizing image: {}x{} -> {}x{} mode: {} quality: {}", 
                source != null ? source.getWidth() : 0, 
                source != null ? source.getHeight() : 0,
                targetWidth, targetHeight, mode, quality);
        
        if (source == null) {
            throw new ImageProcessException("Source image cannot be null", "resize");
        }
        if (targetWidth <= 0 || targetHeight <= 0) {
            throw new ImageProcessException(
                    String.format("Target dimensions must be positive: width=%d, height=%d", targetWidth, targetHeight), 
                    "resize", source.getWidth(), source.getHeight());
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // If dimensions are the same, return the original
        if (sourceWidth == targetWidth && sourceHeight == targetHeight) {
            LOGGER.debug("Source and target dimensions are identical, returning original image");
            return source;
        }

        // Calculate actual dimensions based on mode
        int[] dimensions = calculateDimensions(sourceWidth, sourceHeight, targetWidth, targetHeight, mode);

        LOGGER.debug("Calculated resize dimensions: {}x{}", dimensions[0], dimensions[1]);
        
        try {
            BufferedImage result = scaleImage(source, dimensions[0], dimensions[1], quality);
            LOGGER.info("Successfully resized image: {}x{} -> {}x{}", 
                    sourceWidth, sourceHeight, result.getWidth(), result.getHeight());
            return result;
        } catch (Exception e) {
            LOGGER.error("Failed to resize image: {}x{} -> {}x{}", 
                    sourceWidth, sourceHeight, targetWidth, targetHeight, e);
            throw new ImageProcessException(
                    String.format("Failed to resize image from %dx%d to %dx%d", 
                            sourceWidth, sourceHeight, targetWidth, targetHeight),
                    "resize", sourceWidth, sourceHeight, e);
        }
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
    private static BufferedImage scaleImage(
        BufferedImage source,
        int targetWidth,
        int targetHeight,
        ResizeQuality quality) {
        // Use AffineTransformOp for better performance and quality
        BufferedImage dest = new BufferedImage(targetWidth, targetHeight, source.getType());
        AffineTransform at = AffineTransform.getScaleInstance(
            (double) targetWidth / source.getWidth(),
            (double) targetHeight / source.getHeight()
        );
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_INTERPOLATION,
            getInterpolationHint(quality));
        AffineTransformOp op = new AffineTransformOp(at, hints);
        return op.filter(source, dest);
    }

    /**
     * Returns the appropriate interpolation hint value for the given quality.
     */
    private static Object getInterpolationHint(ResizeQuality quality) {
        switch (quality) {
            case LOW:
                return RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case MEDIUM:
                return RenderingHints.VALUE_INTERPOLATION_BILINEAR;
            case HIGH:
            case ULTRA:
            default:
                return RenderingHints.VALUE_INTERPOLATION_BICUBIC;
        }
    }
}
