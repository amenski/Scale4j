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
package com.scale4j.types;

/**
 * Defines how the image should be scaled when resizing.
 */
public enum ResizeMode {

    /**
     * Automatically determines the best approach based on the aspect ratio.
     * Maintains aspect ratio and fits within the target dimensions.
     */
    AUTOMATIC,

    /**
     * Scales the image to fit within the target dimensions while maintaining aspect ratio.
     * The resulting image may be smaller than target dimensions.
     */
    FIT,

    /**
     * Scales the image to fill the target dimensions while maintaining aspect ratio.
     * The resulting image may be cropped.
     */
    FILL,

    /**
     * Forces the image to exactly match the target dimensions.
     * May distort the image if aspect ratios don't match.
     */
    EXACT
}
