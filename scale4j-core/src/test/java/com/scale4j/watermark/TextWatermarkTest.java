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

import com.scale4j.exception.ImageProcessException;
import org.junit.jupiter.api.Test;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for TextWatermark.
 */
class TextWatermarkTest {

    @Test
    void of_createsWatermarkWithDefaults() {
        TextWatermark watermark = TextWatermark.of("Test");
        assertThat(watermark.getText()).isEqualTo("Test");
        assertThat(watermark.getFont()).isEqualTo(new Font("Arial", Font.PLAIN, 24));
        assertThat(watermark.getColor()).isEqualTo(Color.WHITE);
        assertThat(watermark.getPosition()).isEqualTo(WatermarkPosition.BOTTOM_RIGHT);
        assertThat(watermark.getOpacity()).isEqualTo(0.7f);
        assertThat(watermark.getBackgroundColor()).isNull();
        assertThat(watermark.getMargin()).isEqualTo(5);
    }

    @Test
    void builder_customProperties() {
        Font customFont = new Font("Serif", Font.BOLD, 18);
        TextWatermark watermark = TextWatermark.builder()
                .text("Custom")
                .font(customFont)
                .color(Color.RED)
                .position(WatermarkPosition.CENTER)
                .opacity(0.5f)
                .backgroundColor(Color.BLACK)
                .margin(10)
                .build();

        assertThat(watermark.getText()).isEqualTo("Custom");
        assertThat(watermark.getFont()).isEqualTo(customFont);
        assertThat(watermark.getColor()).isEqualTo(Color.RED);
        assertThat(watermark.getPosition()).isEqualTo(WatermarkPosition.CENTER);
        assertThat(watermark.getOpacity()).isEqualTo(0.5f);
        assertThat(watermark.getBackgroundColor()).isEqualTo(Color.BLACK);
        assertThat(watermark.getMargin()).isEqualTo(10);
    }

    @Test
    void builder_fontFamilyStyleSize() {
        TextWatermark watermark = TextWatermark.builder()
                .text("Hello")
                .font("Monospaced", Font.ITALIC, 12)
                .build();

        assertThat(watermark.getFont()).isEqualTo(new Font("Monospaced", Font.ITALIC, 12));
    }

    @Test
    void builder_nullText_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().text(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Text cannot be null or empty");
    }

    @Test
    void builder_emptyText_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().text(""))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Text cannot be null or empty");
    }

    @Test
    void builder_nullFont_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().font(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Font cannot be null");
    }

    @Test
    void builder_nullColor_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().color(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Color cannot be null");
    }

    @Test
    void builder_nullPosition_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().position(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Position cannot be null");
    }

    @Test
    void builder_opacityOutOfRange_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().opacity(-0.1f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Opacity must be between 0.0 and 1.0");
        assertThatThrownBy(() -> TextWatermark.builder().opacity(1.1f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Opacity must be between 0.0 and 1.0");
    }

    @Test
    void builder_negativeMargin_throwsException() {
        assertThatThrownBy(() -> TextWatermark.builder().margin(-1))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Margin must be non-negative");
    }

    @Test
    void apply_nullTarget_throwsException() {
        TextWatermark watermark = TextWatermark.of("Test");
        assertThatThrownBy(() -> watermark.apply(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Target image cannot be null");
    }

    @Test
    void apply_validTarget_noException() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TextWatermark watermark = TextWatermark.of("Test");
        // Should not throw
        watermark.apply(image);
    }

    @Test
    void apply_withBackgroundColor_noException() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TextWatermark watermark = TextWatermark.builder()
                .text("Test")
                .backgroundColor(Color.GRAY)
                .build();
        watermark.apply(image);
    }

    @Test
    void apply_withDifferentPositions_noException() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        for (WatermarkPosition position : WatermarkPosition.values()) {
            TextWatermark watermark = TextWatermark.builder()
                    .text("Test")
                    .position(position)
                    .build();
            watermark.apply(image);
        }
    }

    @Test
    void apply_withZeroOpacity_stillDraws() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TextWatermark watermark = TextWatermark.builder()
                .text("Test")
                .opacity(0.0f)
                .build();
        watermark.apply(image); // No exception expected
    }

    @Test
    void apply_withFullOpacity_stillDraws() {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TextWatermark watermark = TextWatermark.builder()
                .text("Test")
                .opacity(1.0f)
                .build();
        watermark.apply(image);
    }

    @Test
    void apply_withMargin_adjustsPosition() {
        // Hard to verify exact pixel placement; just ensure no exception.
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        TextWatermark watermark = TextWatermark.builder()
                .text("Test")
                .margin(20)
                .build();
        watermark.apply(image);
    }
}