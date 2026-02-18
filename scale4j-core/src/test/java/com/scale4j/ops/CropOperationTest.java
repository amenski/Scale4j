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
import com.scale4j.exception.ImageProcessException;

import java.awt.image.BufferedImage;
import com.scale4j.exception.ImageProcessException;

import static org.assertj.core.api.Assertions.assertThat;
import com.scale4j.exception.ImageProcessException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.scale4j.exception.ImageProcessException;

class CropOperationTest {

    @Test
    void crop_nullSource_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> CropOperation.crop(null, 0, 0, 10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Source image cannot be null");
    }

    @Test
    void crop_zeroWidth_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, 0, 0, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Crop dimensions must be positive");
    }

    @Test
    void crop_zeroHeight_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, 0, 10, 0))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Crop dimensions must be positive");
    }

    @Test
    void crop_negativeWidth_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, 0, -10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Crop dimensions must be positive");
    }

    @Test
    void crop_negativeHeight_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, 0, 10, -10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Crop dimensions must be positive");
    }

    @Test
    void crop_negativeX_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, -5, 0, 10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("exceeds image bounds");
    }

    @Test
    void crop_negativeY_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, -5, 10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("exceeds image bounds");
    }

    @Test
    void crop_regionExceedsWidth_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        // x = 95, width = 10 => x+width = 105 > 100
        assertThatThrownBy(() -> CropOperation.crop(source, 95, 0, 10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("exceeds image bounds");
    }

    @Test
    void crop_regionExceedsHeight_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> CropOperation.crop(source, 0, 95, 10, 10))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("exceeds image bounds");
    }

    @Test
    void crop_validRegion_returnsCorrectDimensions() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = CropOperation.crop(source, 10, 20, 30, 40);
        assertThat(result.getWidth()).isEqualTo(30);
        assertThat(result.getHeight()).isEqualTo(40);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void crop_fullImage_returnsSameSize() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = CropOperation.crop(source, 0, 0, 100, 100);
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(100);
        // Not same reference because getSubimage creates a new BufferedImage
        assertThat(result).isNotSameAs(source);
    }

    @Test
    void crop_edgeRegion_noException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        // Crop rightmost 10 columns, bottommost 10 rows
        BufferedImage result = CropOperation.crop(source, 90, 90, 10, 10);
        assertThat(result.getWidth()).isEqualTo(10);
        assertThat(result.getHeight()).isEqualTo(10);
    }

    @Test
    void crop_singlePixel() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = CropOperation.crop(source, 50, 50, 1, 1);
        assertThat(result.getWidth()).isEqualTo(1);
        assertThat(result.getHeight()).isEqualTo(1);
    }
}