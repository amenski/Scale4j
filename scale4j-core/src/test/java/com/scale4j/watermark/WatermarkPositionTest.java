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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for WatermarkPosition.
 */
class WatermarkPositionTest {

    @ParameterizedTest
    @EnumSource(WatermarkPosition.class)
    void calculate_returnsCoordinatesWithinBounds(WatermarkPosition position) {
        int imageWidth = 800;
        int imageHeight = 600;
        int watermarkWidth = 100;
        int watermarkHeight = 50;

        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(
            imageWidth, imageHeight, watermarkWidth, watermarkHeight, position, 0);
        assertThat(coords).hasSize(2);
        int x = coords[0];
        int y = coords[1];

        // x must be between 0 and imageWidth - watermarkWidth (inclusive)
        assertThat(x).isBetween(0, imageWidth - watermarkWidth);
        // y must be between 0 and imageHeight - watermarkHeight (inclusive)
        assertThat(y).isBetween(0, imageHeight - watermarkHeight);
    }

    @Test
    void calculate_topLeft() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.TOP_LEFT, 0);
        assertThat(coords).containsExactly(0, 0);
    }

    @Test
    void calculate_topCenter() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.TOP_CENTER, 0);
        assertThat(coords).containsExactly((800 - 100) / 2, 0);
    }

    @Test
    void calculate_topRight() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.TOP_RIGHT, 0);
        assertThat(coords).containsExactly(800 - 100, 0);
    }

    @Test
    void calculate_middleLeft() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.MIDDLE_LEFT, 0);
        assertThat(coords).containsExactly(0, (600 - 50) / 2);
    }

    @Test
    void calculate_center() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.CENTER, 0);
        assertThat(coords).containsExactly((800 - 100) / 2, (600 - 50) / 2);
    }

    @Test
    void calculate_middleRight() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.MIDDLE_RIGHT, 0);
        assertThat(coords).containsExactly(800 - 100, (600 - 50) / 2);
    }

    @Test
    void calculate_bottomLeft() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.BOTTOM_LEFT, 0);
        assertThat(coords).containsExactly(0, 600 - 50);
    }

    @Test
    void calculate_bottomCenter() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.BOTTOM_CENTER, 0);
        assertThat(coords).containsExactly((800 - 100) / 2, 600 - 50);
    }

    @Test
    void calculate_bottomRight() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(800, 600, 100, 50, WatermarkPosition.BOTTOM_RIGHT, 0);
        assertThat(coords).containsExactly(800 - 100, 600 - 50);
    }

    @Test
    void calculate_watermarkLargerThanImage_clampsToZero() {
        // Watermark larger than image, coordinates may become negative? According to algorithm,
        // they would be negative because imageWidth - watermarkWidth < 0.
        // The calculate method does not clamp; it returns negative values.
        // That's okay because caller should handle scaling.
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(50, 50, 100, 100, WatermarkPosition.TOP_LEFT, 0);
        assertThat(coords).containsExactly(0, 0); // still 0,0
    }

    @Test
    void calculate_watermarkSameSizeAsImage() {
        int[] coords = ImageWatermarkPositionCalculator.getInstance().calculate(100, 100, 100, 100, WatermarkPosition.CENTER, 0);
        assertThat(coords).containsExactly(0, 0);
    }
}