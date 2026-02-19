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
package com.scale4j.util;

import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for ImageTypeUtils.
 */
class ImageTypeUtilsTest {

    @Test
    void testGetSafeImageTypeCustom() {
        // TYPE_CUSTOM should be converted to TYPE_INT_RGB
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_CUSTOM);
        assertEquals(BufferedImage.TYPE_INT_RGB, result);
    }

    @Test
    void testGetSafeImageTypeIntRgb() {
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_RGB);
        assertEquals(BufferedImage.TYPE_INT_RGB, result);
    }

    @Test
    void testGetSafeImageTypeIntArgb() {
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_ARGB);
        assertEquals(BufferedImage.TYPE_INT_ARGB, result);
    }

    @Test
    void testGetSafeImageTypeIntBgr() {
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_BGR);
        assertEquals(BufferedImage.TYPE_INT_BGR, result);
    }

    @Test
    void testGetSafeImageType3ByteBgr() {
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_3BYTE_BGR);
        assertEquals(BufferedImage.TYPE_3BYTE_BGR, result);
    }

    @Test
    void testGetSafeImageTypeByteGray() {
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_BYTE_GRAY);
        assertEquals(BufferedImage.TYPE_BYTE_GRAY, result);
    }

    @Test
    void testGetSafeImageTypeWithAlphaTrue() {
        // TYPE_INT_RGB with alpha requested should become TYPE_INT_ARGB
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_RGB, true);
        assertEquals(BufferedImage.TYPE_INT_ARGB, result);
    }

    @Test
    void testGetSafeImageTypeWithAlphaFalse() {
        // TYPE_INT_RGB with no alpha should stay TYPE_INT_RGB
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_RGB, false);
        assertEquals(BufferedImage.TYPE_INT_RGB, result);
    }

    @Test
    void testGetSafeImageTypeWithAlphaTrueAlreadyArgb() {
        // TYPE_INT_ARGB already has alpha, should stay
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_INT_ARGB, true);
        assertEquals(BufferedImage.TYPE_INT_ARGB, result);
    }

    @Test
    void testGetSafeImageTypeWithAlphaTrueCustom() {
        // TYPE_CUSTOM with alpha should become TYPE_INT_ARGB
        int result = ImageTypeUtils.getSafeImageType(BufferedImage.TYPE_CUSTOM, true);
        assertEquals(BufferedImage.TYPE_INT_ARGB, result);
    }

    @Test
    void testHasAlphaIntArgb() {
        assertTrue(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_INT_ARGB));
    }

    @Test
    void testHasAlphaIntArgbPre() {
        assertTrue(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_INT_ARGB_PRE));
    }

    @Test
    void testHasAlpha4ByteAbgr() {
        assertTrue(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_4BYTE_ABGR));
    }

    @Test
    void testHasAlpha4ByteAbgrPre() {
        assertTrue(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_4BYTE_ABGR_PRE));
    }

    @Test
    void testHasAlphaIntRgb() {
        assertFalse(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_INT_RGB));
    }

    @Test
    void testHasAlphaIntBgr() {
        assertFalse(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_INT_BGR));
    }

    @Test
    void testHasAlpha3ByteBgr() {
        assertFalse(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_3BYTE_BGR));
    }

    @Test
    void testHasAlphaByteGray() {
        // Standard grayscale has no alpha channel
        boolean result = ImageTypeUtils.hasAlpha(BufferedImage.TYPE_BYTE_GRAY);
        assertFalse(result);
    }

    @Test
    void testHasAlphaUshortGray() {
        // Standard grayscale has no alpha channel
        boolean result = ImageTypeUtils.hasAlpha(BufferedImage.TYPE_USHORT_GRAY);
        assertFalse(result);
    }

    @Test
    void testHasAlphaByteBinary() {
        // Binary (1-bit) has no alpha channel
        boolean result = ImageTypeUtils.hasAlpha(BufferedImage.TYPE_BYTE_BINARY);
        assertFalse(result);
    }

    @Test
    void testHasAlphaByteIndexed() {
        // Indexed can have alpha depending on color model, but base type doesn't guarantee it
        boolean result = ImageTypeUtils.hasAlpha(BufferedImage.TYPE_BYTE_INDEXED);
        // This tests the switch default case
        assertFalse(result);
    }

    @Test
    void testHasAlphaCustom() {
        assertFalse(ImageTypeUtils.hasAlpha(BufferedImage.TYPE_CUSTOM));
    }

    @Test
    void testAllCommonTypes() {
        // Test that common types don't cause exceptions
        int[] types = {
            BufferedImage.TYPE_INT_RGB,
            BufferedImage.TYPE_INT_ARGB,
            BufferedImage.TYPE_INT_ARGB_PRE,
            BufferedImage.TYPE_INT_BGR,
            BufferedImage.TYPE_3BYTE_BGR,
            BufferedImage.TYPE_4BYTE_ABGR,
            BufferedImage.TYPE_4BYTE_ABGR_PRE,
            BufferedImage.TYPE_USHORT_565_RGB,
            BufferedImage.TYPE_USHORT_555_RGB,
            BufferedImage.TYPE_BYTE_GRAY,
            BufferedImage.TYPE_USHORT_GRAY,
            BufferedImage.TYPE_BYTE_BINARY,
            BufferedImage.TYPE_BYTE_INDEXED,
            BufferedImage.TYPE_CUSTOM
        };

        for (int type : types) {
            int safe = ImageTypeUtils.getSafeImageType(type);
            assertTrue(safe >= 0, "Safe type should be valid for input: " + type);
        }
    }
}
