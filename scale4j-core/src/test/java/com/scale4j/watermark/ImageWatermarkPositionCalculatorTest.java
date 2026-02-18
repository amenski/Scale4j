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


class ImageWatermarkPositionCalculatorTest {

    private final ImageWatermarkPositionCalculator calculator = ImageWatermarkPositionCalculator.getInstance();
    private static final int IMAGE_WIDTH = 100;
    private static final int IMAGE_HEIGHT = 100;
    private static final int WATERMARK_WIDTH = 20;
    private static final int WATERMARK_HEIGHT = 10;
    private static final int MARGIN = 5;

    @Test
    void getInstance_returnsSameInstance() {
        ImageWatermarkPositionCalculator instance1 = ImageWatermarkPositionCalculator.getInstance();
        ImageWatermarkPositionCalculator instance2 = ImageWatermarkPositionCalculator.getInstance();
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void calculate_topLeft_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0);
        assertThat(coords).containsExactly(0, 0);
    }

    @Test
    void calculate_topCenter_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_CENTER, 0);
        assertThat(coords).containsExactly((IMAGE_WIDTH - WATERMARK_WIDTH) / 2, 0);
    }

    @Test
    void calculate_topRight_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_RIGHT, 0);
        assertThat(coords).containsExactly(IMAGE_WIDTH - WATERMARK_WIDTH, 0);
    }

    @Test
    void calculate_middleLeft_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.MIDDLE_LEFT, 0);
        assertThat(coords).containsExactly(0, (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_center_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.CENTER, 0);
        assertThat(coords).containsExactly(
                (IMAGE_WIDTH - WATERMARK_WIDTH) / 2,
                (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_middleRight_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.MIDDLE_RIGHT, 0);
        assertThat(coords).containsExactly(IMAGE_WIDTH - WATERMARK_WIDTH, (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_bottomLeft_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_LEFT, 0);
        assertThat(coords).containsExactly(0, IMAGE_HEIGHT - WATERMARK_HEIGHT);
    }

    @Test
    void calculate_bottomCenter_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_CENTER, 0);
        assertThat(coords).containsExactly(
                (IMAGE_WIDTH - WATERMARK_WIDTH) / 2,
                IMAGE_HEIGHT - WATERMARK_HEIGHT);
    }

    @Test
    void calculate_bottomRight_noMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_RIGHT, 0);
        assertThat(coords).containsExactly(IMAGE_WIDTH - WATERMARK_WIDTH, IMAGE_HEIGHT - WATERMARK_HEIGHT);
    }

    @Test
    void calculate_topLeft_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, MARGIN);
        assertThat(coords).containsExactly(MARGIN, MARGIN);
    }

    @Test
    void calculate_topCenter_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_CENTER, MARGIN);
        assertThat(coords).containsExactly((IMAGE_WIDTH - WATERMARK_WIDTH) / 2, MARGIN);
    }

    @Test
    void calculate_topRight_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_RIGHT, MARGIN);
        assertThat(coords).containsExactly(IMAGE_WIDTH - WATERMARK_WIDTH - MARGIN, MARGIN);
    }

    @Test
    void calculate_middleLeft_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.MIDDLE_LEFT, MARGIN);
        assertThat(coords).containsExactly(MARGIN, (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_center_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.CENTER, MARGIN);
        assertThat(coords).containsExactly(
                (IMAGE_WIDTH - WATERMARK_WIDTH) / 2,
                (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_middleRight_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.MIDDLE_RIGHT, MARGIN);
        assertThat(coords).containsExactly(IMAGE_WIDTH - WATERMARK_WIDTH - MARGIN, (IMAGE_HEIGHT - WATERMARK_HEIGHT) / 2);
    }

    @Test
    void calculate_bottomLeft_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_LEFT, MARGIN);
        assertThat(coords).containsExactly(MARGIN, IMAGE_HEIGHT - WATERMARK_HEIGHT - MARGIN);
    }

    @Test
    void calculate_bottomCenter_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_CENTER, MARGIN);
        assertThat(coords).containsExactly(
                (IMAGE_WIDTH - WATERMARK_WIDTH) / 2,
                IMAGE_HEIGHT - WATERMARK_HEIGHT - MARGIN);
    }

    @Test
    void calculate_bottomRight_withMargin() {
        int[] coords = calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.BOTTOM_RIGHT, MARGIN);
        assertThat(coords).containsExactly(
                IMAGE_WIDTH - WATERMARK_WIDTH - MARGIN,
                IMAGE_HEIGHT - WATERMARK_HEIGHT - MARGIN);
    }

    @Test
    void calculate_watermarkLargerThanImage_negativeCoordinates() {
        int[] coords = calculator.calculate(50, 50, 100, 100, WatermarkPosition.CENTER, 0);
        assertThat(coords[0]).isLessThan(0);
        assertThat(coords[1]).isLessThan(0);
    }

    @Test
    void calculate_largeMargin_negativeCoordinates() {
        int[] coords = calculator.calculate(100, 100, 20, 10, WatermarkPosition.BOTTOM_RIGHT, 100);
        assertThat(coords[0]).isLessThan(0);
        assertThat(coords[1]).isLessThan(0);
    }

    @Test
    void calculate_zeroImageWidth_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(0, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image dimensions must be positive");
    }

    @Test
    void calculate_zeroImageHeight_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(IMAGE_WIDTH, 0, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image dimensions must be positive");
    }

    @Test
    void calculate_negativeImageWidth_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(-10, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Image dimensions must be positive");
    }

    @Test
    void calculate_zeroWatermarkWidth_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, 0, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Watermark dimensions must be positive");
    }

    @Test
    void calculate_zeroWatermarkHeight_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, 0,
                WatermarkPosition.TOP_LEFT, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Watermark dimensions must be positive");
    }

    @Test
    void calculate_negativeMargin_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                WatermarkPosition.TOP_LEFT, -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Margin must be non-negative");
    }

    @Test
    void calculate_unsupportedPosition_throwsException() {
        assertThatThrownBy(() -> calculator.calculate(IMAGE_WIDTH, IMAGE_HEIGHT, WATERMARK_WIDTH, WATERMARK_HEIGHT,
                null, 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Position cannot be null");
    }
}
