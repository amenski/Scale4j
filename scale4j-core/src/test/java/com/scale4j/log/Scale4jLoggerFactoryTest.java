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
 * Tests for Scale4jLoggerFactory.
 */
class Scale4jLoggerFactoryTest {

    @Test
    void shouldReturnSingletonInstance() {
        // When
        Scale4jLoggerFactory instance1 = Scale4jLoggerFactory.getInstance();
        Scale4jLoggerFactory instance2 = Scale4jLoggerFactory.getInstance();

        // Then
        assertThat(instance1).isSameAs(instance2);
    }

    @Test
    void shouldGetLoggerByClass() {
        // When
        Scale4jLogger logger = Scale4jLoggerFactory.getInstance().getLogger(String.class);

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo(String.class.getName());
    }

    @Test
    void shouldGetLoggerByName() {
        // Given
        String loggerName = "TestLogger";

        // When
        Scale4jLogger logger = Scale4jLoggerFactory.getInstance().getLogger(loggerName);

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo(loggerName);
    }

    @Test
    void shouldHandleNullClass() {
        // When
        Scale4jLogger logger = Scale4jLoggerFactory.getInstance().getLogger((Class<?>) null);

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo("Scale4j");
    }

    @Test
    void shouldHandleNullName() {
        // When
        Scale4jLogger logger = Scale4jLoggerFactory.getInstance().getLogger((String) null);

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo("Scale4j");
    }

    @Test
    void shouldHandleEmptyName() {
        // When
        Scale4jLogger logger = Scale4jLoggerFactory.getInstance().getLogger("");

        // Then
        assertThat(logger).isNotNull();
        assertThat(logger.getName()).isEqualTo("Scale4j");
    }

    @Test
    void shouldCacheLoggers() {
        // Given
        Scale4jLoggerFactory factory = Scale4jLoggerFactory.getInstance();
        String loggerName = "CachedLogger";

        // When
        Scale4jLogger logger1 = factory.getLogger(loggerName);
        Scale4jLogger logger2 = factory.getLogger(loggerName);

        // Then - loggers should be cached (same instance)
        assertThat(logger1).isSameAs(logger2);
    }

    @Test
    void shouldReturnDifferentLoggersForDifferentNames() {
        // When
        Scale4jLogger logger1 = Scale4jLoggerFactory.getInstance().getLogger("Logger1");
        Scale4jLogger logger2 = Scale4jLoggerFactory.getInstance().getLogger("Logger2");

        // Then
        assertThat(logger1).isNotSameAs(logger2);
        assertThat(logger1.getName()).isEqualTo("Logger1");
        assertThat(logger2.getName()).isEqualTo("Logger2");
    }

    @Test
    void shouldCheckSlf4jAvailability() {
        // When
        boolean available = Scale4jLoggerFactory.getInstance().isSlf4jAvailable();

        // Then - SLF4J should be available in test environment
        assertThat(available).isTrue();
    }
}
