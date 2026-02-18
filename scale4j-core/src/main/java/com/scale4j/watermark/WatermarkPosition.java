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
 * Defines the position where a watermark should be placed on the image.
 */
public enum WatermarkPosition {

    /** Top-left corner */
    TOP_LEFT,

    /** Top-center */
    TOP_CENTER,

    /** Top-right corner */
    TOP_RIGHT,

    /** Middle-left */
    MIDDLE_LEFT,

    /** Center of the image */
    CENTER,

    /** Middle-right */
    MIDDLE_RIGHT,

    /** Bottom-left corner */
    BOTTOM_LEFT,

    /** Bottom-center */
    BOTTOM_CENTER,

    /** Bottom-right corner */
    BOTTOM_RIGHT;

    /**
     * Calculates the position coordinates for a watermark without margin.
     * Delegates to {@link ImageWatermarkPositionCalculator} with margin = 0.
     *
     * @param imageWidth the width of the base image
     * @param imageHeight the height of the base image
     * @param watermarkWidth the width of the watermark
     * @param watermarkHeight the height of the watermark
     * @return an array containing [x, y] coordinates
     */
    public int[] calculate(int imageWidth, int imageHeight, int watermarkWidth, int watermarkHeight) {
        return ImageWatermarkPositionCalculator.getInstance().calculate(
            imageWidth, imageHeight, watermarkWidth, watermarkHeight, this, 0);
    }
}
