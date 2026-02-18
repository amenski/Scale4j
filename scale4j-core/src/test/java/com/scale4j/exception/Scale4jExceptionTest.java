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
 * Tests for Scale4jException.
 */
class Scale4jExceptionTest {

    @Test
    void shouldCreateExceptionWithNoMessage() {
        // When
        Scale4jException exception = new Scale4jException();

        // Then
        assertThat(exception).hasMessage(null);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessage() {
        // Given
        String message = "Test error message";

        // When
        Scale4jException exception = new Scale4jException(message);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getCause()).isNull();
    }

    @Test
    void shouldCreateExceptionWithMessageAndCause() {
        // Given
        String message = "Test error message";
        Throwable cause = new RuntimeException("Original cause");

        // When
        Scale4jException exception = new Scale4jException(message, cause);

        // Then
        assertThat(exception).hasMessage(message);
        assertThat(exception.getCause()).isEqualTo(cause);
    }

    @Test
    void shouldCreateExceptionWithCauseOnly() {
        // Given
        Throwable cause = new RuntimeException("Original cause");

        // When
        Scale4jException exception = new Scale4jException(cause);

        // Then
        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
