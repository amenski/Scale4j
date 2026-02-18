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

/**
 * No-operation implementation of Scale4jLogger.
 * 
 * <p>This implementation does nothing - all log calls are effectively ignored.
 * It is used when no logging framework is available on the classpath.</p>
 *
 * @author Scale4j
 * @version 5.0.0
 */
public final class NoOpScale4jLogger implements Scale4jLogger {

    private final String name;

    /**
     * Creates a new NoOpScale4jLogger with the specified name.
     *
     * @param name the logger name
     */
    public NoOpScale4jLogger(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isTraceEnabled() {
        return false;
    }

    @Override
    public void trace(String message) {
        // No-op
    }

    @Override
    public void trace(String message, Object... args) {
        // No-op
    }

    @Override
    public void trace(String message, Throwable throwable) {
        // No-op
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
    }

    @Override
    public void debug(String message) {
        // No-op
    }

    @Override
    public void debug(String message, Object... args) {
        // No-op
    }

    @Override
    public void debug(String message, Throwable throwable) {
        // No-op
    }

    @Override
    public boolean isInfoEnabled() {
        return false;
    }

    @Override
    public void info(String message) {
        // No-op
    }

    @Override
    public void info(String message, Object... args) {
        // No-op
    }

    @Override
    public void info(String message, Throwable throwable) {
        // No-op
    }

    @Override
    public boolean isWarnEnabled() {
        return false;
    }

    @Override
    public void warn(String message) {
        // No-op
    }

    @Override
    public void warn(String message, Object... args) {
        // No-op
    }

    @Override
    public void warn(String message, Throwable throwable) {
        // No-op
    }

    @Override
    public boolean isErrorEnabled() {
        return false;
    }

    @Override
    public void error(String message) {
        // No-op
    }

    @Override
    public void error(String message, Object... args) {
        // No-op
    }

    @Override
    public void error(String message, Throwable throwable) {
        // No-op
    }
}
