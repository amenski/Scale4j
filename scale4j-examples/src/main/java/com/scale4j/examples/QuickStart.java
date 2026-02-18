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
package com.scale4j.examples;

import com.scale4j.Scale4j;
import com.scale4j.types.ResizeMode;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Quick start example demonstrating Scale4j usage.
 */
public class QuickStart {

    public static void main(String[] args) {
        try {
            // Basic resize
            basicResize();

            // Resize with mode
            resizeWithMode();

            // Chain operations
            chainOperations();

            // Add watermark
            addWatermark();

            System.out.println("All examples completed successfully!");
        } catch (IOException e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void basicResize() throws IOException {
        System.out.println("Running basic resize example...");

        BufferedImage original = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);

        BufferedImage result = Scale4j.load(original)
                .resize(400, 300)
                .build();

        System.out.println("  Original: 800x600 -> Resized: " + result.getWidth() + "x" + result.getHeight());
    }

    private static void resizeWithMode() throws IOException {
        System.out.println("Running resize with mode example...");

        BufferedImage original = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);

        BufferedImage result = Scale4j.load(original)
                .resize(400, 400, ResizeMode.FIT)
                .build();

        System.out.println("  Resized (FIT mode): " + result.getWidth() + "x" + result.getHeight());
    }

    private static void chainOperations() throws IOException {
        System.out.println("Running chain operations example...");

        BufferedImage original = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);

        BufferedImage result = Scale4j.load(original)
                .resize(400, 300)
                .rotate(90)
                .pad(10, Color.WHITE)
                .build();

        System.out.println("  Chain operations result: " + result.getWidth() + "x" + result.getHeight());
    }

    private static void addWatermark() throws IOException {
        System.out.println("Running watermark example...");

        BufferedImage original = new java.awt.image.BufferedImage(800, 600, java.awt.image.BufferedImage.TYPE_INT_RGB);

        BufferedImage result = Scale4j.load(original)
                .resize(400, 300)
                .watermark("© 2024 Scale4j")
                .build();

        System.out.println("  Watermark added successfully");
    }
}
