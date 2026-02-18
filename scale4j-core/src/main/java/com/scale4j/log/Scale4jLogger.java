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
 * Logging interface for Scale4j.
 * 
 * <p>This interface provides a simple logging abstraction that can be implemented
 * by various logging backends. By default, Scale4j uses a no-op implementation.
 * When SLF4J is on the classpath, the SLF4J-based implementation is used automatically.</p>
 *
 * <p>Log levels follow the standard logging conventions:</p>
 * <ul>
 *   <li>TRACE - Detailed information for debugging</li>
 *   <li>DEBUG - General information for debugging</li>
 *   <li>INFO - Informational messages</li>
 *   <li>WARN - Warning messages</li>
 *   <li>ERROR - Error messages</li>
 * </ul>
 *
 * @author Scale4j
 * @version 5.0.0
 */
public interface Scale4jLogger {

    /**
     * Returns the name of this logger.
     *
     * @return the logger name
     */
    String getName();

    /**
     * Checks if TRACE level logging is enabled.
     *
     * @return true if TRACE level is enabled
     */
    boolean isTraceEnabled();

    /**
     * Logs a message at TRACE level.
     *
     * @param message the message to log
     */
    void trace(String message);

    /**
     * Logs a message at TRACE level with arguments.
     *
     * @param message the message pattern
     * @param args the arguments for the message pattern
     */
    void trace(String message, Object... args);

    /**
     * Logs a message at TRACE level with a throwable.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void trace(String message, Throwable throwable);

    /**
     * Checks if DEBUG level logging is enabled.
     *
     * @return true if DEBUG level is enabled
     */
    boolean isDebugEnabled();

    /**
     * Logs a message at DEBUG level.
     *
     * @param message the message to log
     */
    void debug(String message);

    /**
     * Logs a message at DEBUG level with arguments.
     *
     * @param message the message pattern
     * @param args the arguments for the message pattern
     */
    void debug(String message, Object... args);

    /**
     * Logs a message at DEBUG level with a throwable.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void debug(String message, Throwable throwable);

    /**
     * Checks if INFO level logging is enabled.
     *
     * @return true if INFO level is enabled
     */
    boolean isInfoEnabled();

    /**
     * Logs a message at INFO level.
     *
     * @param message the message to log
     */
    void info(String message);

    /**
     * Logs a message at INFO level with arguments.
     *
     * @param message the message pattern
     * @param args the arguments for the message pattern
     */
    void info(String message, Object... args);

    /**
     * Logs a message at INFO level with a throwable.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void info(String message, Throwable throwable);

    /**
     * Checks if WARN level logging is enabled.
     *
     * @return true if WARN level is enabled
     */
    boolean isWarnEnabled();

    /**
     * Logs a message at WARN level.
     *
     * @param message the message to log
     */
    void warn(String message);

    /**
     * Logs a message at WARN level with arguments.
     *
     * @param message the message pattern
     * @param args the arguments for the message pattern
     */
    void warn(String message, Object... args);

    /**
     * Logs a message at WARN level with a throwable.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void warn(String message, Throwable throwable);

    /**
     * Checks if ERROR level logging is enabled.
     *
     * @return true if ERROR level is enabled
     */
    boolean isErrorEnabled();

    /**
     * Logs a message at ERROR level.
     *
     * @param message the message to log
     */
    void error(String message);

    /**
     * Logs a message at ERROR level with arguments.
     *
     * @param message the message pattern
     * @param args the arguments for the message pattern
     */
    void error(String message, Object... args);

    /**
     * Logs a message at ERROR level with a throwable.
     *
     * @param message the message to log
     * @param throwable the throwable to log
     */
    void error(String message, Throwable throwable);
}
