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
package com.scale4j.benchmarks;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Factory for creating test images used in benchmarks.
 */
public final class TestImageFactory {

    private TestImageFactory() {
        // Utility class
    }

    /**
     * Creates a solid-color test image with the specified dimensions.
     * The image is filled with a consistent blue-gray color (RGB: 100, 150, 200).
     *
     * @param width the width of the image
     * @param height the height of the image
     * @return a new BufferedImage
     */
    public static BufferedImage createSolidTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setColor(new Color(100, 150, 200));
            g2d.fillRect(0, 0, width, height);
        } finally {
            g2d.dispose();
        }
        return image;
    }

    /**
     * Creates a patterned test image with random white rectangles over a solid background.
     * Uses a fixed random seed for reproducible results.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @return a new BufferedImage with random pattern
     */
    public static BufferedImage createPatternTestImage(int width, int height) {
        return createPatternTestImage(width, height, new Random(42));
    }

    /**
     * Creates a patterned test image with random white rectangles over a solid background.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @param random the random number generator to use for rectangle placement
     * @return a new BufferedImage with random pattern
     */
    public static BufferedImage createPatternTestImage(int width, int height, Random random) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setColor(new Color(100, 150, 200));
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.WHITE);
            for (int i = 0; i < 20; i++) {
                int x = random.nextInt(width);
                int y = random.nextInt(height);
                int size = 20 + random.nextInt(80);
                g2d.fillRect(x, y, size, size);
            }
        } finally {
            g2d.dispose();
        }
        return image;
    }

    /**
     * Creates a complex test image with ovals, text, and a solid background.
     * This matches the original implementation used in BenchmarkState.
     *
     * @param width the width of the image
     * @param height the height of the image
     * @return a new BufferedImage with ovals and text
     */
    public static BufferedImage createComplexTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        try {
            g2d.setColor(new Color(100, 150, 200));
            g2d.fillRect(0, 0, width, height);

            g2d.setColor(Color.WHITE);
            Random rand = new Random(42);
            for (int i = 0; i < 20; i++) {
                int x = rand.nextInt(width);
                int y = rand.nextInt(height);
                int size = 20 + rand.nextInt(80);
                g2d.fillOval(x, y, size, size);
            }

            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString("Test Image", width / 4, height / 2);
        } finally {
            g2d.dispose();
        }
        return image;
    }
}