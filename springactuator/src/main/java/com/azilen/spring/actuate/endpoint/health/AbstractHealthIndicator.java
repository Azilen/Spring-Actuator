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

import com.azilen.spring.actuate.endpoint.health.Health.Builder;

/**
 * Base {@link HealthIndicator} implementations that encapsulates creation of
 * {@link Health} instance and error handling.
 * <p>
 * This implementation is only suitable if an {@link Exception} raised from
 * {@link #doHealthCheck(org.springframework.boot.actuate.health.Health.Builder)}
 * should create a {@link Status#DOWN} health status.
 *
 * @author Christian Dupuis
 * @since 1.1.0
 */
public abstract class AbstractHealthIndicator implements HealthIndicator {

    @Override
    public final Health health() {
        Health.Builder builder = new Health.Builder();
        try {
            doHealthCheck(builder);
        }
        catch (Exception ex) {
            builder.down(ex);
        }
        return builder.build();
    }

    /**
     * Actual health check logic.
     *
     * @param builder the {@link Builder} to report health status and details
     * @throws Exception any {@link Exception} that should create a
     * {@link Status#DOWN} system status.
     */
    public abstract void doHealthCheck(Health.Builder builder) throws Exception;
}
