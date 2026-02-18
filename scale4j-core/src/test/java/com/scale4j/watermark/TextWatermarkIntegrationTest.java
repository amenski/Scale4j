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
import org.junit.jupiter.api.io.TempDir;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class TextWatermarkIntegrationTest {

    private static final String TEST_IMAGE_FILENAME = "test-image.jpg";

    @TempDir
    Path tempDir;

    @Test
    void testApplyTextWatermarkToRealImageAllPositions() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist at: " + testImageFile.getAbsolutePath());

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");
        assertTrue(image.getWidth() > 0, "Image should have width");
        assertTrue(image.getHeight() > 0, "Image should have height");

        String watermarkText = "Scale4j";
        Font font = new Font("Arial", Font.BOLD, 32);
        Color textColor = Color.WHITE;
        int margin = 20;

        for (WatermarkPosition position : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);

            TextWatermark watermark = TextWatermark.builder()
                    .text(watermarkText)
                    .font(font)
                    .color(textColor)
                    .position(position)
                    .margin(margin)
                    .opacity(0.8f)
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Should apply watermark without error at position: " + position);

            assertFalse(isImageBlank(testImage), "Image should not be blank after applying watermark at: " + position);

            File outputFile = tempDir.resolve("watermark_" + position.name() + ".png").toFile();
            assertTrue(ImageIO.write(testImage, "png", outputFile), "Should write output image for position: " + position);
            assertTrue(ImageIO.write(testImage, "png", outputFile), "Should write output image for position: " + position);
        }
    }

    @Test
    void testTextWatermarkDoesNotClipOnRealImage() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");

        String testText = "TEST";
        Font font = new Font("Arial", Font.PLAIN, 24);
        int margin = 10;

        TextWatermarkPositionCalculator calculator = TextWatermarkPositionCalculator.getInstance();

        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D tempG2d = tempImage.createGraphics();
        tempG2d.setFont(font);
        FontRenderContext frc = tempG2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(testText, frc);
        LineMetrics metrics = font.getLineMetrics(testText, frc);
        float ascent = metrics.getAscent();
        tempG2d.dispose();

        int textWidth = (int) Math.ceil(bounds.getWidth());
        int textHeight = (int) Math.ceil(bounds.getHeight());

        for (WatermarkPosition position : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);

            int[] coords = calculator.calculate(
                    testImage.getWidth(), testImage.getHeight(),
                    textWidth, textHeight, position, margin);

            int rectX = coords[0];
            int rectY = coords[1];

            int[] textCoords = calculator.calculateTextPosition(
                    rectX, rectY, textWidth, textHeight, position, margin, ascent);

            int textX = textCoords[0];
            int textY = textCoords[1];

            TextWatermark watermark = TextWatermark.builder()
                    .text(testText)
                    .font(font)
                    .color(Color.WHITE)
                    .position(position)
                    .margin(margin)
                    .build();

            watermark.apply(testImage);

            int endX = textX + textWidth;
            assertTrue(textX >= 0,
                    "Text X should be non-negative for " + position + ", was: " + textX);
            assertTrue(endX <= testImage.getWidth(),
                    "Text end X should be within image bounds for " + position +
                            " (end: " + endX + ", width: " + testImage.getWidth() + ")");
        }
    }

    @Test
    void testWatermarkWithBackgroundOnRealImage() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");

        TextWatermark watermark = TextWatermark.builder()
                .text("Watermark with Background")
                .font(new Font("Verdana", Font.ITALIC, 28))
                .color(Color.YELLOW)
                .position(WatermarkPosition.CENTER)
                .margin(15)
                .backgroundColor(new Color(0, 0, 0, 180))
                .opacity(1.0f)
                .build();

        BufferedImage result = copyImage(image);
        watermark.apply(result);

        assertFalse(isImageBlank(result), "Image should not be blank");

        File outputFile = tempDir.resolve("watermark_with_background.png").toFile();
        assertTrue(ImageIO.write(result, "png", outputFile),
                "Should write output image with background");
    }

    @Test
    void testMultipleWatermarksOnSameImage() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");

        BufferedImage result = copyImage(image);

        TextWatermark bottomWatermark = TextWatermark.builder()
                .text("Bottom")
                .font(new Font("Arial", Font.BOLD, 20))
                .color(Color.WHITE)
                .position(WatermarkPosition.BOTTOM_CENTER)
                .margin(30)
                .opacity(0.9f)
                .build();
        bottomWatermark.apply(result);

        TextWatermark topWatermark = TextWatermark.builder()
                .text("Top")
                .font(new Font("Arial", Font.BOLD, 20))
                .color(Color.WHITE)
                .position(WatermarkPosition.TOP_CENTER)
                .margin(30)
                .opacity(0.9f)
                .build();
        topWatermark.apply(result);

        assertFalse(isImageBlank(result), "Image should not be blank after multiple watermarks");

        File outputFile = tempDir.resolve("multiple_watermarks.png").toFile();
        assertTrue(ImageIO.write(result, "png", outputFile),
                "Should write output image with multiple watermarks");
    }

    @Test
    void testLongTextWatermarkOnRealImage() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");

        String longText = "This is a longer text to test wrapping behavior if it were implemented";

        for (WatermarkPosition position : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);

            TextWatermark watermark = TextWatermark.builder()
                    .text(longText)
                    .font(new Font("Arial", Font.PLAIN, 18))
                    .color(Color.WHITE)
                    .position(position)
                    .margin(10)
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Should apply long text watermark at: " + position);
        }
    }

    private File findTestImage() {
        File testImage = new File(TEST_IMAGE_FILENAME);
        if (testImage.exists()) {
            return testImage;
        }

        File projectRoot = new File(System.getProperty("user.dir"));
        testImage = new File(projectRoot, TEST_IMAGE_FILENAME);
        if (testImage.exists()) {
            return testImage;
        }

        File parentDir = projectRoot.getParentFile();
        if (parentDir != null) {
            testImage = new File(parentDir, TEST_IMAGE_FILENAME);
            if (testImage.exists()) {
                return testImage;
            }
        }

        return testImage;
    }

    private BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(
                original.getWidth(), original.getHeight(), original.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return copy;
    }

    private boolean isImageBlank(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        int sampleStep = Math.max(1, Math.min(width, height) / 10);

        for (int y = 0; y < height; y += sampleStep) {
            for (int x = 0; x < width; x += sampleStep) {
                int rgb = image.getRGB(x, y);
                if ((rgb & 0xFF000000) != 0) {
                    return false;
                }
            }
        }
        return true;
    }
}
