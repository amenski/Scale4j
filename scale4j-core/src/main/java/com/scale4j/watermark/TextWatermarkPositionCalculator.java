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
package com.scale4j.watermark;

/**
 * Position calculator specifically for text watermarks.
 * Handles text-specific adjustments like baseline calculation.
 */
public class TextWatermarkPositionCalculator implements WatermarkPositionCalculator {

    private static final TextWatermarkPositionCalculator INSTANCE = new TextWatermarkPositionCalculator();

    protected TextWatermarkPositionCalculator() {}

    public static TextWatermarkPositionCalculator getInstance() {
        return INSTANCE;
    }

    /**
     * Calculates the rectangle position for text watermark (including margin).
     */
    @Override
    public int[] calculate(int imageWidth, int imageHeight, int watermarkWidth, int watermarkHeight,
                          WatermarkPosition position, int margin) {
        if (imageWidth <= 0 || imageHeight <= 0)
            throw new IllegalArgumentException("Image dimensions must be positive");
        if (watermarkWidth <= 0 || watermarkHeight <= 0)
            throw new IllegalArgumentException("Watermark dimensions must be positive");
        if (margin < 0)
            throw new IllegalArgumentException("Margin must be non-negative");
        if (position == null)
            throw new IllegalArgumentException("Position cannot be null");

        switch (position) {
            case TOP_LEFT:
                return new int[]{margin, margin};
            case TOP_CENTER:
                return new int[]{(imageWidth - watermarkWidth) / 2, margin};
            case TOP_RIGHT:
                return new int[]{imageWidth - watermarkWidth - margin, margin};
            case MIDDLE_LEFT:
                return new int[]{margin, (imageHeight - watermarkHeight) / 2};
            case CENTER:
                return new int[]{(imageWidth - watermarkWidth) / 2, (imageHeight - watermarkHeight) / 2};
            case MIDDLE_RIGHT:
                return new int[]{imageWidth - watermarkWidth - margin, (imageHeight - watermarkHeight) / 2};
            case BOTTOM_LEFT:
                return new int[]{margin, imageHeight - watermarkHeight - margin};
            case BOTTOM_CENTER:
                return new int[]{(imageWidth - watermarkWidth) / 2, imageHeight - watermarkHeight - margin};
            case BOTTOM_RIGHT:
                return new int[]{imageWidth - watermarkWidth - margin, imageHeight - watermarkHeight - margin};
            default:
                throw new IllegalArgumentException("Unsupported position: " + position);
        }
    }

    /**
     * Calculates the drawing position for text within the rectangle.
     *
     * @param rectX the x-coordinate of the rectangle (from calculate method)
     * @param rectY the y-coordinate of the rectangle (from calculate method)
     * @param textWidth the text width
     * @param textHeight the text height
     * @param position the watermark position
     * @param margin the margin used
     * @param ascent the font ascent for baseline calculation
     * @return an array containing [x, y] where x is left edge for drawing, y is baseline
     */
    public int[] calculateTextPosition(int rectX, int rectY, int textWidth, int textHeight,
                                      WatermarkPosition position, int margin, float ascent) {
        if (position == null)
            throw new IllegalArgumentException("Position cannot be null");
        if (textWidth <= 0 || textHeight <= 0)
            throw new IllegalArgumentException("Text dimensions must be positive");
        if (margin < 0)
            throw new IllegalArgumentException("Margin must be non-negative");

        int x = rectX;

        switch (position) {
            case TOP_CENTER:
            case CENTER:
            case BOTTOM_CENTER:
                x = rectX;
                break;
            default:
                break;
        }

        int y = rectY + (int) ascent;

        return new int[]{x, y};
    }

    /**
     * Calculates the background rectangle position relative to text drawing position.
     *
     * @param textX the text drawing x-coordinate
     * @param rectY the rectangle y-coordinate
     * @param textWidth the text width
     * @param textHeight the text height
     * @param margin the margin used
     * @return an array containing [x, y, width, height] for background rectangle
     */
    public int[] calculateBackgroundRect(int textX, int rectY, int textWidth, int textHeight, int margin) {
        if (margin < 0)
            throw new IllegalArgumentException("Margin must be non-negative");
        if (textWidth <= 0 || textHeight <= 0)
            throw new IllegalArgumentException("Text dimensions must be positive");

        if (margin > Integer.MAX_VALUE / 2) {
            throw new IllegalArgumentException("Margin too large for background calculation");
        }
        if (textWidth > Integer.MAX_VALUE - 2 * margin) {
            throw new IllegalArgumentException("Text width with margin would overflow");
        }
        if (textHeight > Integer.MAX_VALUE - 2 * margin) {
            throw new IllegalArgumentException("Text height with margin would overflow");
        }

        return new int[]{
            textX - margin,
            rectY - margin,
            textWidth + 2 * margin,
            textHeight + 2 * margin
        };
    }
}
