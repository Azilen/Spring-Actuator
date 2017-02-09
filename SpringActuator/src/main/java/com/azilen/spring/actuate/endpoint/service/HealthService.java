/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.service;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.azilen.spring.actuate.autoconfigure.MongoProperties;
import com.azilen.spring.actuate.endpoint.health.DiskSpaceHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.DiskSpaceHealthIndicatorProperties;
import com.azilen.spring.actuate.endpoint.health.Health;
import com.azilen.spring.actuate.endpoint.health.HealthMvcEndpointProperties;
import com.azilen.spring.actuate.endpoint.health.MongoHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.Status;
import com.azilen.spring.common.property.bind.RelaxedNames;
import com.mongodb.MongoClient;

/**
 *
 * @author harshil.monani
 */
@Component
public class HealthService {
	
	
	
	@Autowired
    ApplicationContext applicationContext;
	
	@Autowired
	MongoClient mongoClient;
	
	@Autowired
	MongoProperties mongoProperties;
	
//	@Autowired
//	private HealthAggregator healthAggregator;
	
	

    public Object getHealthDetails(HealthMvcEndpointProperties healthMvcEndpointProperties) {
        DiskSpaceHealthIndicator diskSpaceHealthIndicator = new DiskSpaceHealthIndicator(new DiskSpaceHealthIndicatorProperties());
        Health.Builder builder = new Health.Builder();
        Health.Builder mongoBuilder = new Health.Builder();
        Map<String,Object> healthMap = new LinkedHashMap<String,Object>();
        Map<String,Health> healths = new LinkedHashMap<String,Health>();
        String applicationStatus = "";
       
        try {
            builder = diskSpaceHealthIndicator.doHealthCheck(builder);
//            String[] beansArray = applicationContext.getBeanDefinitionNames();
            MongoHealthIndicator mongoHealthIndicator = new MongoHealthIndicator(new MongoTemplate(mongoClient, "custom_db"));
//            AbstractHealthIndicator mongoAbstractHealthIndicator = new MongoHealthIndicator(new MongoTemplate(mongoClient,"custom_db"));
//            mongoBuilder = mongoHealthIndicator.doHealthCheck(mongoBuilder);
          // mongoAbstractHealthIndicator.doHealthCheck(mongoBuilder);
            mongoHealthIndicator.doHealthCheck(mongoBuilder);
        }
        catch (Exception ex) {
        	mongoBuilder.down();
        }
        Health health = builder.build();
        Health mongoHealth = mongoBuilder.build();
        HttpStatus status = getStatus(health, healthMvcEndpointProperties);
//        HttpStatus mongoStatus = getStatus(mongoHealth, healthMvcEndpointProperties);
        
        String code = health.getStatus().getCode();
        if(("up").equalsIgnoreCase(code) && ("up").equalsIgnoreCase(mongoHealth.getStatus().getCode())){
        	applicationStatus=Status.UP.getCode();
        }
        else if(("down").equalsIgnoreCase(code) || ("down").equalsIgnoreCase(mongoHealth.getStatus().getCode())){
        	applicationStatus=Status.DOWN.getCode();
        }
        else{
        	applicationStatus=Status.UNKNOWN.getCode();
        }
        
        if (status != null) {
            return new ResponseEntity<Health>(health, status);
        }
       healthMap.put("status", applicationStatus);
       
        healthMap.put("diskSpace", health);
        if(Status.UP.getCode().equals(mongoHealth.getStatus().getCode())){
        	healthMap.put("mongo", mongoHealth);
        }
        
        healths.put("diskSpace", health);
        if(Status.UP.getCode().equals(mongoHealth.getStatus().getCode())){
        	healths.put("mongo",mongoHealth);
        }
        
        return healthMap;
    }

    private HttpStatus getStatus(Health health, HealthMvcEndpointProperties healthMvcEndpointProperties) {
        String code = health.getStatus().getCode();
        if (code != null) {
            code = code.toLowerCase().replace("_", "-");
            for (String candidate : RelaxedNames.forCamelCase(code)) {
                HttpStatus status = healthMvcEndpointProperties.getMapping().get(candidate);
                if (status != null) {
                    return status;
                }
            }
        }
        return null;
    }
}