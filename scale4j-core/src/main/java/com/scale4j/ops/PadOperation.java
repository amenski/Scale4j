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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * Operation for padding images.
 */
public final class PadOperation {

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
     */
    public static BufferedImage pad(BufferedImage source, int top, int right, int bottom, int left, Color color) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        int newWidth = sourceWidth + left + right;
        int newHeight = sourceHeight + top + bottom;

        BufferedImage padded = new BufferedImage(newWidth, newHeight, source.getType());

        Graphics2D g2d = padded.createGraphics();

        // Fill with background color
        if (color != null) {
            g2d.setBackground(color);
            g2d.clearRect(0, 0, newWidth, newHeight);
        }

        // Draw the source image centered
        g2d.drawImage(source, left, top, null);
        g2d.dispose();

        return padded;
    }
}
