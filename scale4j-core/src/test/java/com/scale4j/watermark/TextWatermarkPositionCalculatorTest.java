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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;


class TextWatermarkPositionCalculatorTest {

    private final TextWatermarkPositionCalculator calculator = TextWatermarkPositionCalculator.getInstance();
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    private static final int TEXT_WIDTH = 50;
    private static final int TEXT_HEIGHT = 20;
    private static final int MARGIN = 5;
    private static final float ASCENT = 15.0f;

    @Test
    void getInstance_returnsSameInstance() {
        TextWatermarkPositionCalculator instance1 = TextWatermarkPositionCalculator.getInstance();
        TextWatermarkPositionCalculator instance2 = TextWatermarkPositionCalculator.getInstance();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void calculate_returnsCorrectPositionForTopLeft() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0);
        assertThat(coords[0]).isEqualTo(0);
        assertThat(coords[1]).isEqualTo(0);
    }

    @Test
    void calculate_withMargin_appliesMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, MARGIN);
        assertThat(coords[0]).isEqualTo(MARGIN);
        assertThat(coords[1]).isEqualTo(MARGIN);
    }

    @Test
    void calculateTextPosition_leftAligned_returnsRectX() {
        int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0);
        int[] textCoords = calculator.calculateTextPosition(
                rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, MARGIN, ASCENT);
        assertThat(textCoords[0]).isEqualTo(rectCoords[0]);
    }

    @Test
    void calculateTextPosition_centerAligned_returnsCorrectX() {
        int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_CENTER, 0);
        int[] textCoords = calculator.calculateTextPosition(
                rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_CENTER, MARGIN, ASCENT);
        assertThat(textCoords[0]).isEqualTo(rectCoords[0]);
    }

    @Test
    void calculateTextPosition_rightAligned_returnsRectX() {
        int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_RIGHT, 0);
        int[] textCoords = calculator.calculateTextPosition(
                rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_RIGHT, MARGIN, ASCENT);
        assertThat(textCoords[0]).isEqualTo(rectCoords[0]);
    }

    @Test
    void calculateTextPosition_allPositions_useRectX() {
        for (WatermarkPosition position : WatermarkPosition.values()) {
            int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                    position, 0);
            int[] textCoords = calculator.calculateTextPosition(
                    rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                    position, MARGIN, ASCENT);
            assertThat(textCoords[0]).isEqualTo(rectCoords[0]);
        }
    }

    @Test
    void calculateTextPosition_yIsBaseline() {
        int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0);
        int[] textCoords = calculator.calculateTextPosition(
                rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, MARGIN, ASCENT);
        int expectedY = rectCoords[1] + (int) Math.ceil(ASCENT);
        assertThat(textCoords[1]).isEqualTo(expectedY);
    }

    @Test
    void calculateTextPosition_unsupportedPosition_throwsException() {
        int[] rectCoords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0);
        assertThatThrownBy(() -> calculator.calculateTextPosition(
                rectCoords[0], rectCoords[1], TEXT_WIDTH, TEXT_HEIGHT,
                null, MARGIN, ASCENT))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Position cannot be null");
    }

    @Test
    void calculateBackgroundRect_returnsCorrectDimensions() {
        int textX = 10;
        int rectY = 20;
        int[] bgRect = calculator.calculateBackgroundRect(textX, rectY, TEXT_WIDTH, TEXT_HEIGHT, MARGIN);
        assertThat(bgRect[0]).isEqualTo(textX - MARGIN);
        assertThat(bgRect[1]).isEqualTo(rectY - MARGIN);
        assertThat(bgRect[2]).isEqualTo(TEXT_WIDTH + 2 * MARGIN);
        assertThat(bgRect[3]).isEqualTo(TEXT_HEIGHT + 2 * MARGIN);
    }

    @Test
    void calculateBackgroundRect_zeroMargin_noOffset() {
        int textX = 10;
        int rectY = 20;
        int[] bgRect = calculator.calculateBackgroundRect(textX, rectY, TEXT_WIDTH, TEXT_HEIGHT, 0);
        assertThat(bgRect[0]).isEqualTo(textX);
        assertThat(bgRect[1]).isEqualTo(rectY);
        assertThat(bgRect[2]).isEqualTo(TEXT_WIDTH);
        assertThat(bgRect[3]).isEqualTo(TEXT_HEIGHT);
    }

    @Test
    void calculate_invalidImageWidth_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(0, IMAGE_HEIGHT, TEXT_WIDTH, TEXT_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image dimensions must be positive");
    }
}
