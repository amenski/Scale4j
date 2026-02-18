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

import org.junit.jupiter.api.Test;

import java.awt.Color;
import java.awt.image.BufferedImage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RotateOperationTest {

    @Test
    void rotate_nullSource_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> RotateOperation.rotate(null, 45.0))
                .isInstanceOf(ImageProcessException.class)
                .hasMessage("Source image cannot be null");
    }

    @Test
    void rotate_positiveDegrees_noException() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 45.0);
        assertThat(result).isNotNull();
        // After 45-degree rotation, dimensions increase
        assertThat(result.getWidth()).isGreaterThanOrEqualTo(100);
        assertThat(result.getHeight()).isGreaterThanOrEqualTo(50);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void rotate_negativeDegrees_normalized() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, -90.0);
        // -90 degrees should be equivalent to 270 degrees (counter-clockwise 90)
        // The implementation has special case for 270 degrees? Actually it checks for 270 with tolerance.
        // Since -90 normalized to 270, it will go to rotate90CounterClockwise.
        assertThat(result).isNotNull();
        // 270 rotation swaps width/height (same as 90 clockwise? Actually 270 counter-clockwise is same as 90 clockwise but dimensions swapped)
        // The result width = source height, height = source width
        assertThat(result.getWidth()).isEqualTo(50); // source height
        assertThat(result.getHeight()).isEqualTo(100); // source width
    }

    @Test
    void rotate_90Degrees_swapDimensions() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 90.0);
        assertThat(result.getWidth()).isEqualTo(50); // height becomes width
        assertThat(result.getHeight()).isEqualTo(100); // width becomes height
    }

    @Test
    void rotate_180Degrees_sameDimensions() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 180.0);
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void rotate_270Degrees_swapDimensions() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 270.0);
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(100);
    }

    @Test
    void rotate_0Degrees_noChangeInDimensions() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 0.0);
        // Should produce same dimensions (but new image)
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void rotate_360Degrees_normalizedTo0() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 360.0);
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void rotate_withBackgroundColor() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        Color bg = Color.RED;
        BufferedImage result = RotateOperation.rotate(source, 45.0, bg);
        assertThat(result).isNotNull();
        // Cannot easily verify background color, but ensure no exception
    }

    @Test
    void rotate_withNullBackground() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 45.0, null);
        assertThat(result).isNotNull();
    }

    @Test
    void rotate_generalRotation_boundingBox() {
        // For a square image rotated 45°, bounding box dimensions = side * sqrt(2) approx.
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 45.0);
        double expected = 100 * Math.sqrt(2); // ~141.42
        assertThat(result.getWidth()).isBetween(141, 142);
        assertThat(result.getHeight()).isBetween(141, 142);
    }

    @Test
    void rotate_largeDegrees_normalized() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 450.0); // 450 - 360 = 90
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(100);
    }

    @Test
    void rotate_smallAngle_dimensionsIncreaseSlightly() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = RotateOperation.rotate(source, 5.0);
        // Width and height should be slightly larger
        assertThat(result.getWidth()).isGreaterThan(100);
        assertThat(result.getHeight()).isGreaterThan(50);
    }

    @Test
    void rotate_preservesImageType() {
        BufferedImage source = new BufferedImage(100, 50, BufferedImage.TYPE_INT_ARGB);
        BufferedImage result = RotateOperation.rotate(source, 30.0);
        assertThat(result.getType()).isEqualTo(source.getType());
    }
}