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
 * Defines the quality/speed trade-off for image resizing operations.
 */
public enum ResizeQuality {

    /**
     * Fastest resizing with lowest quality.
     * Uses nearest-neighbor interpolation.
     * Suitable for thumbnails or when speed is critical.
     */
    LOW,

    /**
     * Balanced quality and speed.
     * Uses bilinear interpolation.
     * Good default for most use cases.
     */
    MEDIUM,

    /**
     * High quality with reasonable speed.
     * Uses bicubic interpolation.
     * Produces smooth results with anti-aliasing.
     */
    HIGH,

    /**
     * Highest quality with slowest speed.
     * Uses advanced interpolation algorithms.
     * Best for print or high-quality display.
     */
    ULTRA
}
