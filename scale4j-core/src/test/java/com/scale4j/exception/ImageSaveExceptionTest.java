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
 * Tests for ImageSaveException.
 */
class ImageSaveExceptionTest {

    @Test
    void shouldCreateExceptionWithNoMessage() {
        // When
        ImageSaveException exception = new ImageSaveException();

        // Then
        assertThat(exception).hasMessage(null);
        assertThat(exception.getDestinationPath()).isNull();
        assertThat(exception.getFormat()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Failed to save image";

        // When
        ImageSaveException exception = new ImageSaveException(message);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getDestinationPath()).isNull();
        assertThat(exception.getFormat()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Failed to save image";
        Throwable cause = new RuntimeException("IO Error");

        // When
        ImageSaveException exception = new ImageSaveException(message, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateExceptionWithDestinationInfo() {
        // Given
        String message = "Failed to save image";
        String destinationPath = "/path/to/output.jpg";
        String format = "jpg";

        // When
        ImageSaveException exception = new ImageSaveException(message, destinationPath, format);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getDestinationPath()).isEqualTo(destinationPath);
        assertThat(exception.getFormat()).isEqualTo(format);
    }

    @Test
    void shouldCreateExceptionWithFullInfo() {
        // Given
        String message = "Failed to save image";
        String destinationPath = "/path/to/output.jpg";
        String format = "jpg";
        Throwable cause = new RuntimeException("IO Error");

        // When
        ImageSaveException exception = new ImageSaveException(message, destinationPath, format, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getDestinationPath()).isEqualTo(destinationPath);
        assertThat(exception.getFormat()).isEqualTo(format);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldIncludeDestinationAndFormatInToString() {
        // Given
        String message = "Failed to save image";
        String destinationPath = "/path/to/output.jpg";
        String format = "jpg";

        // When
        ImageSaveException exception = new ImageSaveException(message, destinationPath, format);

        // Then
        String str = exception.toString();
        assertThat(str).contains("ImageSaveException:");
        assertThat(str).contains(message);
        assertThat(str).contains(destinationPath);
        assertThat(str).contains(format);
    }
}
