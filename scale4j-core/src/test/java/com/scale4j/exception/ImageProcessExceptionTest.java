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
 * Tests for ImageProcessException.
 */
class ImageProcessExceptionTest {

    @Test
    void shouldCreateExceptionWithNoMessage() {
        // When
        ImageProcessException exception = new ImageProcessException();

        // Then
        assertThat(exception).hasMessage(null);
        assertThat(exception.getOperation()).isNull();
        assertThat(exception.getImageWidth()).isEqualTo(-1);
        assertThat(exception.getImageHeight()).isEqualTo(-1);
    }

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Failed to process image";

        // When
        ImageProcessException exception = new ImageProcessException(message);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getOperation()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Failed to process image";
        Throwable cause = new RuntimeException("Processing error");

        // When
        ImageProcessException exception = new ImageProcessException(message, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateExceptionWithOperation() {
        // Given
        String message = "Failed to resize";
        String operation = "resize";

        // When
        ImageProcessException exception = new ImageProcessException(message, operation);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getOperation()).isEqualTo(operation);
    }

    @Test
    void shouldCreateExceptionWithOperationAndCause() {
        // Given
        String message = "Failed to resize";
        String operation = "resize";
        Throwable cause = new RuntimeException("Processing error");

        // When
        ImageProcessException exception = new ImageProcessException(message, operation, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getOperation()).isEqualTo(operation);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateExceptionWithFullInfo() {
        // Given
        String message = "Failed to resize";
        String operation = "resize";
        int width = 100;
        int height = 200;

        // When
        ImageProcessException exception = new ImageProcessException(message, operation, width, height);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getOperation()).isEqualTo(operation);
        assertThat(exception.getImageWidth()).isEqualTo(width);
        assertThat(exception.getImageHeight()).isEqualTo(height);
    }

    @Test
    void shouldCreateExceptionWithFullInfoAndCause() {
        // Given
        String message = "Failed to resize";
        String operation = "resize";
        int width = 100;
        int height = 200;
        Throwable cause = new RuntimeException("Processing error");

        // When
        ImageProcessException exception = new ImageProcessException(message, operation, width, height, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getOperation()).isEqualTo(operation);
        assertThat(exception.getImageWidth()).isEqualTo(width);
        assertThat(exception.getImageHeight()).isEqualTo(height);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldIncludeOperationAndImageInfoInToString() {
        // Given
        String message = "Failed to resize";
        String operation = "resize";
        int width = 100;
        int height = 200;

        // When
        ImageProcessException exception = new ImageProcessException(message, operation, width, height);

        // Then
        String str = exception.toString();
        assertThat(str).contains("ImageProcessException:");
        assertThat(str).contains(message);
        assertThat(str).contains(operation);
        assertThat(str).contains("100x200");
    }
}
