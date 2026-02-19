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
package com.scale4j.ext.avif;

import javax.imageio.ImageIO;
import java.util.Set;
import java.util.logging.Logger;

/**
 * AVIF format extension for Scale4j.
 * 
 * <p>This class provides AVIF format support. AVIF support depends on the Java version:</p>
 * <ul>
 *   <li><strong>Java 21+:</strong> Native AVIF support via built-in ImageIO plugin</li>
 *   <li><strong>Java 11-20:</strong> Requires external plugin (e.g., jniojar/avif-native)</li>
 * </ul>
 * 
 * <p>When this module is on the classpath with appropriate support, AVIF images can be 
 * read and written automatically through Scale4j's ImageLoader and ImageSaver.</p>
 * 
 * <p>Usage:</p>
 * <pre>
 * // AVIF support is auto-initialized when this module is on classpath
 * BufferedImage image = Scale4j.getImageLoader().load("input.avif");
 * Scale4j.getImageSaver().save(image, "output.avif");
 * </pre>
 * 
 * @author Scale4j
 * @since 1.0.0
 */
public final class AVIFExtension {

    private static final Logger LOGGER = Logger.getLogger(AVIFExtension.class.getName());
    
    private static final String AVIF_FORMAT = "avif";
    private static volatile boolean initialized = false;
    private static volatile String supportLevel = "none";

    /**
     * Initializes the AVIF extension by checking for AVIF support in ImageIO.
     * This method is automatically called when the module is loaded.
     */
    public static synchronized void initialize() {
        if (initialized) {
            return;
        }
        
        String javaVersion = System.getProperty("java.version", "");
        LOGGER.info("Initializing AVIF extension, Java version: " + javaVersion);
        
        // Check for native Java 21+ AVIF support
        try {
            // Java 21+ has built-in AVIF support
            if (ImageIO.getImageReadersBySuffix(AVIF_FORMAT).hasNext()) {
                LOGGER.info("AVIF format support enabled (Java 21+ native)");
                supportLevel = "native";
                initialized = true;
                return;
            }
        } catch (Exception e) {
            LOGGER.fine("Java 21+ AVIF check: " + e.getMessage());
        }
        
        // Try to load external AVIF plugin (for Java 11-20)
        // NOTE: This hardcoded class name is for a specific external plugin.
        // Other AVIF plugins (e.g., jniojar/avif-native) use SPI registration
        // and will be auto-detected below.
        try {
            Class.forName("io.github.nicklassydney.avif.AVIFImageReaderSpi", 
                    true, 
                    AVIFExtension.class.getClassLoader());
            
            if (ImageIO.getImageReadersBySuffix(AVIF_FORMAT).hasNext()) {
                LOGGER.info("AVIF format support enabled via external plugin");
                supportLevel = "plugin";
                initialized = true;
                return;
            }
        } catch (ClassNotFoundException e) {
            LOGGER.fine("External AVIF plugin not found: " + e.getMessage());
        } catch (Exception e) {
            LOGGER.warning("Failed to load external AVIF plugin: " + e.getMessage());
        }
        
        // Note: Additional external plugins (e.g., jniojar/avif-native) can be auto-detected
        // if they register themselves with ImageIO via SPI
        
        if (!initialized) {
            LOGGER.warning("AVIF format not available. Java 21+ required for native support, " +
                    "or add an AVIF ImageIO plugin for Java 11-20");
        }
    }

    /**
     * Checks if AVIF format is supported for reading.
     *
     * @return true if AVIF reading is supported
     */
    public static boolean isReadSupported() {
        if (!initialized) {
            initialize();
        }
        return ImageIO.getImageReadersBySuffix(AVIF_FORMAT).hasNext();
    }

    /**
     * Checks if AVIF format is supported for writing.
     *
     * @return true if AVIF writing is supported
     */
    public static boolean isWriteSupported() {
        if (!initialized) {
            initialize();
        }
        return ImageIO.getImageWritersBySuffix(AVIF_FORMAT).hasNext();
    }

    /**
     * Checks if AVIF format is fully supported (both read and write).
     *
     * @return true if both reading and writing AVIF are supported
     */
    public static boolean isSupported() {
        return isReadSupported() && isWriteSupported();
    }

    /**
     * Returns the level of AVIF support available.
     *
     * @return "native" for Java 21+, "plugin" for external plugin, "none" if not available
     */
    public static String getSupportLevel() {
        if (!initialized) {
            initialize();
        }
        return supportLevel;
    }

    /**
     * Returns the set of supported AVIF-related formats.
     *
     * @return set containing "avif" and possibly "heic" if supported
     */
    public static Set<String> getSupportedFormats() {
        Set<String> formats = new java.util.HashSet<>();
        formats.add(AVIF_FORMAT);
        
        // AVIF is often related to HEIF/HEIC
        if (ImageIO.getImageReadersBySuffix("heic").hasNext()) {
            formats.add("heic");
        }
        if (ImageIO.getImageReadersBySuffix("heif").hasNext()) {
            formats.add("heif");
        }
        
        return formats;
    }

    /**
     * Gets the version of the AVIF extension.
     *
     * @return the extension version
     */
    public static String getVersion() {
        return "1.0.0";
    }

    /**
     * Gets the minimum Java version required for AVIF support.
     *
     * @return minimum Java version string
     */
    public static String getMinimumJavaVersion() {
        return "11";
    }

    // Static initialization block to auto-register when loaded
    static {
        initialize();
    }
}
