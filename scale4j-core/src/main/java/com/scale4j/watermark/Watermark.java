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

import java.awt.image.BufferedImage;

/**
 * Interface for watermarks that can be applied to images.
 */
public interface Watermark {

    /**
     * Applies this watermark to the target image.
     *
     * @param target the image to apply the watermark to
     */
    void apply(BufferedImage target);

    /**
     * Returns the position where the watermark should be placed.
     *
     * @return the watermark position
     */
    WatermarkPosition getPosition();

    /**
     * Returns the opacity of the watermark (0.0 to 1.0).
     *
     * @return the opacity
     */
    float getOpacity();
}
