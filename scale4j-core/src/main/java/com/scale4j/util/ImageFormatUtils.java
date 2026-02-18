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
package com.scale4j.util;

import java.io.File;
import java.nio.file.Path;

public final class ImageFormatUtils {
    
    private ImageFormatUtils() {
        // Utility class
    }
    
    public static String getFormatFromFile(File file) {
        if (file == null) {
            return "png";
        }
        return getFormatFromPath(file.toPath());
    }
    
    public static String getFormatFromPath(Path path) {
        if (path == null) {
            return "png";
        }
        String pathStr = path.toString();
        int lastDot = pathStr.lastIndexOf('.');
        if (lastDot > 0) {
            String ext = pathStr.substring(lastDot + 1).toLowerCase();
            return normalizeFormat(ext);
        }
        return "png";
    }
    
    private static String normalizeFormat(String ext) {
        return switch (ext) {
            case "jpg", "jpeg" -> "jpeg";
            case "tif" -> "tiff";
            default -> ext;
        };
    }
    
    public static String getFormatFromExtension(String extension) {
        if (extension == null || extension.isEmpty()) {
            return "png";
        }
        return normalizeFormat(extension.toLowerCase());
    }
}