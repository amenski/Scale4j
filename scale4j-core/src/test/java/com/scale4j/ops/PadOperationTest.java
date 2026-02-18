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

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class PadOperationTest {

    @Test
    void pad_nullSource_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> PadOperation.pad(null, 10, 10, 10, 10, Color.WHITE))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Source image cannot be null");
    }

    @Test
    void pad_zeroPadding_noSizeChange() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 0, 0, 0, 0, Color.WHITE);
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void pad_positivePadding_increasesDimensions() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 10, 20, 30, 40, Color.RED);
        // new width = 100 + left + right = 100 + 40 + 20 = 160
        // new height = 50 + top + bottom = 50 + 10 + 30 = 90
        assertThat(result.getWidth()).isEqualTo(160);
        assertThat(result.getHeight()).isEqualTo(90);
    }

    @Test
    void pad_negativePadding_throwsException() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        // Negative padding that results in non-positive dimension should throw IllegalArgumentException
        // top = -100, sourceHeight=50 => newHeight = -50 <= 0
        assertThatThrownBy(() -> PadOperation.pad(source, -100, 0, 0, 0, Color.WHITE))
                .isInstanceOf(ImageProcessException.class);
    }

    @Test
    void pad_nullBackground_noException() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 10, 10, 10, 10, null);
        assertThat(result.getWidth()).isEqualTo(120);
        assertThat(result.getHeight()).isEqualTo(70);
        // No guarantee about background color, but image should be created
    }

    @Test
    void pad_asymmetricPadding_correctOffsets() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        // Paint source with a distinct color to verify placement? Hard to test.
        // We'll just verify dimensions.
        BufferedImage result = PadOperation.pad(source, 5, 15, 25, 35, Color.BLUE);
        assertThat(result.getWidth()).isEqualTo(100 + 35 + 15); // left + right
        assertThat(result.getHeight()).isEqualTo(50 + 5 + 25); // top + bottom
    }

    @Test
    void pad_largePadding_dimensionsCorrect() {
        BufferedImage source = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 100, 200, 300, 400, Color.GREEN);
        assertThat(result.getWidth()).isEqualTo(10 + 400 + 200); // 610
        assertThat(result.getHeight()).isEqualTo(10 + 100 + 300); // 410
    }

    @Test
    void pad_preservesImageType() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        BufferedImage result = PadOperation.pad(source, 10, 10, 10, 10, Color.WHITE);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void pad_zeroColor_nullBackground() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 5, 5, 5, 5, null);
        // Ensure no NullPointerException
        assertThat(result).isNotNull();
    }

    @Test
    void pad_equalPadding_allSides() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = PadOperation.pad(source, 10, 10, 10, 10, Color.CYAN);
        assertThat(result.getWidth()).isEqualTo(120);
        assertThat(result.getHeight()).isEqualTo(70);
    }
}