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
 * Exception thrown when an image fails to save.
 * This exception provides detailed information about the save failure,
 * including the destination path and the format being used.
 *
 * @author Scale4j
 * @version 1.0.0
 */
public class ImageSaveException extends Scale4jException {

    private static final long serialVersionUID = 1L;

    private final String destinationPath;
    private final String format;

    /**
     * Constructs a new ImageSaveException with no detail message.
     */
    public ImageSaveException() {
        super();
        this.destinationPath = null;
        this.format = null;
    }

    /**
     * Constructs a new ImageSaveException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageSaveException(String message) {
        super(message);
        this.destinationPath = null;
        this.format = null;
    }

    /**
     * Constructs a new ImageSaveException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ImageSaveException(String message, Throwable cause) {
        super(message, cause);
        this.destinationPath = null;
        this.format = null;
    }

    /**
     * Constructs a new ImageSaveException with destination information.
     *
     * @param message the detail message
     * @param destinationPath the path where the image was being saved
     * @param format the image format being used
     */
    public ImageSaveException(String message, String destinationPath, String format) {
        super(message);
        this.destinationPath = destinationPath;
        this.format = format;
    }

    /**
     * Constructs a new ImageSaveException with destination information and cause.
     *
     * @param message the detail message
     * @param destinationPath the path where the image was being saved
     * @param format the image format being used
     * @param cause the cause
     */
    public ImageSaveException(String message, String destinationPath, String format, Throwable cause) {
        super(message, cause);
        this.destinationPath = destinationPath;
        this.format = format;
    }

    /**
     * Returns the destination path where the image was being saved.
     *
     * @return the destination path, or null if not available
     */
    public String getDestinationPath() {
        return destinationPath;
    }

    /**
     * Returns the image format being used.
     *
     * @return the format, or null if not available
     */
    public String getFormat() {
        return format;
    }

    /**
     * Returns a string representation of this exception including destination information.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ImageSaveException: ");
        sb.append(getMessage());
        if (destinationPath != null) {
            sb.append(" [destination: ").append(destinationPath).append("]");
        }
        if (format != null) {
            sb.append(" [format: ").append(format).append("]");
        }
        return sb.toString();
    }
}
