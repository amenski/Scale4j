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
 * Exception thrown when an image fails to load.
 * This exception provides detailed information about the loading failure,
 * including the source of the image and the underlying cause.
 *
 * @author Scale4j
 * @version 5.0.0
 */
public class ImageLoadException extends Scale4jException {

    private static final long serialVersionUID = 1L;

    private final String sourcePath;
    private final String sourceType;

    /**
     * Constructs a new ImageLoadException with no detail message.
     */
    public ImageLoadException() {
        super();
        this.sourcePath = null;
        this.sourceType = null;
    }

    /**
     * Constructs a new ImageLoadException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageLoadException(String message) {
        super(message);
        this.sourcePath = null;
        this.sourceType = null;
    }

    /**
     * Constructs a new ImageLoadException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ImageLoadException(String message, Throwable cause) {
        super(message, cause);
        this.sourcePath = null;
        this.sourceType = null;
    }

    /**
     * Constructs a new ImageLoadException with source information.
     *
     * @param message the detail message
     * @param sourcePath the path or URL that failed to load
     * @param sourceType the type of source (e.g., "file", "stream", "url")
     */
    public ImageLoadException(String message, String sourcePath, String sourceType) {
        super(message);
        this.sourcePath = sourcePath;
        this.sourceType = sourceType;
    }

    /**
     * Constructs a new ImageLoadException with source information and cause.
     *
     * @param message the detail message
     * @param sourcePath the path or URL that failed to load
     * @param sourceType the type of source (e.g., "file", "stream", "url")
     * @param cause the cause
     */
    public ImageLoadException(String message, String sourcePath, String sourceType, Throwable cause) {
        super(message, cause);
        this.sourcePath = sourcePath;
        this.sourceType = sourceType;
    }

    /**
     * Returns the source path that failed to load.
     *
     * @return the source path, or null if not available
     */
    public String getSourcePath() {
        return sourcePath;
    }

    /**
     * Returns the type of source that failed to load.
     *
     * @return the source type, or null if not available
     */
    public String getSourceType() {
        return sourceType;
    }

    /**
     * Returns a string representation of this exception including source information.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ImageLoadException: ");
        sb.append(getMessage());
        if (sourcePath != null) {
            sb.append(" [source: ").append(sourceType).append("=").append(sourcePath).append("]");
        }
        return sb.toString();
    }
}
