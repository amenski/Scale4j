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
package com.scale4j.ext.springboot;

import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for Scale4j under the {@code scale4j} prefix.
 */
@ConfigurationProperties(prefix = "scale4j")
public class Scale4jProperties {

    private ResizeQuality defaultQuality = ResizeQuality.MEDIUM;
    private ResizeMode defaultMode = ResizeMode.AUTOMATIC;
    private final Async async = new Async();
    private final Cache cache = new Cache();

    public ResizeQuality getDefaultQuality() { return defaultQuality; }
    public void setDefaultQuality(ResizeQuality defaultQuality) { this.defaultQuality = defaultQuality; }

    public ResizeMode getDefaultMode() { return defaultMode; }
    public void setDefaultMode(ResizeMode defaultMode) { this.defaultMode = defaultMode; }

    public Async getAsync() { return async; }
    public Cache getCache() { return cache; }

    public static class Async {
        private int threads = Runtime.getRuntime().availableProcessors();

        public int getThreads() { return threads; }
        public void setThreads(int threads) { this.threads = threads; }
    }

    public static class Cache {
        private boolean enabled = false;

        public boolean isEnabled() { return enabled; }
        public void setEnabled(boolean enabled) { this.enabled = enabled; }
    }
}
