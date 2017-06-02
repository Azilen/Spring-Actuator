/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.azilen.spring.actuate.endpoint.mvc;

import com.azilen.spring.actuate.endpoint.Endpoint;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;

/**
 *
 * @author harshil.monani
 * @param <E>
 */
public abstract class AbstractEndpointMvcAdapter<E extends Endpoint<?>>
        implements MvcEndpoint {

    private final E delegate;

    /**
     * Endpoint URL path.
     */
    private String path;

    /**
     * Create a new {@link EndpointMvcAdapter}.
     *
     * @param delegate the underlying {@link Endpoint} to adapt.
     */
    public AbstractEndpointMvcAdapter(E delegate) {
        Assert.notNull(delegate, "Delegate must not be null");
        this.delegate = delegate;
    }

    protected Object invoke() {
        if (!this.delegate.isEnabled()) {
            // Shouldn't happen - shouldn't be registered when delegate's disabled
            return getDisabledResponse();
        }
        return this.delegate.invoke();
    }

    public E getDelegate() {
        return this.delegate;
    }

    @Override
    public String getPath() {
        return (this.path != null ? this.path : "/" + this.delegate.getId());
    }

    public void setPath(String path) {
        while (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        this.path = path;
    }

    @Override
    public boolean isSensitive() {
        return this.delegate.isSensitive();
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Class<? extends Endpoint> getEndpointType() {
        return this.delegate.getClass();
    }

    /**
     * Returns the response that should be returned when the endpoint is
     * disabled.
     *
     * @return The response to be returned when the endpoint is disabled
     * @since 1.2.4
     * @see Endpoint#isEnabled()
     */
    protected ResponseEntity<?> getDisabledResponse() {
        return MvcEndpoint.DISABLED_RESPONSE;
    }

}
