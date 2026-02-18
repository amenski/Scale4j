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

import static org.junit.jupiter.api.Assertions.*;

class ExifOrientationTest {

    @Test
    void testTopLeft() {
        ExifOrientation orientation = ExifOrientation.TOP_LEFT;
        assertEquals(1, orientation.getTagValue());
        assertEquals(0, orientation.getRotationDegrees());
        assertFalse(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertFalse(orientation.requiresTransformation());
    }

    @Test
    void testTopRight() {
        ExifOrientation orientation = ExifOrientation.TOP_RIGHT;
        assertEquals(2, orientation.getTagValue());
        assertEquals(0, orientation.getRotationDegrees());
        assertTrue(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testBottomRight() {
        ExifOrientation orientation = ExifOrientation.BOTTOM_RIGHT;
        assertEquals(3, orientation.getTagValue());
        assertEquals(180, orientation.getRotationDegrees());
        assertFalse(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testBottomLeft() {
        ExifOrientation orientation = ExifOrientation.BOTTOM_LEFT;
        assertEquals(4, orientation.getTagValue());
        assertEquals(0, orientation.getRotationDegrees());
        assertFalse(orientation.isFlipHorizontal());
        assertTrue(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testLeftTop() {
        ExifOrientation orientation = ExifOrientation.LEFT_TOP;
        assertEquals(5, orientation.getTagValue());
        assertEquals(270, orientation.getRotationDegrees());
        assertTrue(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testRightTop() {
        ExifOrientation orientation = ExifOrientation.RIGHT_TOP;
        assertEquals(6, orientation.getTagValue());
        assertEquals(90, orientation.getRotationDegrees());
        assertFalse(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testRightBottom() {
        ExifOrientation orientation = ExifOrientation.RIGHT_BOTTOM;
        assertEquals(7, orientation.getTagValue());
        assertEquals(90, orientation.getRotationDegrees());
        assertTrue(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testLeftBottom() {
        ExifOrientation orientation = ExifOrientation.LEFT_BOTTOM;
        assertEquals(8, orientation.getTagValue());
        assertEquals(270, orientation.getRotationDegrees());
        assertFalse(orientation.isFlipHorizontal());
        assertFalse(orientation.isFlipVertical());
        assertTrue(orientation.requiresTransformation());
    }

    @Test
    void testFromTagValue() {
        assertEquals(ExifOrientation.TOP_LEFT, ExifOrientation.fromTagValue(1));
        assertEquals(ExifOrientation.TOP_RIGHT, ExifOrientation.fromTagValue(2));
        assertEquals(ExifOrientation.BOTTOM_RIGHT, ExifOrientation.fromTagValue(3));
        assertEquals(ExifOrientation.BOTTOM_LEFT, ExifOrientation.fromTagValue(4));
        assertEquals(ExifOrientation.LEFT_TOP, ExifOrientation.fromTagValue(5));
        assertEquals(ExifOrientation.RIGHT_TOP, ExifOrientation.fromTagValue(6));
        assertEquals(ExifOrientation.RIGHT_BOTTOM, ExifOrientation.fromTagValue(7));
        assertEquals(ExifOrientation.LEFT_BOTTOM, ExifOrientation.fromTagValue(8));
    }

    @Test
    void testFromTagValueInvalid() {
        assertEquals(ExifOrientation.TOP_LEFT, ExifOrientation.fromTagValue(0));
        assertEquals(ExifOrientation.TOP_LEFT, ExifOrientation.fromTagValue(9));
        assertEquals(ExifOrientation.TOP_LEFT, ExifOrientation.fromTagValue(-1));
    }

    @Test
    void testRotationDegreesForAllOrientations() {
        assertEquals(0, ExifOrientation.TOP_LEFT.getRotationDegrees());
        assertEquals(0, ExifOrientation.TOP_RIGHT.getRotationDegrees());
        assertEquals(180, ExifOrientation.BOTTOM_RIGHT.getRotationDegrees());
        assertEquals(0, ExifOrientation.BOTTOM_LEFT.getRotationDegrees());
        assertEquals(270, ExifOrientation.LEFT_TOP.getRotationDegrees());
        assertEquals(90, ExifOrientation.RIGHT_TOP.getRotationDegrees());
        assertEquals(90, ExifOrientation.RIGHT_BOTTOM.getRotationDegrees());
        assertEquals(270, ExifOrientation.LEFT_BOTTOM.getRotationDegrees());
    }
}
