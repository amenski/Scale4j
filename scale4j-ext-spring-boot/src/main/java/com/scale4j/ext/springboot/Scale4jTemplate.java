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

import com.scale4j.AsyncScale4j;
import com.scale4j.BatchProcessorBuilder;
import com.scale4j.Scale4j;
import com.scale4j.Scale4jBuilder;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;

/**
 * Spring-friendly wrapper around {@link Scale4j} that bakes in
 * the configured default resize mode and quality.
 *
 * <p>Inject this bean wherever you would call {@code Scale4j.load()}:
 * <pre>{@code
 * &#64;Autowired
 * private Scale4jTemplate scale4j;
 *
 * BufferedImage result = scale4j.load(image)
 *     .resize(300, 200)
 *     .build();
 * }</pre>
 *
 * <p>For cases where you need to override the defaults, call
 * {@code .mode(...)} or {@code .quality(...)} on the returned builder
 * as usual — they always win over the template defaults.
 */
public class Scale4jTemplate {

    private final Scale4jProperties properties;

    public Scale4jTemplate(Scale4jProperties properties) {
        this.properties = properties;
    }

    public Scale4jBuilder load(BufferedImage image) {
        return Scale4j.load(image).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder load(File file) {
        return Scale4j.load(file).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder load(Path path) {
        return Scale4j.load(path).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder load(URL url) {
        return Scale4j.load(url).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder load(InputStream stream) {
        return Scale4j.load(stream).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder loadWithMetadata(File file) {
        return Scale4j.loadWithMetadata(file).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder loadWithMetadata(Path path) {
        return Scale4j.loadWithMetadata(path).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder loadWithMetadata(URL url) {
        return Scale4j.loadWithMetadata(url).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public Scale4jBuilder loadWithMetadata(InputStream stream) {
        return Scale4j.loadWithMetadata(stream).mode(properties.getDefaultMode()).quality(properties.getDefaultQuality());
    }

    public BatchProcessorBuilder batch() {
        return Scale4j.batch();
    }

    public BatchProcessorBuilder batch(List<BufferedImage> images) {
        return Scale4j.batch(images);
    }

    public AsyncScale4j async() {
        return Scale4j.async();
    }

    public Scale4jProperties getProperties() {
        return properties;
    }
}
