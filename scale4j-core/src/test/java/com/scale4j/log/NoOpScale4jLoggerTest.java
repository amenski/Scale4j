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
package com.scale4j.log;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for NoOpScale4jLogger.
 */
class NoOpScale4jLoggerTest {

    @Test
    void shouldCreateLoggerWithName() {
        // Given
        String loggerName = "TestLogger";

        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger(loggerName);

        // Then
        assertThat(logger.getName()).isEqualTo(loggerName);
    }

    @Test
    void shouldReturnCorrectName() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("com.scale4j.Test");

        // Then
        assertThat(logger.getName()).isEqualTo("com.scale4j.Test");
    }

    @Test
    void shouldReturnFalseForTraceEnabled() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then
        assertThat(logger.isTraceEnabled()).isFalse();
    }

    @Test
    void shouldReturnFalseForDebugEnabled() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then
        assertThat(logger.isDebugEnabled()).isFalse();
    }

    @Test
    void shouldReturnFalseForInfoEnabled() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then
        assertThat(logger.isInfoEnabled()).isFalse();
    }

    @Test
    void shouldReturnFalseForWarnEnabled() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then
        assertThat(logger.isWarnEnabled()).isFalse();
    }

    @Test
    void shouldReturnFalseForErrorEnabled() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then
        assertThat(logger.isErrorEnabled()).isFalse();
    }

    @Test
    void shouldNotThrowOnTrace() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then - should not throw
        logger.trace("Test trace message");
        logger.trace("Test trace with arg: {}", "arg");
        logger.trace("Test trace with error", new RuntimeException("test"));
    }

    @Test
    void shouldNotThrowOnDebug() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then - should not throw
        logger.debug("Test debug message");
        logger.debug("Test debug with arg: {}", "arg");
        logger.debug("Test debug with error", new RuntimeException("test"));
    }

    @Test
    void shouldNotThrowOnInfo() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then - should not throw
        logger.info("Test info message");
        logger.info("Test info with arg: {}", "arg");
        logger.info("Test info with error", new RuntimeException("test"));
    }

    @Test
    void shouldNotThrowOnWarn() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then - should not throw
        logger.warn("Test warn message");
        logger.warn("Test warn with arg: {}", "arg");
        logger.warn("Test warn with error", new RuntimeException("test"));
    }

    @Test
    void shouldNotThrowOnError() {
        // When
        NoOpScale4jLogger logger = new NoOpScale4jLogger("TestLogger");

        // Then - should not throw
        logger.error("Test error message");
        logger.error("Test error with arg: {}", "arg");
        logger.error("Test error with error", new RuntimeException("test"));
    }
}
