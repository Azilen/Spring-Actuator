/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint;

/**
 *
 * An endpoint that can be used to expose useful information to operations.
 * Usually exposed via Spring MVC but could also be exposed using some other
 * technique. Consider extending {@link AbstractEndpoint} if you are developing
 * you
 *
 * @author harshil.monani
 */
public interface Endpoint<T> {

    /**
     * The logical ID of the endpoint. Must only contain simple letters, numbers
     * and '_' characters (i.e. a {@literal "\w"} regex).
     *
     * @return the endpoint ID
     */
    String getId();

    /**
     * Return if the endpoint is enabled.
     *
     * @return if the endpoint is enabled
     */
    boolean isEnabled();

    /**
     * Return if the endpoint is sensitive, i.e. may return data that the
     * average user should not see. Mappings can use this as a security hint.
     *
     * @return if the endpoint is sensitive
     */
    boolean isSensitive();

    /**
     * Called to invoke the endpoint.
     *
     * @return the results of the invocation
     */
    T invoke();

}
