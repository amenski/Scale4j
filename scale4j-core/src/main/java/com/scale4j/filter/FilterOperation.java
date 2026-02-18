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
package com.scale4j.filter;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ByteLookupTable;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.LookupOp;
import java.awt.image.RescaleOp;

/**
 * Utility class providing image filter operations.
 * Filters are based on ConvolveOp, LookupOp, and RescaleOp.
 */
public final class FilterOperation {

    private FilterOperation() {
        // Utility class
    }

    // ==================== Blur ====================

    /**
     * Applies a Gaussian blur to the image.
     *
     * @param source  the source image
     * @param radius the blur radius (greater than 0)
     * @return the blurred image
     */
    public static BufferedImage blur(BufferedImage source, float radius) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (radius <= 0) {
            throw new IllegalArgumentException("Blur radius must be greater than 0");
        }

        int size = Math.max(3, (int) (radius * 2 + 1));
        if (size % 2 == 0) {
            size++;
        }

        float[] kernel = createGaussianKernel(size, radius);
        Kernel convolveKernel = new Kernel(size, size, kernel);
        ConvolveOp op = new ConvolveOp(convolveKernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }

    /**
     * Creates a Gaussian kernel for blur operations.
     */
    private static float[] createGaussianKernel(int size, float radius) {
        float[] kernel = new float[size * size];
        float sigma = radius / 3.0f;
        float twoSigmaSq = 2 * sigma * sigma;
        float sqrtTwoSigmaSqPi = (float) Math.sqrt(twoSigmaSq * Math.PI);
        float sum = 0;

        int halfSize = size / 2;
        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                int dx = x - halfSize;
                int dy = y - halfSize;
                float value = (float) Math.exp(-(dx * dx + dy * dy) / twoSigmaSq) / sqrtTwoSigmaSqPi;
                kernel[y * size + x] = value;
                sum += value;
            }
        }

        // Normalize
        for (int i = 0; i < kernel.length; i++) {
            kernel[i] /= sum;
        }

        return kernel;
    }

    // ==================== Sharpen ====================

    /**
     * Applies a sharpening filter to the image.
     *
     * @param source the source image
     * @return the sharpened image
     */
    public static BufferedImage sharpen(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        // Sharpen kernel
        float[] sharpenKernel = {
            0f, -1f, 0f,
            -1f, 5f, -1f,
            0f, -1f, 0f
        };
        Kernel kernel = new Kernel(3, 3, sharpenKernel);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }

    /**
     * Applies a sharpening filter with custom strength.
     *
     * @param source  the source image
     * @param strength the sharpening strength (1.0 = normal, higher = stronger)
     * @return the sharpened image
     */
    public static BufferedImage sharpen(BufferedImage source, float strength) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (strength < 0) {
            throw new IllegalArgumentException("Strength cannot be negative");
        }

        float center = 1f + 4f * strength;
        float edge = -strength;

        float[] sharpenKernel = {
            edge, edge, edge,
            edge, center, edge,
            edge, edge, edge
        };
        Kernel kernel = new Kernel(3, 3, sharpenKernel);
        ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        return op.filter(source, null);
    }

    // ==================== Grayscale ====================

    /**
     * Converts the image to grayscale.
     *
     * @param source the source image
     * @return the grayscale image
     */
    public static BufferedImage grayscale(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        int width = source.getWidth();
        int height = source.getHeight();
        
        // Create output image with same type
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        // Apply grayscale conversion pixel by pixel
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int rgb = source.getRGB(x, y);
                
                // Extract RGB components (ignoring alpha for RGB images)
                int r = (rgb >> 16) & 0xFF;
                int g = (rgb >> 8) & 0xFF;
                int b = rgb & 0xFF;
                
                // Apply ITU-R BT.601 luma coefficients
                int gray = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                gray = Math.min(255, Math.max(0, gray));
                
                // Set grayscale pixel (R = G = B = gray)
                result.setRGB(x, y, (gray << 16) | (gray << 8) | gray);
            }
        }
        
        return result;
    }

    // ==================== Brightness ====================

    /**
     * Adjusts the brightness of the image.
     *
     * @param source     the source image
     * @param brightness the brightness factor (1.0 = no change, 2.0 = twice as bright, 0.5 = half as bright)
     * @return the brightness-adjusted image
     */
    public static BufferedImage brightness(BufferedImage source, float brightness) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        RescaleOp op = new RescaleOp(brightness, 0f, null);
        return op.filter(source, null);
    }

    /**
     * Adjusts the brightness by adding an offset.
     *
     * @param source     the source image
     * @param offset     the brightness offset (-255 to 255)
     * @return the brightness-adjusted image
     */
    public static BufferedImage brightnessOffset(BufferedImage source, float offset) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (offset < -255 || offset > 255) {
            throw new IllegalArgumentException("Offset must be between -255 and 255");
        }

        RescaleOp op = new RescaleOp(1f, offset, null);
        return op.filter(source, null);
    }

    // ==================== Contrast ====================

    /**
     * Adjusts the contrast of the image.
     *
     * @param source   the source image
     * @param contrast the contrast factor (1.0 = no change, 2.0 = double contrast, 0.5 = half contrast)
     * @return the contrast-adjusted image
     */
    public static BufferedImage contrast(BufferedImage source, float contrast) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        // Scale and offset for contrast
        float offset = (1f - contrast) * 128f;
        RescaleOp op = new RescaleOp(contrast, offset, null);
        return op.filter(source, null);
    }

    // ==================== Sepia ====================

    /**
     * Applies a sepia tone effect to the image.
     *
     * @param source the source image
     * @return the sepia-toned image
     */
    public static BufferedImage sepia(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        // Sepia transformation matrix
        byte[] sepiaR = new byte[256];
        byte[] sepiaG = new byte[256];
        byte[] sepiaB = new byte[256];

        for (int i = 0; i < 256; i++) {
            double r = i;
            double g = i;
            double b = i;

            // Sepia transformation
            int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            sepiaR[i] = (byte) Math.min(255, tr);
            sepiaG[i] = (byte) Math.min(255, tg);
            sepiaB[i] = (byte) Math.min(255, tb);
        }

        byte[][] lookupTable = new byte[][]{sepiaR, sepiaG, sepiaB};
        LookupOp op = new LookupOp(new ByteLookupTable(0, lookupTable), null);
        return op.filter(source, null);
    }

    /**
     * Applies a sepia tone effect with custom intensity.
     *
     * @param source    the source image
     * @param intensity the sepia intensity (0.0 = no effect, 1.0 = full sepia)
     * @return the sepia-toned image
     */
    public static BufferedImage sepia(BufferedImage source, float intensity) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (intensity < 0 || intensity > 1) {
            throw new IllegalArgumentException("Intensity must be between 0 and 1");
        }

        if (intensity == 0) {
            return source;
        }
        if (intensity == 1) {
            return sepia(source);
        }

        // Interpolate between original and sepia
        byte[] sepiaR = new byte[256];
        byte[] sepiaG = new byte[256];
        byte[] sepiaB = new byte[256];

        for (int i = 0; i < 256; i++) {
            double r = i;
            double g = i;
            double b = i;

            int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
            int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
            int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

            int sepiaValR = Math.min(255, tr);
            int sepiaValG = Math.min(255, tg);
            int sepiaValB = Math.min(255, tb);

            sepiaR[i] = (byte) (i + (sepiaValR - i) * intensity);
            sepiaG[i] = (byte) (i + (sepiaValG - i) * intensity);
            sepiaB[i] = (byte) (i + (sepiaValB - i) * intensity);
        }

        byte[][] lookupTable = new byte[][]{sepiaR, sepiaG, sepiaB};
        LookupOp op = new LookupOp(new ByteLookupTable(0, lookupTable), null);
        return op.filter(source, null);
    }

    // ==================== Edge Detection ====================

    /**
     * Applies an edge detection filter using the Sobel operator.
     *
     * @param source the source image
     * @return the edge-detected image
     */
    public static BufferedImage edgeDetect(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        // First convert to grayscale for edge detection
        BufferedImage gray = grayscale(source);

        // Sobel horizontal kernel
        float[] sobelH = {
            -1f, 0f, 1f,
            -2f, 0f, 2f,
            -1f, 0f, 1f
        };

        // Sobel vertical kernel
        float[] sobelV = {
            -1f, -2f, -1f,
            0f, 0f, 0f,
            1f, 2f, 1f
        };

        Kernel kernelH = new Kernel(3, 3, sobelH);
        Kernel kernelV = new Kernel(3, 3, sobelV);

        ConvolveOp opH = new ConvolveOp(kernelH, ConvolveOp.EDGE_NO_OP, null);
        ConvolveOp opV = new ConvolveOp(kernelV, ConvolveOp.EDGE_NO_OP, null);

        BufferedImage resultH = opH.filter(gray, null);
        BufferedImage resultV = opV.filter(gray, null);

        // Combine the two results
        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int pixelH = resultH.getRGB(x, y) & 0xFF;
                int pixelV = resultV.getRGB(x, y) & 0xFF;
                int magnitude = (int) Math.sqrt(pixelH * pixelH + pixelV * pixelV);
                magnitude = Math.min(255, magnitude);
                int rgb = (magnitude << 16) | (magnitude << 8) | magnitude;
                result.setRGB(x, y, rgb);
            }
        }

        return result;
    }

    // ==================== Vignette ====================

    /**
     * Applies a vignette effect to the image.
     *
     * @param source    the source image
     * @param intensity the vignette intensity (0.0 = no effect, 1.0 = strong vignette)
     * @return the vignetted image
     */
    public static BufferedImage vignette(BufferedImage source, float intensity) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }
        if (intensity < 0 || intensity > 1) {
            throw new IllegalArgumentException("Intensity must be between 0 and 1");
        }

        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        float centerX = width / 2f;
        float centerY = height / 2f;
        float maxDistance = (float) Math.sqrt(centerX * centerX + centerY * centerY);

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                float dx = x - centerX;
                float dy = y - centerY;
                float distance = (float) Math.sqrt(dx * dx + dy * dy);
                float normalizedDistance = distance / maxDistance;

                // Calculate vignette factor (1.0 at center, decreasing towards edges)
                float factor = 1.0f - (normalizedDistance * intensity);
                factor = Math.max(0, Math.min(1, factor));

                int rgb = source.getRGB(x, y);
                int r = ((rgb >> 16) & 0xFF);
                int g = ((rgb >> 8) & 0xFF);
                int b = (rgb & 0xFF);

                r = (int) (r * factor);
                g = (int) (g * factor);
                b = (int) (b * factor);

                r = Math.max(0, Math.min(255, r));
                g = Math.max(0, Math.min(255, g));
                b = Math.max(0, Math.min(255, b));

                result.setRGB(x, y, (r << 16) | (g << 8) | b);
            }
        }

        return result;
    }

    // ==================== Invert ====================

    /**
     * Inverts the colors of the image.
     *
     * @param source the source image
     * @return the inverted image
     */
    public static BufferedImage invert(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        byte[] invertTable = new byte[256];
        for (int i = 0; i < 256; i++) {
            invertTable[i] = (byte) (255 - i);
        }

        byte[][] lookupTable = new byte[][]{invertTable, invertTable, invertTable};
        LookupOp op = new LookupOp(new ByteLookupTable(0, lookupTable), null);
        return op.filter(source, null);
    }

    // ==================== Flip ====================

    /**
     * Flips the image horizontally (left to right).
     *
     * @param source the source image
     * @return the flipped image
     */
    public static BufferedImage flip(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setRGB(width - 1 - x, y, source.getRGB(x, y));
            }
        }

        return result;
    }

    // ==================== Flop ====================

    /**
     * Flips the image vertically (top to bottom).
     *
     * @param source the source image
     * @return the flipped image
     */
    public static BufferedImage flop(BufferedImage source) {
        if (source == null) {
            throw new IllegalArgumentException("Source image cannot be null");
        }

        int width = source.getWidth();
        int height = source.getHeight();
        BufferedImage result = new BufferedImage(width, height, source.getType());

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                result.setRGB(x, height - 1 - y, source.getRGB(x, y));
            }
        }

        return result;
    }
}
