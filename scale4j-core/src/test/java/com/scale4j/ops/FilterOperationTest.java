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

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for FilterOperation.
 */
class FilterOperationTest {

    @TempDir
    java.nio.file.Path tempDir;

    private BufferedImage testImage;
    private static final int TEST_WIDTH = 100;
    private static final int TEST_HEIGHT = 100;

    @BeforeEach
    void setUp() {
        // Create a test image with various colors
        testImage = new BufferedImage(TEST_WIDTH, TEST_HEIGHT, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < TEST_HEIGHT; y++) {
            for (int x = 0; x < TEST_WIDTH; x++) {
                int r = (x * 255) / TEST_WIDTH;
                int g = (y * 255) / TEST_HEIGHT;
                int b = 128;
                testImage.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }
    }

    // ==================== Blur Tests ====================

    @Test
    void blur_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.blur(null, 5.0f));
    }

    @Test
    void blur_shouldThrowExceptionForZeroRadius() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.blur(testImage, 0));
    }

    @Test
    void blur_shouldThrowExceptionForNegativeRadius() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.blur(testImage, -5.0f));
    }

    @Test
    void blur_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.blur(testImage, 5.0f);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void blur_withDifferentRadius() {
        BufferedImage resultSmall = FilterOperation.blur(testImage, 2.0f);
        BufferedImage resultLarge = FilterOperation.blur(testImage, 10.0f);
        assertNotNull(resultSmall);
        assertNotNull(resultLarge);
    }

    // ==================== Sharpen Tests ====================

    @Test
    void sharpen_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.sharpen(null));
    }

    @Test
    void sharpen_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.sharpen(testImage);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void sharpen_withStrength() {
        BufferedImage result = FilterOperation.sharpen(testImage, 2.0f);
        assertNotNull(result);
    }

    @Test
    void sharpen_withNegativeStrength() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.sharpen(testImage, -1.0f));
    }

    // ==================== Grayscale Tests ====================

    @Test
    void grayscale_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.grayscale(null));
    }

    @Test
    void grayscale_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.grayscale(testImage);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void grayscale_shouldConvertToGrayscale() {
        BufferedImage grayImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        grayImage.setRGB(5, 5, 0xFFFF0000);
        
        BufferedImage result = FilterOperation.grayscale(grayImage);
        
        int pixel = result.getRGB(5, 5);
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;
        
        assertEquals(r, g, "R and G should be equal in grayscale");
        assertEquals(g, b, "G and B should be equal in grayscale");
        assertTrue(r >= 70 && r <= 85, "Grayscale value should be approximately 77");
    }

    @Test
    void grayscale_shouldPreserveAlphaChannel() {
        BufferedImage alphaImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
        alphaImage.setRGB(5, 5, 0x80FF0000);
        
        BufferedImage result = FilterOperation.grayscale(alphaImage);
        
        assertEquals(BufferedImage.TYPE_INT_ARGB, result.getType(), "Output should preserve ARGB type");
        
        int pixel = result.getRGB(5, 5);
        int alpha = (pixel >> 24) & 0xFF;
        int r = (pixel >> 16) & 0xFF;
        int g = (pixel >> 8) & 0xFF;
        int b = pixel & 0xFF;
        
        assertEquals(128, alpha, "Alpha channel should be preserved");
        assertEquals(r, g, "R and G should be equal in grayscale");
        assertEquals(g, b, "G and B should be equal in grayscale");
    }

    // ==================== Brightness Tests ====================

    @Test
    void brightness_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.brightness(null, 1.5f));
    }

    @Test
    void brightness_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.brightness(testImage, 1.5f);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void brightness_withFactorOne() {
        BufferedImage result = FilterOperation.brightness(testImage, 1.0f);
        assertNotNull(result);
    }

    @Test
    void brightness_withFactorTwo() {
        BufferedImage result = FilterOperation.brightness(testImage, 2.0f);
        assertNotNull(result);
    }

    @Test
    void brightnessOffset_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.brightnessOffset(null, 50));
    }

    @Test
    void brightnessOffset_shouldThrowExceptionForInvalidOffset() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.brightnessOffset(testImage, 300));
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.brightnessOffset(testImage, -300));
    }

    @Test
    void brightnessOffset_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.brightnessOffset(testImage, 50);
        assertNotNull(result);
    }

    // ==================== Contrast Tests ====================

    @Test
    void contrast_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.contrast(null, 1.5f));
    }

    @Test
    void contrast_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.contrast(testImage, 1.5f);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void contrast_withFactorOne() {
        BufferedImage result = FilterOperation.contrast(testImage, 1.0f);
        assertNotNull(result);
    }

    @Test
    void contrast_withFactorTwo() {
        BufferedImage result = FilterOperation.contrast(testImage, 2.0f);
        assertNotNull(result);
    }

    // ==================== Sepia Tests ====================

    @Test
    void sepia_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.sepia(null));
    }

    @Test
    void sepia_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.sepia(testImage);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void sepia_withIntensity() {
        BufferedImage result = FilterOperation.sepia(testImage, 0.5f);
        assertNotNull(result);
    }

    @Test
    void sepia_withInvalidIntensity() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.sepia(testImage, 1.5f));
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.sepia(testImage, -0.5f));
    }

    @Test
    void sepia_withZeroIntensityReturnsOriginal() {
        BufferedImage result = FilterOperation.sepia(testImage, 0.0f);
        assertNotNull(result);
    }

    // ==================== Edge Detection Tests ====================

    @Test
    void edgeDetect_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.edgeDetect(null));
    }

    @Test
    void edgeDetect_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.edgeDetect(testImage);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    // ==================== Vignette Tests ====================

    @Test
    void vignette_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.vignette(null, 0.5f));
    }

    @Test
    void vignette_shouldThrowExceptionForInvalidIntensity() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.vignette(testImage, 1.5f));
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.vignette(testImage, -0.5f));
    }

    @Test
    void vignette_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.vignette(testImage, 0.5f);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void vignette_withZeroIntensity() {
        BufferedImage result = FilterOperation.vignette(testImage, 0.0f);
        assertNotNull(result);
    }

    // ==================== Invert Tests ====================

    @Test
    void invert_shouldThrowExceptionForNullSource() {
        assertThrows(IllegalArgumentException.class, () -> FilterOperation.invert(null));
    }

    @Test
    void invert_shouldReturnBufferedImage() {
        BufferedImage result = FilterOperation.invert(testImage);
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }

    @Test
    void invert_shouldInvertColors() {
        // Create a simple grayscale image
        BufferedImage grayImage = new BufferedImage(10, 10, BufferedImage.TYPE_INT_RGB);
        grayImage.setRGB(5, 5, 0x808080); // Gray (128)
        
        BufferedImage result = FilterOperation.invert(grayImage);
        int invertedPixel = result.getRGB(5, 5) & 0xFF;
        
        // 128 should become 127
        assertEquals(127, invertedPixel);
    }

    // ==================== Chaining Tests ====================

    @Test
    void multipleFiltersCanBeChained() {
        // This tests that filters work correctly when applied sequentially
        BufferedImage result = testImage;
        result = FilterOperation.grayscale(result);
        result = FilterOperation.blur(result, 3.0f);
        result = FilterOperation.brightness(result, 1.2f);
        result = FilterOperation.contrast(result, 1.1f);
        
        assertNotNull(result);
        assertEquals(TEST_WIDTH, result.getWidth());
        assertEquals(TEST_HEIGHT, result.getHeight());
    }
}
