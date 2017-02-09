/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.mvc;

import com.azilen.spring.actuate.endpoint.Endpoint;
import java.util.Collections;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 ** A strategy for the MVC layer on top of an {@link Endpoint}. Implementations
 * are allowed to use {@code @RequestMapping} and the full Spring MVC machinery,
 * but should not use {@code @Controller} or {@code @RequestMapping} at the type
 * level (since that would lead to a double mapping of paths, once by the
 * regular MVC handler mappings and once by the {@link EndpointHandlerMapping}).
 *
 * @author harshil.monani
 */
public interface MvcEndpoint {

    /**
     * A {@link ResponseEntity} returned for disabled endpoints.
     */
    ResponseEntity<Map<String, String>> DISABLED_RESPONSE = new ResponseEntity<>(
            Collections.singletonMap("message", "This endpoint is disabled"),
            HttpStatus.NOT_FOUND);

    /**
     * Return the MVC path of the endpoint.
     *
     * @return the endpoint path
     */
    String getPath();

    /**
     * Return if the endpoint exposes sensitive information.
     *
     * @return if the endpoint is sensitive
     */
    boolean isSensitive();

    /**
     * Return the type of {@link Endpoint} exposed, or {@code null} if this
     * {@link MvcEndpoint} exposes information that cannot be represented as a
     * traditional {@link Endpoint}.
     *
     * @return the endpoint type
     */
    @SuppressWarnings("rawtypes")
    Class<? extends Endpoint> getEndpointType();

}
