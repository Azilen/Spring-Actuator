/*
 * Copyright 2012-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.azilen.spring.actuate.endpoint.health;

import java.util.HashMap;
import java.util.Map;

import com.azilen.spring.actuate.endpoint.mvc.HealthMvcEndpoint;
import com.azilen.spring.common.configure.condition.ConfigurationProperties;

import java.util.Collections;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * Configuration properties for the {@link HealthMvcEndpoint}.
 *
 * @author Christian Dupuis
 * @since 1.1.0
 */

@ConfigurationProperties(prefix = "endpoints.health")
public class HealthMvcEndpointProperties {

    /**
     * Mapping of health statuses to HttpStatus codes. By default, registered
     * health statuses map to sensible defaults (i.e. UP maps to 200).
     */
    private Map<String, HttpStatus> mapping = new HashMap<>();

    public Map<String, HttpStatus> getMapping() {
        return Collections.unmodifiableMap(this.mapping);
    }

    public void setMapping(Map<String, HttpStatus> mapping) {
        this.mapping = mapping;
    }

}
