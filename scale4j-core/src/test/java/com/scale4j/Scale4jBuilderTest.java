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

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.WatermarkPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class Scale4jBuilderTest {

    @TempDir
    Path tempDir;

    @Test
    void builder_nullSource_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> Scale4j.load((BufferedImage) null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Source image cannot be null");
    }

    @Test
    void builder_resize_dimensionsCorrect() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .resize(100, 50)
                .build();
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void builder_resizeWithModeAndQuality() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .mode(ResizeMode.FIT)
                .quality(ResizeQuality.HIGH)
                .resize(150, 150)
                .build();
        // FIT with aspect ratio 2:1, target square => width 150, height 75
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(75);
    }

    @Test
    void builder_crop() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .crop(10, 10, 50, 50)
                .build();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void builder_cropRectangle() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .crop(new Rectangle(10, 10, 50, 50))
                .build();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void builder_rotate() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .rotate(90)
                .build();
        // 90-degree rotation swaps dimensions
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(100);
    }

    @Test
    void builder_rotateWithBackground() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .rotate(45, Color.RED)
                .build();
        assertThat(result).isNotNull();
        // Dimensions increase due to rotation
        assertThat(result.getWidth()).isGreaterThan(100);
        assertThat(result.getHeight()).isGreaterThan(50);
    }

    @Test
    void builder_pad() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .pad(10, Color.WHITE)
                .build();
        assertThat(result.getWidth()).isEqualTo(120); // left+right = 10+10
        assertThat(result.getHeight()).isEqualTo(70); // top+bottom = 10+10
    }

    @Test
    void builder_padAsymmetric() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .pad(5, 10, 15, 20, Color.BLUE)
                .build();
        assertThat(result.getWidth()).isEqualTo(100 + 20 + 10); // left + right
        assertThat(result.getHeight()).isEqualTo(50 + 5 + 15); // top + bottom
    }

    @Test
    void builder_watermarkText() {
        BufferedImage source = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .watermark("Test")
                .build();
        assertThat(result).isNotNull();
        // Watermark applied, no easy assertion
    }

    @Test
    void builder_watermarkTextWithOptions() {
        BufferedImage source = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .watermark("Hello", WatermarkPosition.BOTTOM_RIGHT, 0.5f)
                .build();
        assertThat(result).isNotNull();
    }

    @Test
    void builder_chainMultipleOperations() {
        BufferedImage source = new BufferedImage(200, 150, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .resize(100, 75)
                .rotate(90)
                .pad(5, Color.BLACK)
                .build();
        // After resize: 100x75, rotate 90 => 75x100, pad 5 each side => 85x110
        assertThat(result.getWidth()).isEqualTo(85);
        assertThat(result.getHeight()).isEqualTo(110);
    }

    @Test
    void builder_toOutputStream() throws IOException {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        Scale4j.load(source)
                .resize(50, 50)
                .toOutputStream(out, "png");
        assertThat(out.toByteArray()).isNotEmpty();
    }

    @Test
    void builder_toFile() throws IOException {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Path outputFile = tempDir.resolve("output.png");
        Scale4j.load(source)
                .resize(50, 50)
                .toFile(outputFile);
        assertThat(outputFile).exists();
    }

    @Test
    void builder_toByteArray() throws IOException {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        byte[] bytes = Scale4j.load(source)
                .resize(50, 50)
                .toByteArray("png");
        assertThat(bytes).isNotEmpty();
    }

    @Test
    void builder_invalidCropPropagatesException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        // Crop region out of bounds
        assertThatThrownBy(() -> Scale4j.load(source)
                .crop(90, 90, 20, 20)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Crop region exceeds image bounds");
    }

    @Test
    void builder_invalidResizePropagatesException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> Scale4j.load(source)
                .resize(-10, 50)
                .build())
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Target dimensions must be positive");
    }

    @Test
    void builder_scaleFactor() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = Scale4j.load(source)
                .scale(0.5)
                .build();
        // Half size
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }
}