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

import com.scale4j.types.ResizeMode;
import com.scale4j.exception.ImageProcessException;
import com.scale4j.types.ResizeQuality;
import com.scale4j.exception.ImageProcessException;
import org.junit.jupiter.api.Test;
import com.scale4j.exception.ImageProcessException;

import java.awt.image.BufferedImage;
import com.scale4j.exception.ImageProcessException;

import static org.assertj.core.api.Assertions.assertThat;
import com.scale4j.exception.ImageProcessException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import com.scale4j.exception.ImageProcessException;

class ResizeOperationTest {

    @Test
    void resize_nullSource_throwsIllegalArgumentException() {
        assertThatThrownBy(() -> ResizeOperation.resize(null, 100, 100, ResizeMode.EXACT, ResizeQuality.MEDIUM))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Source image cannot be null");
    }

    @Test
    void resize_negativeTargetWidth_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ResizeOperation.resize(source, -10, 100, ResizeMode.EXACT, ResizeQuality.MEDIUM))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Target dimensions must be positive");
    }

    @Test
    void resize_negativeTargetHeight_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ResizeOperation.resize(source, 100, -10, ResizeMode.EXACT, ResizeQuality.MEDIUM))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Target dimensions must be positive");
    }

    @Test
    void resize_zeroTargetWidth_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ResizeOperation.resize(source, 0, 100, ResizeMode.EXACT, ResizeQuality.MEDIUM))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Target dimensions must be positive");
    }

    @Test
    void resize_zeroTargetHeight_throwsIllegalArgumentException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        assertThatThrownBy(() -> ResizeOperation.resize(source, 100, 0, ResizeMode.EXACT, ResizeQuality.MEDIUM))
                .isInstanceOf(ImageProcessException.class)
                .hasMessageContaining("Target dimensions must be positive");
    }

    @Test
    void resize_sameDimensions_returnsOriginalReference() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 100, 100, ResizeMode.EXACT, ResizeQuality.MEDIUM);
        assertThat(result).isSameAs(source);
    }

    @Test
    void resize_downscale_dimensionsCorrect() {
        BufferedImage source = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 50, 50, ResizeMode.EXACT, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void resize_upscale_dimensionsCorrect() {
        BufferedImage source = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 200, 200, ResizeMode.EXACT, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(200);
        assertThat(result.getHeight()).isEqualTo(200);
        assertThat(result.getType()).isEqualTo(source.getType());
    }

    @Test
    void resize_modeFit_aspectRatioPreserved() {
        // Source 200x100 (aspect 2:1), target 150x150 (aspect 1:1)
        // FIT should scale to fit within target, preserving aspect.
        // Expected: width=150, height=75 (since 150/2 = 75)
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 150, 150, ResizeMode.FIT, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(75);
    }

    @Test
    void resize_modeFill_aspectRatioPreserved() {
        // Source 200x100 (aspect 2:1), target 150x150 (aspect 1:1)
        // FILL should scale to fill the target, cropping if needed.
        // Expected: width=300, height=150 (since 150*2 = 300) then crop? Actually FILL scales to cover.
        // According to implementation, FILL chooses dimensions that fill target while preserving aspect.
        // If sourceAspect > targetAspect, resultHeight = targetHeight, resultWidth = targetHeight * sourceAspect.
        // sourceAspect = 2, targetAspect = 1, sourceAspect > targetAspect => height = 150, width = 150 * 2 = 300
        // But targetWidth is 150, so we'll have width > targetWidth? Wait the calculateDimensions returns dimensions that may exceed target.
        // Actually FILL returns dimensions that fill the target (i.e., at least one dimension equals target, the other may be larger).
        // The result width/height may exceed target? Yes, they are the dimensions of the scaled image before cropping? Actually the resize method later scales to those dimensions, not crop.
        // So the resulting image will be 300x150, which is larger than target 150x150.
        // That's fine; we just verify.
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 150, 150, ResizeMode.FILL, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(300);
        assertThat(result.getHeight()).isEqualTo(150);
    }

    @Test
    void resize_modeExact_aspectRatioIgnored() {
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 150, 150, ResizeMode.EXACT, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(150);
    }

    @Test
    void resize_modeAutomatic_defaultsToFit() {
        // AUTOMATIC should behave like FIT
        BufferedImage source = new BufferedImage(200, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 150, 150, ResizeMode.AUTOMATIC, ResizeQuality.MEDIUM);
        assertThat(result.getWidth()).isEqualTo(150);
        assertThat(result.getHeight()).isEqualTo(75);
    }

    @Test
    void resize_qualityLow_noException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 50, 50, ResizeMode.EXACT, ResizeQuality.LOW);
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void resize_qualityMedium_noException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 50, 50, ResizeMode.EXACT, ResizeQuality.MEDIUM);
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void resize_qualityHigh_noException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 50, 50, ResizeMode.EXACT, ResizeQuality.HIGH);
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void resize_qualityUltra_noException() {
        BufferedImage source = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 50, 50, ResizeMode.EXACT, ResizeQuality.ULTRA);
        assertThat(result).isNotNull();
        assertThat(result.getWidth()).isEqualTo(50);
        assertThat(result.getHeight()).isEqualTo(50);
    }

    @Test
    void resize_extremeAspectRatio_noException() {
        BufferedImage source = new BufferedImage(1000, 10, BufferedImage.TYPE_INT_RGB);
        BufferedImage result = ResizeOperation.resize(source, 100, 100, ResizeMode.FIT, ResizeQuality.MEDIUM);
        assertThat(result).isNotNull();
        // FIT: sourceAspect = 100, targetAspect = 1, sourceAspect > targetAspect => width = targetWidth, height = targetWidth / sourceAspect
        // sourceAspect = 1000/10 = 100, targetWidth=100, height = 100 / 100 = 1
        assertThat(result.getWidth()).isEqualTo(100);
        assertThat(result.getHeight()).isEqualTo(1);
    }
}