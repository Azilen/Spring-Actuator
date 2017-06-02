package com.azilen.spring.actuate.endpoint;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 *
 * @author harshil.monani
 * @param <T>
 */
public abstract class AbstractEndpoint<T> implements Endpoint<T>, EnvironmentAware {

    private Environment environment;

    /**
     * Endpoint identifier. With HTTP monitoring the identifier of the endpoint
     * is mapped to a URL (e.g. 'foo' is mapped to '/foo').
     */
    @NotNull
    @Pattern(regexp = "\\w+", message = "ID must only contains letters, numbers and '_'")
    private String id;

    private final boolean sensitiveDefault;

    /**
     * Mark if the endpoint exposes sensitive information.
     */
    private Boolean sensitive;

    /**
     * Enable the endpoint.
     */
    private Boolean enabled;

    /**
     * Create a new sensitive endpoint instance. The endpoint will enabled flag
     * will be based on the spring {@link Environment} unless explicitly set.
     *
     * @param id the endpoint ID
     */
    public AbstractEndpoint(String id) {
        this(id, true);
    }

    /**
     * Create a new endpoint instance. The endpoint will enabled flag will be
     * based on the spring {@link Environment} unless explicitly set.
     *
     * @param id the endpoint ID
     * @param sensitive if the endpoint is sensitive by default
     */
    public AbstractEndpoint(String id, boolean sensitive) {
        this.id = id;
        this.sensitiveDefault = sensitive;
    }

    /**
     * Create a new endpoint instance.
     *
     * @param id the endpoint ID
     * @param sensitive if the endpoint is sensitive
     * @param enabled if the endpoint is enabled or not.
     */
    public AbstractEndpoint(String id, boolean sensitive, boolean enabled) {
        this.id = id;
        this.sensitiveDefault = sensitive;
        this.enabled = enabled;
    }

    protected final Environment getEnvironment() {
        return this.environment;
    }

    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean isEnabled() {
        return EndpointProperties.isEnabled(this.environment, this.enabled);
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isSensitive() {
        return EndpointProperties.isSensitive(this.environment, this.sensitive,
                this.sensitiveDefault);
    }

    public void setSensitive(Boolean sensitive) {
        this.sensitive = sensitive;
    }

}