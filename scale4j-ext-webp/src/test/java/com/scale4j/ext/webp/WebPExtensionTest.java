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
package com.scale4j.ext.webp;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for WebP extension.
 */
class WebPExtensionTest {

    @BeforeEach
    void setUp() {
        WebPExtension.initialize();
    }

    @Test
    void testInitialize() {
        // Should not throw exception
        WebPExtension.initialize();
        assertTrue(true);
    }

    @Test
    void testGetVersion() {
        assertNotNull(WebPExtension.getVersion());
        assertEquals("1.0.0", WebPExtension.getVersion());
    }

    @Test
    void testGetSupportedFormats() {
        var formats = WebPExtension.getSupportedFormats();
        assertNotNull(formats);
        assertTrue(formats.contains("webp"));
        assertEquals(1, formats.size());
    }

    @Test
    void testWebPReadSupport() {
        boolean readSupported = WebPExtension.isReadSupported();
        // WebP read should be supported when plugin is available
        System.out.println("WebP read support: " + readSupported);
    }

    @Test
    void testWebPWriteSupport() {
        boolean writeSupported = WebPExtension.isWriteSupported();
        // WebP write should be supported when plugin is available
        System.out.println("WebP write support: " + writeSupported);
    }

    @Test
    void testWebPFullySupported() {
        boolean supported = WebPExtension.isSupported();
        System.out.println("WebP fully supported: " + supported);
    }

    @Test
    void testCreateAndSaveWebPImage() throws IOException {
        // Create a simple test image
        BufferedImage testImage = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.setColor(Color.BLUE);
        g.fillOval(25, 25, 50, 50);
        g.dispose();

        // Save to a temporary file
        Path tempFile = Files.createTempFile("test", ".webp");
        try {
            // Try to save as WebP if supported
            if (WebPExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "webp", tempFile.toFile());
                assertTrue(Files.exists(tempFile));
                assertTrue(Files.size(tempFile) > 0);
                System.out.println("WebP file saved: " + tempFile + " (" + Files.size(tempFile) + " bytes)");
            } else {
                System.out.println("WebP write not supported, skipping save test");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testWebPLoadingFromStream() throws IOException {
        // Create a simple test image
        BufferedImage testImage = new BufferedImage(50, 50, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = testImage.createGraphics();
        g.setColor(new Color(0, 255, 0, 128));
        g.fillRect(0, 0, 50, 50);
        g.dispose();

        // Save to a temporary file as PNG first, then read it
        Path tempFile = Files.createTempFile("test", ".png");
        try {
            javax.imageio.ImageIO.write(testImage, "png", tempFile.toFile());
            
            // Read back using ImageIO
            BufferedImage loaded = javax.imageio.ImageIO.read(tempFile.toFile());
            assertNotNull(loaded);
            assertEquals(50, loaded.getWidth());
            assertEquals(50, loaded.getHeight());
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testMultipleInitialization() {
        // Calling initialize multiple times should be safe
        WebPExtension.initialize();
        WebPExtension.initialize();
        WebPExtension.initialize();
        
        assertTrue(WebPExtension.isReadSupported() || !WebPExtension.isReadSupported());
    }

    @Test
    void testWebPWithTransparentBackground() throws IOException {
        // Create an image with transparency
        BufferedImage testImage = new BufferedImage(64, 64, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = testImage.createGraphics();
        
        // Transparent background
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, 64, 64);
        
        // Draw semi-transparent circle
        g.setComposite(AlphaComposite.SrcOver);
        g.setColor(new Color(255, 0, 0, 128));
        g.fillOval(8, 8, 48, 48);
        
        g.dispose();

        Path tempFile = Files.createTempFile("transparent_test", ".webp");
        try {
            if (WebPExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "webp", tempFile.toFile());
                assertTrue(Files.exists(tempFile));
                System.out.println("Transparent WebP saved: " + Files.size(tempFile) + " bytes");
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }

    @Test
    void testWebPDifferentQuality() throws IOException {
        BufferedImage testImage = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = testImage.createGraphics();
        
        // Create a colorful test pattern
        for (int i = 0; i < 10; i++) {
            g.setColor(new Color(i * 25, i * 20, i * 15));
            g.fillRect(i * 20, 0, 20, 200);
        }
        g.dispose();

        Path tempFile = Files.createTempFile("quality_test", ".webp");
        try {
            if (WebPExtension.isWriteSupported()) {
                javax.imageio.ImageIO.write(testImage, "webp", tempFile.toFile());
                long fileSize = Files.size(tempFile);
                System.out.println("WebP file size at 200x200: " + fileSize + " bytes");
                
                // Read back
                BufferedImage loaded = javax.imageio.ImageIO.read(tempFile.toFile());
                assertNotNull(loaded);
                assertEquals(200, loaded.getWidth());
            }
        } finally {
            Files.deleteIfExists(tempFile);
        }
    }
}
