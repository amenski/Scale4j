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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.font.LineMetrics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * A text watermark that can be applied to images.
 */
public final class TextWatermark implements Watermark {

    private final String text;
    private final Font font;
    private final Color color;
    private final WatermarkPosition position;
    private final float opacity;
    private final Color backgroundColor;
    private final int margin;

    private TextWatermark(Builder builder) {
        this.text = builder.text;
        this.font = builder.font;
        this.color = builder.color;
        this.position = builder.position;
        this.opacity = builder.opacity;
        this.backgroundColor = builder.backgroundColor;
        this.margin = builder.margin;
    }

    /**
     * Creates a simple text watermark with default settings.
     *
     * @param text the watermark text
     * @return a new TextWatermark instance
     */
    public static TextWatermark of(String text) {
        return builder().text(text).build();
    }

    /**
     * Creates a new builder for TextWatermark.
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

        Graphics2D g2d = target.createGraphics();

        // Enable anti-aliasing
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_TEXT_ANTIALIASING,
                java.awt.RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setRenderingHint(java.awt.RenderingHints.KEY_RENDERING,
                java.awt.RenderingHints.VALUE_RENDER_QUALITY);

        // Calculate text dimensions
        FontRenderContext frc = g2d.getFontRenderContext();
        Rectangle2D bounds = font.getStringBounds(text, frc);
        LineMetrics metrics = font.getLineMetrics(text, frc);

        int textWidth = (int) Math.ceil(bounds.getWidth());
        int textHeight = (int) Math.ceil(bounds.getHeight());

        // Calculate position
        int[] coords = position.calculate(target.getWidth(), target.getHeight(), textWidth, textHeight);
        int x = coords[0] + margin;
        int y = (int) (coords[1] - metrics.getDescent() + margin);

        // Apply opacity
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));

        // Draw background if specified
        if (backgroundColor != null) {
            g2d.setColor(backgroundColor);
            g2d.fillRect(x - margin, y - textHeight - margin,
                    textWidth + 2 * margin, textHeight + 2 * margin);
        }

        // Draw text
        g2d.setColor(color);
        g2d.setFont(font);
        g2d.drawString(text, x, y);

        g2d.dispose();
    }

    @Override
    public WatermarkPosition getPosition() {
        return position;
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    public String getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public int getMargin() {
        return margin;
    }

    /**
     * Builder for TextWatermark.
     */
    public static final class Builder {

        private String text = "";
        private Font font = new Font("Arial", Font.PLAIN, 24);
        private Color color = Color.WHITE;
        private WatermarkPosition position = WatermarkPosition.BOTTOM_RIGHT;
        private float opacity = 0.7f;
        private Color backgroundColor = null;
        private int margin = 5;

        private Builder() {
        }

        public Builder text(String text) {
            if (text == null || text.isEmpty()) {
                throw new ImageProcessException("Text cannot be null or empty", "text");
            }
            this.text = text;
            return this;
        }

        public Builder font(Font font) {
            if (font == null) {
                throw new ImageProcessException("Font cannot be null", "font");
            }
            this.font = font;
            return this;
        }

        public Builder font(String family, int style, int size) {
            return font(new Font(family, style, size));
        }

        public Builder color(Color color) {
            if (color == null) {
                throw new ImageProcessException("Color cannot be null", "color");
            }
            this.color = color;
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

        public Builder backgroundColor(Color backgroundColor) {
            this.backgroundColor = backgroundColor;
            return this;
        }

        public Builder margin(int margin) {
            if (margin < 0) {
                throw new ImageProcessException("Margin must be non-negative", "margin");
            }
            this.margin = margin;
            return this;
        }

        public TextWatermark build() {
            return new TextWatermark(this);
        }
    }
}
