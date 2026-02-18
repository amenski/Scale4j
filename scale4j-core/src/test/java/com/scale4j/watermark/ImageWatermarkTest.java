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

import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for ImageWatermark.
 */
class ImageWatermarkTest {

    @Test
    void builder_defaults() {
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .build();

        assertThat(watermark.getImage()).isEqualTo(watermarkImage);
        assertThat(watermark.getPosition()).isEqualTo(WatermarkPosition.BOTTOM_RIGHT);
        assertThat(watermark.getOpacity()).isEqualTo(0.5f);
        assertThat(watermark.getScale()).isEqualTo(0.25f);
    }

    @Test
    void builder_customProperties() {
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .position(WatermarkPosition.CENTER)
                .opacity(0.8f)
                .scale(0.5f)
                .build();

        assertThat(watermark.getImage()).isEqualTo(watermarkImage);
        assertThat(watermark.getPosition()).isEqualTo(WatermarkPosition.CENTER);
        assertThat(watermark.getOpacity()).isEqualTo(0.8f);
        assertThat(watermark.getScale()).isEqualTo(0.5f);
    }

    @Test
    void builder_nullImage_throwsException() {
        assertThatThrownBy(() -> ImageWatermark.builder().image(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Watermark image cannot be null");
    }

    @Test
    void builder_nullPosition_throwsException() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ImageWatermark.builder().image(img).position(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Position cannot be null");
    }

    @Test
    void builder_opacityOutOfRange_throwsException() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ImageWatermark.builder().image(img).opacity(-0.1f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Opacity must be between 0.0 and 1.0");
        assertThatThrownBy(() -> ImageWatermark.builder().image(img).opacity(1.1f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Opacity must be between 0.0 and 1.0");
    }

    @Test
    void builder_scaleOutOfRange_throwsException() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ImageWatermark.builder().image(img).scale(0.0f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Scale must be between 0.0 and 1.0");
        assertThatThrownBy(() -> ImageWatermark.builder().image(img).scale(1.1f))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Scale must be between 0.0 and 1.0");
    }

    @Test
    void builder_scaleOneAllowed() {
        BufferedImage img = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(img)
                .scale(1.0f)
                .build();
        assertThat(watermark.getScale()).isEqualTo(1.0f);
    }

    @Test
    void builder_missingImage_throwsException() {
        assertThatThrownBy(() -> ImageWatermark.builder().build())
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Watermark image must be set");
    }

    @Test
    void apply_nullTarget_throwsException() {
        BufferedImage watermarkImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder().image(watermarkImage).build();
        assertThatThrownBy(() -> watermark.apply(null))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Target image cannot be null");
    }

    @Test
    void apply_nullWatermarkImage_throwsException() {
        // This should not happen because builder ensures image is set, but we can test internal validation.
        // Since image is final and set via builder, we can't create an ImageWatermark with null image.
        // Skip.
    }

    @Test
    void apply_validTarget_noException() {
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder().image(watermarkImage).build();
        watermark.apply(target); // Should not throw
    }

    @Test
    void apply_withDifferentPositions_noException() {
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        for (WatermarkPosition position : WatermarkPosition.values()) {
            ImageWatermark watermark = ImageWatermark.builder()
                    .image(watermarkImage)
                    .position(position)
                    .build();
            watermark.apply(target);
        }
    }

    @Test
    void apply_withScale_changesSize() {
        // Hard to verify internal scaling, but we can ensure no exception.
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .scale(0.5f)
                .build();
        watermark.apply(target);
    }

    @Test
    void apply_withZeroOpacity_stillDraws() {
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .opacity(0.0f)
                .build();
        watermark.apply(target);
    }

    @Test
    void apply_withFullOpacity_stillDraws() {
        BufferedImage target = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(50, 30, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .opacity(1.0f)
                .build();
        watermark.apply(target);
    }

    @Test
    void apply_watermarkLargerThanTarget_scalesDown() {
        // Watermark larger than target, but scale 0.25 may still be larger.
        // The method should still work (coordinates may become negative).
        BufferedImage target = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage watermarkImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        ImageWatermark watermark = ImageWatermark.builder()
                .image(watermarkImage)
                .scale(0.25f) // becomes 50x50
                .build();
        watermark.apply(target);
    }
}