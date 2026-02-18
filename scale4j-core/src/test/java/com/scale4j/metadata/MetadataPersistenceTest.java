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

import com.scale4j.Scale4j;
import com.scale4j.ImageWithMetadata;
import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

class MetadataPersistenceTest {

    @Test
    void testMetadataPreservedThroughBuilder() {
        BufferedImage image = createTestImage(100, 80);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.RIGHT_TOP);
        ImageWithMetadata iwm = new ImageWithMetadata(image, metadata, "png");
        
        ImageWithMetadata result = Scale4j.load(iwm)
                .resize(50, 40)
                .buildWithMetadata();
        
        assertNotNull(result);
        assertNotNull(result.getMetadata());
        assertEquals(ExifOrientation.RIGHT_TOP, result.getMetadata().getOrientation());
        assertEquals("png", result.getSourceFormat());
    }

    @Test
    void testAutoRotationResetsOrientation() {
        BufferedImage image = createTestImage(100, 80);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.RIGHT_TOP);
        ImageWithMetadata iwm = new ImageWithMetadata(image, metadata, "png");
        
        ImageWithMetadata result = Scale4j.load(iwm)
                .autoRotate()
                .buildWithMetadata();
        
        assertNotNull(result);
        assertNotNull(result.getMetadata());
        // After auto-rotation, orientation should be reset to TOP_LEFT
        assertEquals(ExifOrientation.TOP_LEFT, result.getMetadata().getOrientation());
    }

    @Test
    void testMetadataPreservedWithMultipleOperations() {
        BufferedImage image = createTestImage(200, 150);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.BOTTOM_RIGHT);
        ImageWithMetadata iwm = new ImageWithMetadata(image, metadata, "jpg");
        
        ImageWithMetadata result = Scale4j.load(iwm)
                .resize(100, 75)
                .crop(10, 10, 80, 60)
                .pad(5, 5, 5, 5, Color.WHITE)
                .buildWithMetadata();
        
        assertNotNull(result);
        assertNotNull(result.getMetadata());
        assertEquals(ExifOrientation.BOTTOM_RIGHT, result.getMetadata().getOrientation());
        assertEquals("jpg", result.getSourceFormat());
    }

    @Test
    void testWithMetadataMethods() throws Exception {
        BufferedImage image = createTestImage(100, 100);
        ExifMetadata metadata = new ExifMetadata(ExifOrientation.LEFT_TOP);
        ImageWithMetadata iwm = new ImageWithMetadata(image, metadata, "png");
        
        // Test that toByteArrayWithMetadata returns bytes
        byte[] bytes = Scale4j.load(iwm)
                .resize(50, 50)
                .toByteArrayWithMetadata("png");
        
        assertTrue(bytes.length > 0);
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