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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

/**
 * Operation for rotating images.
 */
public final class RotateOperation {

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
            throw new IllegalArgumentException("Source image cannot be null");
        }

        // Normalize degrees to 0-360 range
        double normalizedDegrees = degrees % 360;
        if (normalizedDegrees < 0) {
            normalizedDegrees += 360;
        }

        // Handle common rotations
        if (Math.abs(normalizedDegrees - 90) < 0.001) {
            return rotate90Clockwise(source, backgroundColor);
        } else if (Math.abs(normalizedDegrees - 180) < 0.001) {
            return rotate180(source, backgroundColor);
        } else if (Math.abs(normalizedDegrees - 270) < 0.001) {
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
        BufferedImage rotated = new BufferedImage(newWidth, newHeight, source.getType());
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
     * Rotates an image 90 degrees clockwise.
     */
    private static BufferedImage rotate90Clockwise(BufferedImage source, Color backgroundColor) {
        int width = source.getWidth();
        int height = source.getHeight();

        BufferedImage rotated = new BufferedImage(height, width, source.getType());
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

        BufferedImage rotated = new BufferedImage(height, width, source.getType());
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

        BufferedImage rotated = new BufferedImage(width, height, source.getType());
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
