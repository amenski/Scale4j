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

import com.scale4j.Scale4j;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Auto-configuration for Scale4j.
 *
 * <p>Registers a {@link Scale4jTemplate} bean pre-configured
 * with defaults from {@link Scale4jProperties}.
 */
@AutoConfiguration
@ConditionalOnClass(Scale4j.class)
@EnableConfigurationProperties(Scale4jProperties.class)
public class Scale4jAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public Scale4jTemplate scale4jTemplate(Scale4jProperties properties) {
        return new Scale4jTemplate(properties);
    }
}
