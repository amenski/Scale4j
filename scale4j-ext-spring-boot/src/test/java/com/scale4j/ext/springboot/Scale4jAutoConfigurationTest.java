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

import static org.assertj.core.api.Assertions.assertThat;

import com.scale4j.Scale4jBuilder;
import com.scale4j.types.ResizeMode;
import com.scale4j.types.ResizeQuality;
import java.awt.image.BufferedImage;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

class Scale4jAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(Scale4jAutoConfiguration.class));

    @Test
    void shouldCreateTemplateBean() {
        contextRunner.run(ctx -> {
            assertThat(ctx).hasSingleBean(Scale4jTemplate.class);
            assertThat(ctx).hasSingleBean(Scale4jProperties.class);
        });
    }

    @Test
    void shouldBindDefaultProperties() {
        contextRunner.run(ctx -> {
            Scale4jProperties props = ctx.getBean(Scale4jProperties.class);
            assertThat(props.getDefaultQuality()).isEqualTo(ResizeQuality.MEDIUM);
            assertThat(props.getDefaultMode()).isEqualTo(ResizeMode.AUTOMATIC);
            assertThat(props.getAsync().getThreads()).isPositive();
            assertThat(props.getCache().isEnabled()).isFalse();
        });
    }

    @Test
    void shouldBindCustomProperties() {
        contextRunner
                .withPropertyValues(
                        "scale4j.default-quality=HIGH",
                        "scale4j.default-mode=FILL",
                        "scale4j.async.threads=8",
                        "scale4j.cache.enabled=true")
                .run(ctx -> {
                    Scale4jProperties props = ctx.getBean(Scale4jProperties.class);
                    assertThat(props.getDefaultQuality()).isEqualTo(ResizeQuality.HIGH);
                    assertThat(props.getDefaultMode()).isEqualTo(ResizeMode.FILL);
                    assertThat(props.getAsync().getThreads()).isEqualTo(8);
                    assertThat(props.getCache().isEnabled()).isTrue();
                });
    }

    @Test
    void shouldBakeDefaultsIntoBuilder() {
        contextRunner
                .withPropertyValues(
                        "scale4j.default-quality=ULTRA",
                        "scale4j.default-mode=EXACT")
                .run(ctx -> {
                    Scale4jTemplate template = ctx.getBean(Scale4jTemplate.class);
                    BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);

                    Scale4jBuilder builder = template.load(image);
                    assertThat(builder).isNotNull();
                });
    }
}
