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
 * Calculates the position for placing a watermark on an image.
 */
public interface WatermarkPositionCalculator {

    /**
     * Calculates the position coordinates for placing a watermark.
     *
     * @param imageWidth the width of the base image
     * @param imageHeight the height of the base image
     * @param watermarkWidth the width of the watermark
     * @param watermarkHeight the height of the watermark
     * @param position the desired position (e.g., TOP_LEFT, CENTER, etc.)
     * @param margin the margin to apply (positive values)
     * @return an array containing [x, y] coordinates for the watermark rectangle
     */
    int[] calculate(int imageWidth, int imageHeight, int watermarkWidth, int watermarkHeight, 
                    WatermarkPosition position, int margin);
}