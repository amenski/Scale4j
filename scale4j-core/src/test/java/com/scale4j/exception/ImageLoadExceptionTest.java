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
package com.scale4j.exception;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for ImageLoadException.
 */
class ImageLoadExceptionTest {

    @Test
    void shouldCreateExceptionWithNoMessage() {
        // When
        ImageLoadException exception = new ImageLoadException();

        // Then
        assertThat(exception).hasMessage(null);
        assertThat(exception.getSourcePath()).isNull();
        assertThat(exception.getSourceType()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Failed to load image";

        // When
        ImageLoadException exception = new ImageLoadException(message);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getSourcePath()).isNull();
        assertThat(exception.getSourceType()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Failed to load image";
        Throwable cause = new RuntimeException("IO Error");

        // When
        ImageLoadException exception = new ImageLoadException(message, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateExceptionWithSourceInfo() {
        // Given
        String message = "Failed to load image";
        String sourcePath = "/path/to/image.jpg";
        String sourceType = "file";

        // When
        ImageLoadException exception = new ImageLoadException(message, sourcePath, sourceType);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getSourcePath()).isEqualTo(sourcePath);
        assertThat(exception.getSourceType()).isEqualTo(sourceType);
    }

    @Test
    void shouldCreateExceptionWithFullInfo() {
        // Given
        String message = "Failed to load image";
        String sourcePath = "/path/to/image.jpg";
        String sourceType = "file";
        Throwable cause = new RuntimeException("IO Error");

        // When
        ImageLoadException exception = new ImageLoadException(message, sourcePath, sourceType, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getSourcePath()).isEqualTo(sourcePath);
        assertThat(exception.getSourceType()).isEqualTo(sourceType);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldIncludeSourceInfoInToString() {
        // Given
        String message = "Failed to load image";
        String sourcePath = "/path/to/image.jpg";
        String sourceType = "file";

        // When
        ImageLoadException exception = new ImageLoadException(message, sourcePath, sourceType);

        // Then
        String str = exception.toString();
        assertThat(str).contains("ImageLoadException:");
        assertThat(str).contains(message);
        assertThat(str).contains(sourcePath);
        assertThat(str).contains(sourceType);
    }
}
