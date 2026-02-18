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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class ImageWatermarkIntegrationTest {

    private static final String TEST_IMAGE_FILENAME = "test-image.jpg";
    private static final String WATERMARK_IMAGE_FILENAME = "do-not-copy-watermark.png";

    @TempDir
    Path tempDir;

    @Test
    void testApplyImageWatermarkToRealImageAllPositions() throws IOException {
        File testImageFile = findTestImage();
        assertTrue(testImageFile.exists(), "Test image should exist at: " + testImageFile.getAbsolutePath());

        File watermarkImageFile = findWatermarkImage();
        assertTrue(watermarkImageFile.exists(), "Watermark image should exist at: " + watermarkImageFile.getAbsolutePath());

        BufferedImage image = ImageIO.read(testImageFile);
        assertNotNull(image, "Should be able to read test image");
        assertTrue(image.getWidth() > 0, "Image should have width");
        assertTrue(image.getHeight() > 0, "Image should have height");

        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);
        assertNotNull(watermarkImage, "Should be able to read watermark image");
        assertTrue(watermarkImage.getWidth() > 0, "Watermark image should have width");
        assertTrue(watermarkImage.getHeight() > 0, "Watermark image should have height");

        float opacity = 0.7f;
        float scale = 0.25f;

        for (WatermarkPosition position : WatermarkPosition.values()) {
            BufferedImage testImage = copyImage(image);

            ImageWatermark watermark = ImageWatermark.builder()
                    .image(watermarkImage)
                    .position(position)
                    .opacity(opacity)
                    .scale(scale)
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Should apply image watermark without error at position: " + position);

            assertFalse(isImageBlank(testImage), "Image should not be blank after applying watermark at: " + position);

            File outputFile = tempDir.resolve("image_watermark_" + position.name() + ".png").toFile();
            assertTrue(ImageIO.write(testImage, "png", outputFile),
                    "Should write output image for position: " + position);
        }
    }

    @Test
    void testImageWatermarkWithDifferentScales() throws IOException {
        File testImageFile = findTestImage();
        File watermarkImageFile = findWatermarkImage();
        assertTrue(testImageFile.exists() && watermarkImageFile.exists(), "Both images should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

        float[] scales = {0.1f, 0.25f, 0.5f, 0.75f, 1.0f};

        for (float scale : scales) {
            BufferedImage testImage = copyImage(image);

            ImageWatermark watermark = ImageWatermark.builder()
                    .image(watermarkImage)
                    .position(WatermarkPosition.CENTER)
                    .opacity(0.7f)
                    .scale(scale)
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Should apply image watermark with scale: " + scale);

            File outputFile = tempDir.resolve("image_watermark_scale_" + (int)(scale * 100) + ".png").toFile();
            ImageIO.write(testImage, "png", outputFile);
        }
    }

    @Test
    void testImageWatermarkWithDifferentOpacities() throws IOException {
        File testImageFile = findTestImage();
        File watermarkImageFile = findWatermarkImage();
        assertTrue(testImageFile.exists() && watermarkImageFile.exists(), "Both images should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

        float[] opacities = {0.2f, 0.4f, 0.6f, 0.8f, 1.0f};

        for (float opacity : opacities) {
            BufferedImage testImage = copyImage(image);

            ImageWatermark watermark = ImageWatermark.builder()
                    .image(watermarkImage)
                    .position(WatermarkPosition.BOTTOM_RIGHT)
                    .opacity(opacity)
                    .scale(0.25f)
                    .build();

            assertDoesNotThrow(() -> watermark.apply(testImage),
                    "Should apply image watermark with opacity: " + opacity);

            File outputFile = tempDir.resolve("image_watermark_opacity_" + (int)(opacity * 100) + ".png").toFile();
            ImageIO.write(testImage, "png", outputFile);
        }
    }

    @Test
    void testImageWatermarkLargerThanTargetScalesDown() throws IOException {
        File testImageFile = findTestImage();
        File watermarkImageFile = findWatermarkImage();
        assertTrue(testImageFile.exists() && watermarkImageFile.exists(), "Both images should exist");

        BufferedImage image = ImageIO.read(testImageFile);
        BufferedImage watermarkImage = ImageIO.read(watermarkImageFile);

        // Create a larger watermark (scale > 1.0 would make it larger)
        // Actually, scale is limited to 0.0-1.0, so we need to test with a physically larger image
        // Let's create a larger watermark by scaling up the original
        BufferedImage largeWatermark = new BufferedImage(
                watermarkImage.getWidth() * 2,
                watermarkImage.getHeight() * 2,
                watermarkImage.getType());
        java.awt.Graphics2D g = largeWatermark.createGraphics();
        g.drawImage(watermarkImage, 0, 0, largeWatermark.getWidth(), largeWatermark.getHeight(), null);
        g.dispose();

        ImageWatermark watermark = ImageWatermark.builder()
                .image(largeWatermark)
                .position(WatermarkPosition.CENTER)
                .opacity(0.7f)
                .scale(1.0f) // Use full size
                .build();

        BufferedImage testImage = copyImage(image);
        assertDoesNotThrow(() -> watermark.apply(testImage),
                "Should apply larger watermark without error");

        File outputFile = tempDir.resolve("image_watermark_larger.png").toFile();
        ImageIO.write(testImage, "png", outputFile);
    }

    private File findTestImage() {
        return findImageFile(TEST_IMAGE_FILENAME);
    }

    private File findWatermarkImage() {
        return findImageFile(WATERMARK_IMAGE_FILENAME);
    }

    private File findImageFile(String filename) {
        File image = new File(filename);
        if (image.exists()) {
            return image;
        }

        File projectRoot = new File(System.getProperty("user.dir"));
        image = new File(projectRoot, filename);
        if (image.exists()) {
            return image;
        }

        File parentDir = projectRoot.getParentFile();
        if (parentDir != null) {
            image = new File(parentDir, filename);
            if (image.exists()) {
                return image;
            }
        }

        return image;
    }

    private BufferedImage copyImage(BufferedImage original) {
        BufferedImage copy = new BufferedImage(
                original.getWidth(), original.getHeight(), original.getType());
        java.awt.Graphics2D g = copy.createGraphics();
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
