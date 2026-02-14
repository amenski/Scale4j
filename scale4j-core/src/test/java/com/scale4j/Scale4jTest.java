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
package com.scale4j;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Basic tests for Scale4j.
 */
class Scale4jTest {

    @Test
    void shouldCreateBufferedImage() {
        // Given
        int width = 100;
        int height = 100;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .resize(50, 50)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void shouldResizeWithMode() {
        // Given
        BufferedImage image = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .resize(100, 50, com.scale4j.types.ResizeMode.FIT)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isLessThanOrEqualTo(100);
        assertThat(result.getHeight()).isLessThanOrEqualTo(50);
    }

    @Test
    void shouldRotateImage() {
        // Given
        BufferedImage image = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .rotate(90)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(100);
    }

    @Test
    void shouldPadImage() {
        // Given
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .pad(10, Color.WHITE)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(120);
        assertThat(result.getHeight()).isEqualTo(120);
    }

    @Test
    void shouldCropImage() {
        // Given
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .crop(10, 10, 50, 50)
                .build();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void shouldChainOperations() {
        // Given
        BufferedImage image = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .resize(100, 75)
                .rotate(90)
                .pad(5, Color.BLACK)
                .build();

        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void shouldAddTextWatermark() {
        // Given
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);

        // When
        BufferedImage result = Scale4j.load(image)
                .watermark("Test Watermark")
                .build();

        // Then
        assertThat(result).isNotNull();
    }
}
