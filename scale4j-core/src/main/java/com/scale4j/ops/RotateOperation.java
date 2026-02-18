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

import com.scale4j.exception.ImageProcessException;
import com.scale4j.util.ImageTypeUtils;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Operation for rotating images.
 */
public final class RotateOperation {

    private static final double EPSILON = 0.001;

    private RotateOperation() {
        // Utility class
    }

    /**
     * Rotates a BufferedImage by the specified degrees.
     *
     * @param source the source image
     * @param degrees the rotation angle in degrees (positive = clockwise)
     * @return the rotated image
     */
    public static BufferedImage rotate(BufferedImage source, double degrees) {
        return rotate(source, degrees, Color.WHITE);
    }

    /**
     * Rotates a BufferedImage by the specified degrees with a background color.
     *
     * @param source the source image
     * @param degrees the rotation angle in degrees (positive = clockwise)
     * @param backgroundColor the background color for empty spaces
     * @return the rotated image
     */
    public static BufferedImage rotate(BufferedImage source, double degrees, Color backgroundColor) {
        if (source == null) {
            throw new ImageProcessException("Source image cannot be null", "rotate");
        }

        // Normalize degrees to 0-360 range
        double normalizedDegrees = degrees % 360;
        if (normalizedDegrees < 0) {
            normalizedDegrees += 360;
        }

        // Handle common rotations
        if (Math.abs(normalizedDegrees - 90) < EPSILON) {
            return rotate90Clockwise(source, backgroundColor);
        } else if (Math.abs(normalizedDegrees - 180) < EPSILON) {
            return rotate180(source, backgroundColor);
        } else if (Math.abs(normalizedDegrees - 270) < EPSILON) {
            return rotate90CounterClockwise(source, backgroundColor);
        }

        // General rotation
        double radians = Math.toRadians(normalizedDegrees);

        // Calculate new dimensions
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();

        // Calculate the bounding box after rotation
        double cos = Math.abs(Math.cos(radians));
        double sin = Math.abs(Math.sin(radians));

        int newWidth = (int) (sourceWidth * cos + sourceHeight * sin);
        int newHeight = (int) (sourceWidth * sin + sourceHeight * cos);

        // Create the rotated image
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha()));
        Graphics2D g2d = rotated.createGraphics();

        // Set background
        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, newWidth, newHeight);
        }

        // Set transformation
        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(radians);
        transform.translate(-sourceWidth / 2.0, -sourceHeight / 2.0);

        g2d.setTransform(transform);
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Rotates a BufferedImage by the specified degrees with a background color, reusing the provided buffer if possible.
     * If the buffer dimensions and type match, it will be reused; otherwise a new image is created.
     *
     * @param source the source image
     * @param degrees the rotation angle in degrees (positive = clockwise)
     * @param backgroundColor the background color for empty spaces
     * @param buffer optional buffer to reuse (may be null)
     * @return the rotated image
     */
    public static BufferedImage rotateWithBuffer(BufferedImage source, double degrees, Color backgroundColor, BufferedImage buffer) {
        if (source == null) {
            throw new ImageProcessException("Source image cannot be null", "rotate");
        }

        // Normalize degrees to 0-360 range
        double normalizedDegrees = degrees % 360;
        if (normalizedDegrees < 0) {
            normalizedDegrees += 360;
        }

        // Early return for zero-degree rotation
        if (Math.abs(normalizedDegrees) < EPSILON) {
            return source;
        }

        // Calculate new dimensions
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        int newWidth;
        int newHeight;

        // Handle common rotations for dimension calculation
        if (Math.abs(normalizedDegrees - 90) < EPSILON || Math.abs(normalizedDegrees - 270) < EPSILON) {
            // 90 or 270 degree rotation swaps width and height
            newWidth = sourceHeight;
            newHeight = sourceWidth;
        } else if (Math.abs(normalizedDegrees - 180) < EPSILON) {
            // 180 degree rotation keeps same dimensions
            newWidth = sourceWidth;
            newHeight = sourceHeight;
        } else {
            // General rotation
            double radians = Math.toRadians(normalizedDegrees);
            double cos = Math.abs(Math.cos(radians));
            double sin = Math.abs(Math.sin(radians));
            newWidth = (int) (sourceWidth * cos + sourceHeight * sin);
            newHeight = (int) (sourceWidth * sin + sourceHeight * cos);
        }

        int imageType = ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha());
        BufferedImage rotated;
        if (buffer != null &&
            buffer.getWidth() == newWidth &&
            buffer.getHeight() == newHeight &&
            buffer.getType() == imageType) {
            rotated = buffer;
        } else {
            rotated = new BufferedImage(newWidth, newHeight, imageType);
        }

        // Handle common rotations with buffer reuse
        if (Math.abs(normalizedDegrees - 90) < EPSILON) {
            return rotate90ClockwiseWithBuffer(source, backgroundColor, rotated);
        } else if (Math.abs(normalizedDegrees - 180) < EPSILON) {
            return rotate180WithBuffer(source, backgroundColor, rotated);
        } else if (Math.abs(normalizedDegrees - 270) < EPSILON) {
            return rotate90CounterClockwiseWithBuffer(source, backgroundColor, rotated);
        }

        // General rotation
        double radians = Math.toRadians(normalizedDegrees);
        Graphics2D g2d = rotated.createGraphics();

        // Set background
        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, newWidth, newHeight);
        } else {
            // Clear to transparent when no background color is specified
            g2d.setComposite(java.awt.AlphaComposite.Clear);
            g2d.fillRect(0, 0, newWidth, newHeight);
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        }

        // Set transformation
        AffineTransform transform = new AffineTransform();
        transform.translate(newWidth / 2.0, newHeight / 2.0);
        transform.rotate(radians);
        transform.translate(-sourceWidth / 2.0, -sourceHeight / 2.0);

        g2d.setTransform(transform);
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Rotates an image 90 degrees clockwise using the provided destination buffer.
     */
    private static BufferedImage rotate90ClockwiseWithBuffer(BufferedImage source, Color backgroundColor, BufferedImage dest) {
        Graphics2D g2d = dest.createGraphics();
        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, dest.getWidth(), dest.getHeight());
        } else {
            // Clear to transparent when no background color is specified
            g2d.setComposite(java.awt.AlphaComposite.Clear);
            g2d.fillRect(0, 0, dest.getWidth(), dest.getHeight());
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        }
        g2d.translate(dest.getHeight(), 0);
        g2d.rotate(Math.toRadians(90));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return dest;
    }

    /**
     * Rotates an image 90 degrees counter-clockwise using the provided destination buffer.
     */
    private static BufferedImage rotate90CounterClockwiseWithBuffer(BufferedImage source, Color backgroundColor, BufferedImage dest) {
        Graphics2D g2d = dest.createGraphics();
        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, dest.getWidth(), dest.getHeight());
        } else {
            // Clear to transparent when no background color is specified
            g2d.setComposite(java.awt.AlphaComposite.Clear);
            g2d.fillRect(0, 0, dest.getWidth(), dest.getHeight());
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        }
        g2d.rotate(Math.toRadians(-90));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return dest;
    }

    /**
     * Rotates an image 180 degrees using the provided destination buffer.
     */
    private static BufferedImage rotate180WithBuffer(BufferedImage source, Color backgroundColor, BufferedImage dest) {
        Graphics2D g2d = dest.createGraphics();
        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, dest.getWidth(), dest.getHeight());
        } else {
            // Clear to transparent when no background color is specified
            g2d.setComposite(java.awt.AlphaComposite.Clear);
            g2d.fillRect(0, 0, dest.getWidth(), dest.getHeight());
            g2d.setComposite(java.awt.AlphaComposite.SrcOver);
        }
        g2d.translate(dest.getWidth(), dest.getHeight());
        g2d.rotate(Math.toRadians(180));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();
        return dest;
    }

    /**
     * Rotates an image 90 degrees clockwise.
     */
    private static BufferedImage rotate90Clockwise(BufferedImage source, Color backgroundColor) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage rotated = new BufferedImage(height, width, ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha()));
        Graphics2D g2d = rotated.createGraphics();

        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, rotated.getWidth(), rotated.getHeight());
        }

        g2d.translate(height, 0);
        g2d.rotate(Math.toRadians(90));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Rotates an image 90 degrees counter-clockwise.
     */
    private static BufferedImage rotate90CounterClockwise(BufferedImage source, Color backgroundColor) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage rotated = new BufferedImage(height, width, ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha()));
        Graphics2D g2d = rotated.createGraphics();

        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, rotated.getWidth(), rotated.getHeight());
        }

        g2d.rotate(Math.toRadians(-90));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    /**
     * Rotates an image 180 degrees.
     */
    private static BufferedImage rotate180(BufferedImage source, Color backgroundColor) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage rotated = new BufferedImage(width, height, ImageTypeUtils.getSafeImageType(source.getType(), source.getColorModel().hasAlpha()));
        Graphics2D g2d = rotated.createGraphics();

        if (backgroundColor != null) {
            g2d.setBackground(backgroundColor);
            g2d.clearRect(0, 0, width, height);
        }

        g2d.translate(width, height);
        g2d.rotate(Math.toRadians(180));
        g2d.drawImage(source, 0, 0, null);
        g2d.dispose();

        return rotated;
    }
}
