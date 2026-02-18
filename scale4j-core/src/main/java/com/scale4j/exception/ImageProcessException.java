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
 * Exception thrown when an image processing operation fails.
 * This exception provides detailed information about the processing failure,
 * including the operation that failed and the image dimensions if available.
 *
 * @author Scale4j
 * @version 1.0.0
 */
public class ImageProcessException extends Scale4jException {

    private static final long serialVersionUID = 1L;

    private final String operation;
    private final int imageWidth;
    private final int imageHeight;

    /**
     * Constructs a new ImageProcessException with no detail message.
     */
    public ImageProcessException() {
        super();
        this.operation = null;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /**
     * Constructs a new ImageProcessException with the specified detail message.
     *
     * @param message the detail message
     */
    public ImageProcessException(String message) {
        super(message);
        this.operation = null;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /**
     * Constructs a new ImageProcessException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public ImageProcessException(String message, Throwable cause) {
        super(message, cause);
        this.operation = null;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /**
     * Constructs a new ImageProcessException with operation information.
     *
     * @param message the detail message
     * @param operation the operation that failed (e.g., "resize", "rotate", "crop")
     */
    public ImageProcessException(String message, String operation) {
        super(message);
        this.operation = operation;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /**
     * Constructs a new ImageProcessException with operation information and cause.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param cause the cause
     */
    public ImageProcessException(String message, String operation, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.imageWidth = -1;
        this.imageHeight = -1;
    }

    /**
     * Constructs a new ImageProcessException with full operation and image information.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param imageWidth the width of the image being processed
     * @param imageHeight the height of the image being processed
     */
    public ImageProcessException(String message, String operation, int imageWidth, int imageHeight) {
        super(message);
        this.operation = operation;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Constructs a new ImageProcessException with full operation and image information, plus cause.
     *
     * @param message the detail message
     * @param operation the operation that failed
     * @param imageWidth the width of the image being processed
     * @param imageHeight the height of the image being processed
     * @param cause the cause
     */
    public ImageProcessException(String message, String operation, int imageWidth, int imageHeight, Throwable cause) {
        super(message, cause);
        this.operation = operation;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
    }

    /**
     * Returns the operation that failed.
     *
     * @return the operation name, or null if not available
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Returns the width of the image being processed.
     *
     * @return the image width, or -1 if not available
     */
    public int getImageWidth() {
        return imageWidth;
    }

    /**
     * Returns the height of the image being processed.
     *
     * @return the image height, or -1 if not available
     */
    public int getImageHeight() {
        return imageHeight;
    }

    /**
     * Returns a string representation of this exception including operation information.
     *
     * @return a string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ImageProcessException: ");
        sb.append(getMessage());
        if (operation != null) {
            sb.append(" [operation: ").append(operation).append("]");
        }
        if (imageWidth > 0 && imageHeight > 0) {
            sb.append(" [image: ").append(imageWidth).append("x").append(imageHeight).append("]");
        }
        return sb.toString();
    }
}
