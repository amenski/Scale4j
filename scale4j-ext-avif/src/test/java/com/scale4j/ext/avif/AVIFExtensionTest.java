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
package com.scale4j.ext.avif;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for AVIF extension.
 */
class AVIFExtensionTest {

    @BeforeEach
    void setUp() {
        AVIFExtension.initialize();
    }

    @Test
    void testInitialize() {
        // Should not throw exception
        AVIFExtension.initialize();
        assertTrue(true);
    }

    @Test
    void testGetVersion() {
        assertNotNull(AVIFExtension.getVersion());
        assertEquals("1.0.0", AVIFExtension.getVersion());
    }

    @Test
    void testGetMinimumJavaVersion() {
        String minVersion = AVIFExtension.getMinimumJavaVersion();
        assertNotNull(minVersion);
        assertEquals("11", minVersion);
    }

    @Test
    void testGetSupportedFormats() {
        Set<String> formats = AVIFExtension.getSupportedFormats();
        assertNotNull(formats);
        assertTrue(formats.contains("avif"));
    }

    @Test
    void testGetSupportLevel() {
        String level = AVIFExtension.getSupportLevel();
        assertNotNull(level);
        // Level can be "native", "plugin", or "none"
        assertTrue(level.equals("native") || level.equals("plugin") || level.equals("none"));
        System.out.println("AVIF support level: " + level);
    }

    @Test
    void testAVIFReadSupport() {
        boolean readSupported = AVIFExtension.isReadSupported();
        System.out.println("AVIF read support: " + readSupported);
    }

    @Test
    void testAVIFWriteSupport() {
        boolean writeSupported = AVIFExtension.isWriteSupported();
        System.out.println("AVIF write support: " + writeSupported);
    }

    @Test
    void testAVIFFullySupported() {
        boolean supported = AVIFExtension.isSupported();
        System.out.println("AVIF fully supported: " + supported);
    }

    @Test
    void testCreateAndSaveAVIFImage() throws IOException {
        // Create a simple test image
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLUE);
        g.fillOval(25, 25, 50, 50);
        g.dispose();

        // Save to a temporary file
        Path tempFile = Files.createTempFile("test", ".avif");
        try {
            // Try to save as AVIF if supported
            if (AVIFExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "avif", tempFile.toFile());
                assertTrue(Files.exists(tempFile));
                assertTrue(Files.size(tempFile) > 0);
                System.out.println("AVIF file saved: " + tempFile + " (" + Files.size(tempFile) + " bytes)");
            } else {
                System.out.println("AVIF write not supported on this Java version");
                System.out.println("Support level: " + AVIFExtension.getSupportLevel());
                System.out.println("Java version: " + System.getProperty("java.version"));
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testAVIFWithTransparentBackground() throws IOException {
        // Create an image with transparency
        BufferedImage testImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = testImage.createGraphics();
        
        // Transparent background
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 64, 64);
        
        // Draw semi-transparent circle
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(new Color(0, 255, 0, 128));
        g.fillOval(8, 8, 48, 48);
        
        g.dispose();

        Path tempFile = Files.createTempFile("transparent_test", ".avif");
        try {
            if (AVIFExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "avif", tempFile.toFile());
                assertTrue(Files.exists(tempFile));
                System.out.println("Transparent AVIF saved: " + Files.size(tempFile) + " bytes");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testAVIFGrayscale() throws IOException {
        // Create a grayscale image
        BufferedImage testImage = new BufferedImage(128, 128, BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D g = testImage.createGraphics();
        
        // Draw gradient-like pattern
        for (int x = 0; x < 128; x++) {
            int gray = x * 2;
            g.setColor(new Color(gray, gray, gray));
            g.drawLine(x, 0, x, 127);
        }
        g.dispose();

        Path tempFile = Files.createTempFile("grayscale_test", ".avif");
        try {
            if (AVIFExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "avif", tempFile.toFile());
                assertTrue(Files.exists(tempFile));
                System.out.println("Grayscale AVIF saved: " + Files.size(tempFile) + " bytes");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testAVIFLargeImage() throws IOException {
        // Create a larger test image to test performance
        BufferedImage testImage = new BufferedImage(800, 600, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        
        // Fill with gradient
        for (int y = 0; y < 600; y++) {
            for (int x = 0; x < 800; x++) {
                int r = (x * 255) / 800;
                int gVal = (y * 255) / 600;
                int b = 128;
                testImage.setRGB(x, y, new Color(r, gVal, b).getRGB());
            }
        }
        g.dispose();

        Path tempFile = Files.createTempFile("large_test", ".avif");
        try {
            if (AVIFExtension.isWriteSupported()) {
                long startTime = System.currentTimeMillis();
                javax.imageio.ImageIO.write(testImage, "avif", tempFile.toFile());
                long endTime = System.currentTimeMillis();
                
                assertTrue(Files.exists(tempFile));
                System.out.println("Large AVIF (800x600) saved: " + Files.size(tempFile) + " bytes in " + (endTime - startTime) + "ms");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testMultipleInitialization() {
        // Calling initialize multiple times should be safe
        AVIFExtension.initialize();
        AVIFExtension.initialize();
        AVIFExtension.initialize();
        
        // Just verify it doesn't crash
        assertTrue(true);
    }

    @Test
    void testJavaVersionCheck() {
        String javaVersion = System.getProperty("java.version");
        assertNotNull(javaVersion);
        System.out.println("Running on Java: " + javaVersion);
        
        // AVIF native support requires Java 21+
        if (javaVersion.startsWith("21") || javaVersion.startsWith("22")) {
            System.out.println("Java 21+ detected - native AVIF support should be available");
        }
    }

    @Test
    void testHEICSupport() {
        // AVIF extension also checks for HEIC/HEIF support
        Set<String> formats = AVIFExtension.getSupportedFormats();
        System.out.println("Supported formats in AVIF extension: " + formats);
    }
}
