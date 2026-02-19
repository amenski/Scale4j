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
package com.scale4j.ext.webp;

import javax.imageio.ImageIO;
import java.util.Set;
import java.util.logging.Logger;

/**
 * WebP format extension for Scale4j.
 * 
 * <p>This class provides WebP format support by initializing the TwelveMonkeys ImageIO
 * WebP plugin. When this module is on the classpath, WebP images can be read and written
 * automatically through Scale4j's ImageLoader and ImageSaver.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * // WebP support is auto-initialized when this module is on classpath
 * BufferedImage image = Scale4j.getImageLoader().load("input.webp");
 * Scale4j.getImageSaver().save(image, "output.webp");
 * </pre>
 * 
 * @author Scale4j
 * @since 1.0.0
 */
public final class WebPExtension {

    private static final Logger LOGGER = Logger.getLogger(WebPExtension.class.getName());
    
    private static final String WEBP_FORMAT = "webp";
    private static boolean initialized = false;

    /**
     * Initializes the WebP extension by registering the WebP format with ImageIO.
     * This method is automatically called when the module is loaded.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        try {
            // Trigger class loading of the WebP ImageIO plugin
            Class.forName("com.twelvemonkeys.imageio.plugins.webp.WebPImageReaderSpi", 
                    true, 
                    WebPExtension.class.getClassLoader());
            
            // Check if WebP is now supported
            if (ImageIO.getImageReadersBySuffix(WEBP_FORMAT).hasNext()) {
                LOGGER.info("WebP format support enabled via TwelveMonkeys ImageIO");
                initialized = true;
            } else {
                LOGGER.warning("WebP ImageIO plugin found but format not registered");
            }
        } catch (ClassNotFoundException e) {
            LOGGER.warning("WebP ImageIO plugin not found on classpath");
        } catch (Exception e) {
            LOGGER.warning("Failed to initialize WebP extension: " + e.getMessage());
        }
    }

    /**
     * Checks if WebP format is supported for reading.
     *
     * @return true if WebP reading is supported
     */
    public static boolean isReadSupported() {
        return ImageIO.getImageReadersBySuffix(WEBP_FORMAT).hasNext();
    }

    /**
     * Checks if WebP format is supported for writing.
     *
     * @return true if WebP writing is supported
     */
    public static boolean isWriteSupported() {
        return ImageIO.getImageWritersBySuffix(WEBP_FORMAT).hasNext();
    }

    /**
     * Checks if WebP format is fully supported (both read and write).
     *
     * @return true if both reading and writing WebP are supported
     */
    public static boolean isSupported() {
        return isReadSupported() && isWriteSupported();
    }

    /**
     * Returns the set of supported WebP-related formats.
     *
     * @return set containing "webp"
     */
    public static Set<String> getSupportedFormats() {
        return Set.of(WEBP_FORMAT);
    }

    /**
     * Gets the version of the WebP extension.
     *
     * @return the extension version
     */
    public static String getVersion() {
        return "1.0.0";
    }

    /**
     * Gets the minimum Java version required for WebP support.
     * WebP is supported via the TwelveMonkeys plugin on Java 8+.
     *
     * @return the minimum Java version
     */
    public static String getMinimumJavaVersion() {
        return "8";
    }

    // Static initialization block to auto-register when loaded
    static {
        initialize();
    }
}
