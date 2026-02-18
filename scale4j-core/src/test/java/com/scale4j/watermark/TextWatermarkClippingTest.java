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

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class TextWatermarkClippingTest {

    private static final String TEST_IMAGE_PATH = "../../test-image.jpg";

    @Test
    void testTextWatermarkNoClippingOnLeftAlignedPositions() throws Exception {
        BufferedImage image = loadTestImage();
        assertNotNull(image, "Test image should load");

        String testText = "Scale4j Test";

        for (WatermarkPosition pos : new WatermarkPosition[]{
                WatermarkPosition.TOP_LEFT,
                WatermarkPosition.MIDDLE_LEFT,
                WatermarkPosition.BOTTOM_LEFT
        }) {
            BufferedImage testImage = copyImage(image);
            TextWatermark watermark = TextWatermark.builder()
                    .text(testText)
                    .font(new Font("Arial", Font.PLAIN, 24))
                    .color(Color.WHITE)
                    .position(pos)
                    .margin(10)
                    .build();

            watermark.apply(testImage);

            int[] coords = calculateExpectedPosition(testImage, testText, pos, 24, 10);
            assertFalse(isClippingOnLeft(testImage, testText, coords[0], new Font("Arial", Font.PLAIN, 24)),
                    "Text should not clip on left side for position: " + pos);
        }
    }

    @Test
    void testTextWatermarkNoClippingOnRightAlignedPositions() throws Exception {
        BufferedImage image = loadTestImage();
        assertNotNull(image, "Test image should load");

        String testText = "Scale4j Test";

        for (WatermarkPosition pos : new WatermarkPosition[]{
                WatermarkPosition.TOP_RIGHT,
                WatermarkPosition.MIDDLE_RIGHT,
                WatermarkPosition.BOTTOM_RIGHT
        }) {
            BufferedImage testImage = copyImage(image);
            TextWatermark watermark = TextWatermark.builder()
                    .text(testText)
                    .font(new Font("Arial", Font.PLAIN, 24))
                    .color(Color.WHITE)
                    .position(pos)
                    .margin(10)
                    .build();

            watermark.apply(testImage);

            assertFalse(isClippingOnRight(testImage, testText, new Font("Arial", Font.PLAIN, 24), pos, 10),
                    "Text should not clip on right side for position: " + pos);
        }
    }

    @Test
    void testTextWatermarkNoClippingOnCenterPositions() throws Exception {
        BufferedImage image = loadTestImage();
        assertNotNull(image, "Test image should load");

        String testText = "Scale4j Test";

        for (WatermarkPosition pos : new WatermarkPosition[]{
                WatermarkPosition.TOP_CENTER,
                WatermarkPosition.CENTER,
                WatermarkPosition.BOTTOM_CENTER
        }) {
            BufferedImage testImage = copyImage(image);
            TextWatermark watermark = TextWatermark.builder()
                    .text(testText)
                    .font(new Font("Arial", Font.PLAIN, 24))
                    .color(Color.WHITE)
                    .position(pos)
                    .margin(10)
                    .build();

            watermark.apply(testImage);

            assertFalse(isClippingOnCenter(testImage, testText, new Font("Arial", Font.PLAIN, 24), pos, 10),
                    "Text should not clip for center position: " + pos);
        }
    }

    @Test
    void testLastLetterNotHidden() throws Exception {
        BufferedImage image = loadTestImage();
        assertNotNull(image, "Test image should load");

        String testText = "TEST";
        Font font = new Font("Arial", Font.PLAIN, 24);

        for (WatermarkPosition pos : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);
            TextWatermark watermark = TextWatermark.builder()
                    .text(testText)
                    .font(font)
                    .color(Color.WHITE)
                    .position(pos)
                    .margin(10)
                    .build();

            watermark.apply(testImage);

            int[] coords = TextWatermarkPositionCalculator.getInstance().calculate(
                    testImage.getWidth(), testImage.getHeight(),
                    getTextWidth(testText, font), 24, pos, 10);

            assertTrue(isLastLetterVisible(testImage, testText, font, coords[0], coords[1]),
                    "Last letter should not be hidden for position: " + pos);
        }
    }

    @Test
    void testAllPositionsWithLongText() throws Exception {
        BufferedImage image = loadTestImage();
        assertNotNull(image, "Test image should load");

        String longText = "This is a longer test text for watermark positioning verification";

        for (WatermarkPosition pos : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);
            TextWatermark watermark = TextWatermark.builder()
                    .text(longText)
                    .font(new Font("Arial", Font.PLAIN, 20))
                    .color(Color.WHITE)
                    .position(pos)
                    .margin(15)
                    .backgroundColor(new Color(0, 0, 0, 128))
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Applying watermark should not throw for position: " + pos);
        }
    }

    private BufferedImage loadTestImage() {
        try {
            File file = new File(TEST_IMAGE_PATH);
            if (file.exists()) {
                return ImageIO.read(file);
            }
        } catch (Exception e) {
        }
        return new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
    }

    private BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(
                original.getWidth(), original.getHeight(), original.getType());
        Graphics2D g = copy.createGraphics();
        g.drawImage(original, 0, 0, null);
        g.dispose();
        return copy;
    }

    private int[] calculateExpectedPosition(BufferedImage image, String text,
                                             WatermarkPosition pos, int fontSize, int margin) {
        TextWatermarkPositionCalculator calculator = TextWatermarkPositionCalculator.getInstance();
        Font font = new Font("Arial", Font.PLAIN, fontSize);
        int textWidth = getTextWidth(text, font);
        return calculator.calculate(image.getWidth(), image.getHeight(),
                textWidth, fontSize, pos, margin);
    }

    private int getTextWidth(String text, Font font) {
        BufferedImage tempImage = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = tempImage.createGraphics();
        FontRenderContext frc = g.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(text, frc);
        g.dispose();
        return (int) Math.ceil(bounds.getWidth());
    }

    private boolean isClippingOnLeft(BufferedImage image, String text, int x, Font font) {
        if (x <= 0) return true;
        int edgePixel = image.getRGB(0, image.getHeight() / 2);
        return edgePixel != Color.BLACK.getRGB() &&
               edgePixel != Color.DARK_GRAY.getRGB();
    }

    private boolean isClippingOnRight(BufferedImage image, String text, Font font,
                                       WatermarkPosition pos, int margin) {
        int[] coords = TextWatermarkPositionCalculator.getInstance().calculate(
                image.getWidth(), image.getHeight(),
                getTextWidth(text, font), 24, pos, margin);
        int textEnd = coords[0] + getTextWidth(text, font) + 2;
        return textEnd > image.getWidth();
    }

    private boolean isClippingOnCenter(BufferedImage image, String text, Font font,
                                        WatermarkPosition pos, int margin) {
        int textWidth = getTextWidth(text, font);
        int[] coords = TextWatermarkPositionCalculator.getInstance().calculate(
                image.getWidth(), image.getHeight(), textWidth, 24, pos, margin);
        int startX = coords[0];
        int endX = coords[0] + textWidth + 2;
        return startX < 0 || endX > image.getWidth();
    }

    private boolean isLastLetterVisible(BufferedImage image, String text, Font font,
                                         int x, int y) {
        int textWidth = getTextWidth(text, font);
        int lastCharX = x + textWidth - 1;
        if (lastCharX >= image.getWidth() || lastCharX < 0) return false;
        if (y >= image.getHeight() || y < 0) return false;
        return (image.getRGB(lastCharX, y) & 0xFF000000) != 0;
    }
}
