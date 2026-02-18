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
 * Factory for creating Scale4jLogger instances.
 * 
 * <p>This factory automatically selects the appropriate logger implementation
 * based on the availability of SLF4J on the classpath:</p>
 * <ul>
 *   <li>If SLF4J is available, uses {@link Slf4jScale4jLogger}</li>
 *   <li>Otherwise, uses {@link NoOpScale4jLogger}</li>
 * </ul>
 *
 * @author Scale4j
 * @version 5.0.0
 */
public final class Scale4jLoggerFactory {

    private static final Scale4jLoggerFactory INSTANCE = new Scale4jLoggerFactory();
    
    private static final boolean SLF4J_AVAILABLE;
    
    static {
        boolean available;
        try {
            Class.forName("org.slf4j.Logger");
            available = true;
        } catch (ClassNotFoundException e) {
            available = false;
        }
        SLF4J_AVAILABLE = available;
    }

    private Scale4jLoggerFactory() {
        // Private constructor
    }

    /**
     * Returns the singleton instance of the factory.
     *
     * @return the factory instance
     */
    public static Scale4jLoggerFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Gets a logger for the specified class.
     *
     * @param clazz the class for which to get the logger
     * @return a Scale4jLogger instance
     */
    public Scale4jLogger getLogger(Class<?> clazz) {
        if (clazz == null) {
            return getLogger("Scale4j");
        }
        return getLogger(clazz.getName());
    }

    /**
     * Gets a logger for the specified name.
     *
     * @param name the logger name
     * @return a Scale4jLogger instance
     */
    public Scale4jLogger getLogger(String name) {
        if (name == null || name.isEmpty()) {
            name = "Scale4j";
        }
        
        if (SLF4J_AVAILABLE) {
            return new Slf4jScale4jLogger(name);
        }
        return new NoOpScale4jLogger(name);
    }

    /**
     * Checks if SLF4J is available on the classpath.
     *
     * @return true if SLF4J is available
     */
    public boolean isSlf4jAvailable() {
        return SLF4J_AVAILABLE;
    }
}
