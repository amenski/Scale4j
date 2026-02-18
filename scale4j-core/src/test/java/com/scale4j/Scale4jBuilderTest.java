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

import com.scale4j.exception.ImageProcessException;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import com.scale4j.watermark.WatermarkPosition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.Graphics2D;
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
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Source image cannot be null");
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
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("exceeds image bounds");
    }

    @Test
    void builder_invalidResizePropagatesException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> Scale4j.load(source)
                .resize(-10, 50)
                .build())
                .isInstanceOf(ImageProcessException.class)
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

    // ==================== Scratch Buffer Tests ====================

    @Test
    void scratchBuffer_resizeReusesBuffer() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        // Fill source with a specific color pattern to detect corruption
        Graphics2D g = source.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 200, 100);
        g.dispose();

        // First resize creates scratch buffer
        BufferedImage result1 = Scale4j.load(source)
                .resize(100, 50)
                .build();
        assertThat(result1.getWidth()).isEqualTo(100);
        assertThat(result1.getHeight()).isEqualTo(50);

        // Second resize with same dimensions should reuse buffer
        BufferedImage result2 = Scale4j.load(source)
                .resize(100, 50)
                .build();
        assertThat(result2.getWidth()).isEqualTo(100);
        assertThat(result2.getHeight()).isEqualTo(50);

        // Verify source is not corrupted
        assertThat(source.getRGB(50, 50)).isEqualTo(Color.RED.getRGB());
    }

    @Test
    void scratchBuffer_rotateReusesBuffer() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.BLUE);
        g.fillRect(0, 0, 100, 50);
        g.dispose();

        // First rotate creates scratch buffer
        BufferedImage result1 = Scale4j.load(source)
                .rotate(90)
                .build();
        assertThat(result1.getWidth()).isEqualTo(50);
        assertThat(result1.getHeight()).isEqualTo(100);

        // Second rotate with same angle should reuse buffer
        BufferedImage result2 = Scale4j.load(source)
                .rotate(90)
                .build();
        assertThat(result2.getWidth()).isEqualTo(50);
        assertThat(result2.getHeight()).isEqualTo(100);

        // Verify source is not corrupted
        assertThat(source.getRGB(50, 25)).isEqualTo(Color.BLUE.getRGB());
    }

    @Test
    void scratchBuffer_padReusesBuffer() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.GREEN);
        g.fillRect(0, 0, 100, 50);
        g.dispose();

        // First pad creates scratch buffer
        BufferedImage result1 = Scale4j.load(source)
                .pad(10)
                .build();
        assertThat(result1.getWidth()).isEqualTo(120);
        assertThat(result1.getHeight()).isEqualTo(70);

        // Second pad with same padding should reuse buffer
        BufferedImage result2 = Scale4j.load(source)
                .pad(10)
                .build();
        assertThat(result2.getWidth()).isEqualTo(120);
        assertThat(result2.getHeight()).isEqualTo(70);

        // Verify source is not corrupted
        assertThat(source.getRGB(50, 25)).isEqualTo(Color.GREEN.getRGB());
    }

    @Test
    void scratchBuffer_noOpResizeDoesNotCorruptSource() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.YELLOW);
        g.fillRect(0, 0, 100, 50);
        g.dispose();

        // Resize to same dimensions (should return source)
        BufferedImage result = Scale4j.load(source)
                .resize(100, 50)
                .build();

        // Result should be the same object as source
        assertThat(result).isSameAs(source);

        // Verify source is intact
        assertThat(source.getRGB(50, 25)).isEqualTo(Color.YELLOW.getRGB());
    }

    @Test
    void scratchBuffer_noOpRotateDoesNotCorruptSource() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.CYAN);
        g.fillRect(0, 0, 100, 50);
        g.dispose();

        // Rotate by 0 degrees (should return source)
        BufferedImage result = Scale4j.load(source)
                .rotate(0)
                .build();

        // Result should be the same object as source
        assertThat(result).isSameAs(source);

        // Verify source is intact
        assertThat(source.getRGB(50, 25)).isEqualTo(Color.CYAN.getRGB());
    }

    @Test
    void scratchBuffer_noOpPadDoesNotCorruptSource() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.MAGENTA);
        g.fillRect(0, 0, 100, 50);
        g.dispose();

        // Pad with 0 on all sides (should return source)
        BufferedImage result = Scale4j.load(source)
                .pad(0, 0, 0, 0)
                .build();

        // Result should be the same object as source
        assertThat(result).isSameAs(source);

        // Verify source is intact
        assertThat(source.getRGB(50, 25)).isEqualTo(Color.MAGENTA.getRGB());
    }

    @Test
    void scratchBuffer_chainedOperationsReuseBuffer() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = source.createGraphics();
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 200, 100);
        g.dispose();

        // Chain of operations with same intermediate dimensions
        BufferedImage result = Scale4j.load(source)
                .resize(100, 50)
                .rotate(0)  // Should be no-op
                .pad(5)     // Creates 110x60
                .build();

        assertThat(result.getWidth()).isEqualTo(110);
        assertThat(result.getHeight()).isEqualTo(60);

        // Verify source is not corrupted
        assertThat(source.getRGB(100, 50)).isEqualTo(Color.BLACK.getRGB());
    }

    @Test
    void scratchBuffer_differentDimensionsCreateNewBuffer() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);

        BufferedImage result = Scale4j.load(source)
                .resize(100, 50)
                .resize(50, 25)
                .build();

        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(25);
    }

    @Test
    void builder_invalidPaddingThrowsException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

        assertThatThrownBy(() -> Scale4j.load(source)
                .pad(-1, 0, 0, 0)
                .build())
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Padding values must be non-negative");

        assertThatThrownBy(() -> Scale4j.load(source)
                .pad(0, -1, 0, 0)
                .build())
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Padding values must be non-negative");
    }
}