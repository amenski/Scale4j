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

import java.awt.image.BufferedImage;

/**
 * Utility methods for working with BufferedImage types.
 */
public final class ImageTypeUtils {

    private ImageTypeUtils() {
        // Utility class
    }

    /**
     * Returns a safe image type for creating new BufferedImages.
     * TYPE_CUSTOM is not suitable for creating new images and should be replaced
     * with a standard type like TYPE_INT_RGB or TYPE_INT_ARGB.
     *
     * @param imageType the original BufferedImage type
     * @return a safe image type suitable for creating new BufferedImages
     */
    public static int getSafeImageType(int imageType) {
        // TYPE_CUSTOM is used for custom/codec-specific image types
        // and cannot be used to create new BufferedImages
        if (imageType == BufferedImage.TYPE_CUSTOM) {
            return BufferedImage.TYPE_INT_RGB;
        }
        return imageType;
    }

    /**
     * Returns a safe image type with alpha support if the original had alpha.
     * TYPE_CUSTOM is handled as described in {@link #getSafeImageType(int)}.
     *
     * @param imageType the original BufferedImage type
     * @param hasAlpha true if the image has alpha channel
     * @return a safe image type suitable for creating new BufferedImages
     */
    public static int getSafeImageType(int imageType, boolean hasAlpha) {
        int safeType = getSafeImageType(imageType);
        
        // If original had alpha but we got a non-alpha type, use ARGB
        if (hasAlpha && safeType != BufferedImage.TYPE_INT_ARGB 
                && safeType != BufferedImage.TYPE_4BYTE_ABGR
                && safeType != BufferedImage.TYPE_4BYTE_ABGR_PRE) {
            return BufferedImage.TYPE_INT_ARGB;
        }
        
        return safeType;
    }

    /**
     * Checks if the given image type supports alpha transparency.
     *
     * @param imageType the BufferedImage type
     * @return true if the type supports alpha
     */
    public static boolean hasAlpha(int imageType) {
        switch (imageType) {
            case BufferedImage.TYPE_INT_ARGB:
            case BufferedImage.TYPE_INT_ARGB_PRE:
            case BufferedImage.TYPE_4BYTE_ABGR:
            case BufferedImage.TYPE_4BYTE_ABGR_PRE:
                return true;
            case BufferedImage.TYPE_BYTE_GRAY:
            case BufferedImage.TYPE_USHORT_GRAY:
            case BufferedImage.TYPE_BYTE_BINARY:
            case BufferedImage.TYPE_BYTE_INDEXED:
                // Some indexed types may have alpha depending on ColorModel
                // Grayscale and binary typically don't have alpha
                return false;
            default:
                return false;
        }
    }
}
