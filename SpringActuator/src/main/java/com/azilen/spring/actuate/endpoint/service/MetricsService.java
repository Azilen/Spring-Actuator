/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.service;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author harshil.monani
 */
@Service
public class MetricsService {

    Map<String, Object> result = new LinkedHashMap<String, Object>();

    @Autowired
    private SystemMetrics systemMetrics;

    @Autowired
    private TomcatMetrics tomcatMetrics;

    public Object getMetrics() {

        Map<String, Object> sysMetrics = systemMetrics.getMetrics();
        Map<String, Object> tomMetrics = tomcatMetrics.getTomcatMetrics();
        result.putAll(sysMetrics);
        result.putAll(tomMetrics);
        return result;
    }
}
