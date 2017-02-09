/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.health;

import com.azilen.spring.actuate.endpoint.AbstractEndpoint;
import java.util.Map;
import org.springframework.util.Assert;

/**
 *
 *
 * @author harshil.monani
 */
public class HealthEndpoint extends AbstractEndpoint<Health> {

    private final HealthIndicator healthIndicator;

    /**
     * Time to live for cached result, in milliseconds.
     */
    private long timeToLive = 1000;

    /**
     * Create a new {@link HealthEndpoint} instance.
     *
     * @param healthAggregator the health aggregator
     * @param healthIndicators the health indicators
     */
    public HealthEndpoint(HealthAggregator healthAggregator, Map<String, HealthIndicator> healthIndicators) {
        super("health", false);
        Assert.notNull(healthAggregator, "HealthAggregator must not be null");
        Assert.notNull(healthIndicators, "HealthIndicators must not be null");
        CompositeHealthIndicator healthIndicator = new CompositeHealthIndicator(healthAggregator);
        for (Map.Entry<String, HealthIndicator> entry : healthIndicators.entrySet()) {
            healthIndicator.addHealthIndicator(getKey(entry.getKey()), entry.getValue());
        }
        this.healthIndicator = healthIndicator;
    }

    /**
     * Time to live for cached result. This is particularly useful to cache the
     * result of this endpoint to prevent a DOS attack if it is accessed
     * anonymously.
     *
     * @return time to live in milliseconds (default 1000)
     */
    public long getTimeToLive() {
        return this.timeToLive;
    }

    public void setTimeToLive(long ttl) {
        this.timeToLive = ttl;
    }

    /**
     * Invoke all {@link HealthIndicator} delegates and collect their health
     * information.
     */
    @Override
    public Health invoke() {
        return this.healthIndicator.health();
    }

    /**
     * Turns the bean name into a key that can be used in the map of health
     * information.
     *
     * @param name the bean name
     * @return the key
     */
    private String getKey(String name) {
        int index = name.toLowerCase().indexOf("healthindicator");
        if (index > 0) {
            return name.substring(0, index);
        }
        return name;
    }
}
