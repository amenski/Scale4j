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

/**
 * Base exception class for all Scale4j exceptions.
 * Provides detailed error messages and optional cause chain support.
 *
 * <p>This is the root exception in the Scale4j exception hierarchy, allowing
 * applications to catch all Scale4j-specific exceptions with a single catch block.</p>
 *
 * @author Scale4j
 * @version 1.0.0
 */
public class Scale4jException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * Constructs a new Scale4jException with no detail message.
     */
    public Scale4jException() {
        super();
    }

    /**
     * Constructs a new Scale4jException with the specified detail message.
     *
     * @param message the detail message
     */
    public Scale4jException(String message) {
        super(message);
    }

    /**
     * Constructs a new Scale4jException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public Scale4jException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new Scale4jException with the specified cause.
     *
     * @param cause the cause
     */
    public Scale4jException(Throwable cause) {
        super(cause);
    }
}
