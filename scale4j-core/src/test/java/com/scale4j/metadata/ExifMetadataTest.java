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
package com.scale4j.metadata;

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class ExifMetadataTest {

    @Test
    void testDefaultConstructor() {
        ExifMetadata metadata = new ExifMetadata();
        assertEquals(ExifOrientation.TOP_LEFT, metadata.getOrientation());
        assertNull(metadata.getMetadata());
    }

    @Test
    void testConstructorWithOrientation() {
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.RIGHT_TOP);
        assertEquals(ExifOrientation.RIGHT_TOP, metadata.getOrientation());
    }

    @Test
    void testSetOrientation() {
        ExifMetadata metadata = new ExifMetadata();
        metadata.setOrientation(ExifOrientation.BOTTOM_RIGHT);
        assertEquals(ExifOrientation.BOTTOM_RIGHT, metadata.getOrientation());
    }

    @Test
    void testApplyAutoRotationNoOpForTopLeft() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.TOP_LEFT);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertSame(original, result);
    }

    @Test
    void testApplyAutoRotationNoOpForNull() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata();
        metadata.setOrientation(null);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertSame(original, result);
    }

    @Test
    void testApplyAutoRotation180Degrees() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.BOTTOM_RIGHT);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertNotSame(original, result);
        assertEquals(original.getWidth(), result.getWidth());
        assertEquals(original.getHeight(), result.getHeight());
    }

    @Test
    void testApplyAutoRotation90Degrees() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.RIGHT_TOP);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertNotSame(original, result);
        assertEquals(original.getHeight(), result.getWidth());
        assertEquals(original.getWidth(), result.getHeight());
    }

    @Test
    void testApplyAutoRotation270Degrees() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.LEFT_BOTTOM);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertNotSame(original, result);
        assertEquals(original.getHeight(), result.getWidth());
        assertEquals(original.getWidth(), result.getHeight());
    }

    @Test
    void testApplyAutoRotationFlipHorizontal() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.TOP_RIGHT);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertNotSame(original, result);
        assertEquals(original.getWidth(), result.getWidth());
        assertEquals(original.getHeight(), result.getHeight());
    }

    @Test
    void testApplyAutoRotationFlipVertical() {
        BufferedImage original = createTestImage(100, 50);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.BOTTOM_LEFT);
        
        BufferedImage result = metadata.applyAutoRotation(original);
        
        assertNotSame(original, result);
        assertEquals(original.getWidth(), result.getWidth());
        assertEquals(original.getHeight(), result.getHeight());
    }

    @Test
    void testApplyAutoRotationAllOrientations() {
        BufferedImage original = createTestImage(100, 80);
        
        for (ExifOrientation orientation : ExifOrientation.values()) {
            ExifMetadata metadata = new ExifMetadata(orientation);
            BufferedImage result = metadata.applyAutoRotation(original);
            
            assertNotNull(result, "Result should not be null for orientation: " + orientation);
            assertTrue(result.getWidth() > 0, "Width should be positive for orientation: " + orientation);
            assertTrue(result.getHeight() > 0, "Height should be positive for orientation: " + orientation);
        }
    }

    @Test
    void testRequiresTransformation() {
        assertFalse(ExifOrientation.TOP_LEFT.requiresTransformation());
        assertTrue(ExifOrientation.TOP_RIGHT.requiresTransformation());
        assertTrue(ExifOrientation.BOTTOM_RIGHT.requiresTransformation());
        assertTrue(ExifOrientation.BOTTOM_LEFT.requiresTransformation());
        assertTrue(ExifOrientation.LEFT_TOP.requiresTransformation());
        assertTrue(ExifOrientation.RIGHT_TOP.requiresTransformation());
        assertTrue(ExifOrientation.RIGHT_BOTTOM.requiresTransformation());
        assertTrue(ExifOrientation.LEFT_BOTTOM.requiresTransformation());
    }

    @Test
    void testApplyAutoRotationPixelMapping() {
        // Create a 2x2 test image with distinct colors in each quadrant
        // Top-left: RED, Top-right: GREEN, Bottom-left: BLUE, Bottom-right: YELLOW
        BufferedImage image = new BufferedImage(2, 2, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, Color.RED.getRGB());   // (0,0)
        image.setRGB(1, 0, Color.GREEN.getRGB()); // (1,0)
        image.setRGB(0, 1, Color.BLUE.getRGB());  // (0,1)
        image.setRGB(1, 1, Color.YELLOW.getRGB());// (1,1)
        
        // Test each orientation
        // Orientation 1 (TOP_LEFT): no transformation
        ExifMetadata metadata1 = new ExifMetadata(ExifOrientation.TOP_LEFT);
        BufferedImage result1 = metadata1.applyAutoRotation(image);
        assertEquals(Color.RED.getRGB(), result1.getRGB(0, 0));
        assertEquals(Color.GREEN.getRGB(), result1.getRGB(1, 0));
        assertEquals(Color.BLUE.getRGB(), result1.getRGB(0, 1));
        assertEquals(Color.YELLOW.getRGB(), result1.getRGB(1, 1));
        
        // Orientation 2 (TOP_RIGHT): flip horizontal
        ExifMetadata metadata2 = new ExifMetadata(ExifOrientation.TOP_RIGHT);
        BufferedImage result2 = metadata2.applyAutoRotation(image);
        assertEquals(Color.GREEN.getRGB(), result2.getRGB(0, 0)); // (1,0) -> (0,0)
        assertEquals(Color.RED.getRGB(), result2.getRGB(1, 0));   // (0,0) -> (1,0)
        assertEquals(Color.YELLOW.getRGB(), result2.getRGB(0, 1)); // (1,1) -> (0,1)
        assertEquals(Color.BLUE.getRGB(), result2.getRGB(1, 1));   // (0,1) -> (1,1)
        
        // Orientation 3 (BOTTOM_RIGHT): rotate 180°
        ExifMetadata metadata3 = new ExifMetadata(ExifOrientation.BOTTOM_RIGHT);
        BufferedImage result3 = metadata3.applyAutoRotation(image);
        assertEquals(Color.YELLOW.getRGB(), result3.getRGB(0, 0)); // (1,1) -> (0,0)
        assertEquals(Color.BLUE.getRGB(), result3.getRGB(1, 0));   // (0,1) -> (1,0)
        assertEquals(Color.GREEN.getRGB(), result3.getRGB(0, 1));  // (1,0) -> (0,1)
        assertEquals(Color.RED.getRGB(), result3.getRGB(1, 1));    // (0,0) -> (1,1)
        
        // Orientation 4 (BOTTOM_LEFT): flip vertical
        ExifMetadata metadata4 = new ExifMetadata(ExifOrientation.BOTTOM_LEFT);
        BufferedImage result4 = metadata4.applyAutoRotation(image);
        assertEquals(Color.BLUE.getRGB(), result4.getRGB(0, 0));   // (0,1) -> (0,0)
        assertEquals(Color.YELLOW.getRGB(), result4.getRGB(1, 0)); // (1,1) -> (1,0)
        assertEquals(Color.RED.getRGB(), result4.getRGB(0, 1));    // (0,0) -> (0,1)
        assertEquals(Color.GREEN.getRGB(), result4.getRGB(1, 1));  // (1,0) -> (1,1)
        
        // Note: Orientations 5-8 involve dimension swapping (2x2 -> 2x2, so same dimensions)
        // We'll test them but the pixel mapping is more complex
        // For now, just verify they produce valid images
        for (ExifOrientation orientation : new ExifOrientation[] {
            ExifOrientation.LEFT_TOP, ExifOrientation.RIGHT_TOP,
            ExifOrientation.RIGHT_BOTTOM, ExifOrientation.LEFT_BOTTOM
        }) {
            ExifMetadata metadata = new ExifMetadata(orientation);
            BufferedImage result = metadata.applyAutoRotation(image);
            assertNotNull(result);
            assertEquals(2, result.getWidth());
            assertEquals(2, result.getHeight());
        }
    }

    private BufferedImage createTestImage(int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        java.awt.Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.RED);
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.BLUE);
        g2d.fillRect(0, 0, width / 2, height / 2);
        g2d.dispose();
        return image;
    }
}
