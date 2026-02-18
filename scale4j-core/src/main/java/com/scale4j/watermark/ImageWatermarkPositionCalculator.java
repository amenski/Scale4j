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
 * Position calculator for image watermarks that handles margins based on position.
 */
public class ImageWatermarkPositionCalculator implements WatermarkPositionCalculator {

    private static final ImageWatermarkPositionCalculator INSTANCE = new ImageWatermarkPositionCalculator();

    protected ImageWatermarkPositionCalculator() {}

    public static ImageWatermarkPositionCalculator getInstance() {
        return INSTANCE;
    }

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
}