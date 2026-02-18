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

public enum ExifOrientation {
    TOP_LEFT(1, 0, false, false),
    TOP_RIGHT(2, 0, true, false),
    BOTTOM_RIGHT(3, 180, false, false),
    BOTTOM_LEFT(4, 0, false, true),
    LEFT_TOP(5, 270, true, false),
    RIGHT_TOP(6, 90, false, false),
    RIGHT_BOTTOM(7, 90, true, false),
    LEFT_BOTTOM(8, 270, false, false);

    private final int tagValue;
    private final int rotationDegrees;
    private final boolean flipHorizontal;
    private final boolean flipVertical;

    ExifOrientation(int tagValue, int rotationDegrees, boolean flipHorizontal, boolean flipVertical) {
        this.tagValue = tagValue;
        this.rotationDegrees = rotationDegrees;
        this.flipHorizontal = flipHorizontal;
        this.flipVertical = flipVertical;
    }

    public int getTagValue() {
        return tagValue;
    }

    public int getRotationDegrees() {
        return rotationDegrees;
    }

    public boolean isFlipHorizontal() {
        return flipHorizontal;
    }

    public boolean isFlipVertical() {
        return flipVertical;
    }

    public static ExifOrientation fromTagValue(int value) {
        for (ExifOrientation orientation : values()) {
            if (orientation.tagValue == value) {
                return orientation;
            }
        }
        return TOP_LEFT;
    }

    public boolean requiresTransformation() {
        return rotationDegrees != 0 || flipHorizontal || flipVertical;
    }
}
