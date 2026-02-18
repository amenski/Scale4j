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
package com.scale4j.watermark;

import com.scale4j.exception.ImageProcessException;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

/**
 * An image watermark that can be applied to other images.
 */
public final class ImageWatermark implements Watermark {

    private final BufferedImage image;
    private final WatermarkPosition position;
    private final float opacity;
    private final float scale;

    private ImageWatermark(Builder builder) {
        this.image = builder.image;
        this.position = builder.position;
        this.opacity = builder.opacity;
        this.scale = builder.scale;
    }

    /**
     * Creates a new builder for ImageWatermark.
     *
     * @return a new builder
     */
    public static Builder builder() {
        return new Builder();
    }

    @Override
    public void apply(BufferedImage target) {
        if (target == null) {
            throw new ImageProcessException("Target image cannot be null", "apply");
        }
        if (image == null) {
            throw new ImageProcessException("Watermark image cannot be null", "apply");
        }

        // Calculate scaled dimensions
        int watermarkWidth = (int) (image.getWidth() * scale);
        int watermarkHeight = (int) (image.getHeight() * scale);

        // Scale the watermark image
        BufferedImage scaledWatermark = new BufferedImage(watermarkWidth, watermarkHeight, image.getType());
        Graphics2D g2d = scaledWatermark.createGraphics();
        g2d.drawImage(image, 0, 0, watermarkWidth, watermarkHeight, null);
        g2d.dispose();

        // Calculate position
        int[] coords = position.calculate(target.getWidth(), target.getHeight(), watermarkWidth, watermarkHeight);
        int x = coords[0];
        int y = coords[1];

        // Apply watermark
        Graphics2D targetG2d = target.createGraphics();
        targetG2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        targetG2d.drawImage(scaledWatermark, x, y, null);
        targetG2d.dispose();
    }

    @Override
    public WatermarkPosition getPosition() {
        return position;
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    public BufferedImage getImage() {
        return image;
    }

    public float getScale() {
        return scale;
    }

    /**
     * Builder for ImageWatermark.
     */
    public static final class Builder {

        private BufferedImage image;
        private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;
        private float opacity = 0.5f;
        private float scale = 0.25f;

        private Builder() {
        }

        public Builder image(BufferedImage image) {
            if (image == null) {
                throw new ImageProcessException("Watermark image cannot be null", "image");
            }
            this.image = image;
            return this;
        }

        public Builder position(WatermarkPosition position) {
            if (position == null) {
                throw new ImageProcessException("Position cannot be null", "position");
            }
            this.position = position;
            return this;
        }

        public Builder opacity(float opacity) {
            if (opacity < 0.0f || opacity > 1.0f) {
                throw new ImageProcessException("Opacity must be between 0.0 and 1.0", "opacity");
            }
            this.opacity = opacity;
            return this;
        }

        public Builder scale(float scale) {
            if (scale <= 0.0f || scale > 1.0f) {
                throw new ImageProcessException("Scale must be between 0.0 and 1.0", "scale");
            }
            this.scale = scale;
            return this;
        }

        public ImageWatermark build() {
            if (image == null) {
                throw new ImageProcessException("Watermark image must be set", "build");
            }
            return new ImageWatermark(this);
        }
    }
}
