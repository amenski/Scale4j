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

import java.awt.image.BufferedImage;

/**
 * Operation for cropping images.
 */
public final class CropOperation {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(CropOperation.class);

    private CropOperation() {
        // Utility class
    }

    /**
     * Crops a BufferedImage to the specified region.
     *
     * @param source the source image
     * @param x the x coordinate of the upper-left corner
     * @param y the y coordinate of the upper-left corner
     * @param width the width of the crop region
     * @param height the height of the crop region
     * @return the cropped image
     * @throws ImageProcessException if the crop operation fails
     */
    public static BufferedImage crop(BufferedImage source, int x, int y, int width, int height) throws ImageProcessException {
        LOGGER.debug("Cropping image: x={} y={} width={} height={}", x, y, width, height);
        
        if (source == null) {
            throw new ImageProcessException("Source image cannot be null", "crop");
        }
        if (width <= 0 || height <= 0) {
            throw new ImageProcessException(
                    String.format("Crop dimensions must be positive: width=%d, height=%d", width, height), 
                    "crop", source.getWidth(), source.getHeight());
        }
        if (x < 0 || y < 0 || x + width > source.getWidth() || y + height > source.getHeight()) {
            throw new ImageProcessException(
                    String.format("Crop region (x=%d, y=%d, w=%d, h=%d) exceeds image bounds (%dx%d)",
                            x, y, width, height, source.getWidth(), source.getHeight()),
                    "crop", source.getWidth(), source.getHeight());
        }

        LOGGER.info("Successfully cropped image: {}x{} -> {}x{}", 
                source.getWidth(), source.getHeight(), width, height);
        
        return source.getSubimage(x, y, width, height);
    }
}
