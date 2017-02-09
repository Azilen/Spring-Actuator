/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

/**
 *
 * @author harshil.monani
 */
@Component
public class TomcatMetrics {

    Map<String, Object> result = new LinkedHashMap<String, Object>();

    protected int maxActiveSessions = -1;

    public Map<String, Object> getTomcatMetrics() {
        result.put("httpsessions.max", maxActiveSessions);
        result.put("httpsessions.active", SessionCounter.getActiveSessions());
        return result;
    }

}
