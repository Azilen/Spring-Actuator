package com.azilen.spring.actuate.rest.controllers;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.azilen.spring.actuate.endpoint.health.HealthMvcEndpointProperties;
import com.azilen.spring.actuate.endpoint.service.HealthService;
import com.azilen.spring.common.configure.condition.EnableConfigurationProperties;

/**
 *
 * @author harshil.monani
 */
@RestController
@EnableConfigurationProperties(HealthMvcEndpointProperties.class)
public class HealthController {

    @Autowired	
    private HealthService healthService;

    @Autowired
    private HealthMvcEndpointProperties healthMvcEndpointProperties;

    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/health", produces = MediaType.APPLICATION_JSON_VALUE)
    public Object getHealth(Model model) {
    	
    	Map<String,Object> healthMap = new LinkedHashMap<String,Object>();
    	healthMap = (Map<String, Object>) healthService.getHealthDetails(healthMvcEndpointProperties);
    	//Health health = (Health) healthService.getHealthDetails(healthMvcEndpointProperties);
        ///return health;
    	return healthMap;
    }
}
