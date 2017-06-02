/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.rest.controllers;

import com.azilen.spring.actuate.endpoint.service.MetricsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 *
 * @author harshil.monani
 */
@RestController
public class MetricsController {

    @Autowired
    private MetricsService metricsService;

    @RequestMapping(value = "/metrics", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getHealth(Model model) {

        Object object = metricsService.getMetrics();
        return object;
    }
}
