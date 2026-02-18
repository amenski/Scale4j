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
import com.scale4j.util.ImageTypeUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Operation for padding images.
 */
public final class PadOperation {

    private static final Scale4jLogger LOGGER = Scale4jLoggerFactory.getInstance().getLogger(PadOperation.class);

    private PadOperation() {
        // Utility class
    }

    /**
     * Pads a BufferedImage with the specified padding on all sides.
     *
     * @param source the source image
     * @param top the top padding
     * @param right the right padding
     * @param bottom the bottom padding
     * @param left the left padding
     * @param color the padding color
     * @return the padded image
     * @throws ImageProcessException if the pad operation fails
     */
    public static BufferedImage pad(BufferedImage source, int top, int right, int bottom, int left, Color color) throws ImageProcessException {
        LOGGER.debug("Padding image: top={} right={} bottom={} left={} color={}", 
                top, right, bottom, left, color);
        
        if (source == null) {
            throw new ImageProcessException("Source image cannot be null", "pad");
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        int newWidth = sourceWidth + left + right;
        int newHeight = sourceHeight + top + bottom;

        LOGGER.debug("Creating padded image: {}x{} -> {}x{}", 
                sourceWidth, sourceHeight, newWidth, newHeight);
        
        try {
            int imageType = ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha());
            BufferedImage padded = new BufferedImage(newWidth, newHeight, imageType);

            Graphics2D g2d = padded.createGraphics();

            // Fill with background color
            if (color != null) {
                g2d.setBackground(color);
                g2d.clearRect(0, 0, newWidth, newHeight);
            }

            // Draw the source image centered
            g2d.drawImage(source, left, top, null);
            g2d.dispose();

            LOGGER.info("Successfully padded image: {}x{} -> {}x{}", 
                    sourceWidth, sourceHeight, newWidth, newHeight);
            
            return padded;
        } catch (Exception e) {
            LOGGER.error("Failed to pad image: {}x{}", sourceWidth, sourceHeight, e);
            throw new ImageProcessException(
                    String.format("Failed to pad image from %dx%d", sourceWidth, sourceHeight),
                    "pad", sourceWidth, sourceHeight, e);
        }
    }
}
