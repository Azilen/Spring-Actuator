package com.azilen.spring.actuate.endpoint;

import org.springframework.core.env.Environment;

/**
 *
 * @author harshil.monani
 */
public class EndpointProperties {

    private static final String ENDPOINTS_ENABLED_PROPERTY = "endpoints.enabled";

    private static final String ENDPOINTS_SENSITIVE_PROPERTY = "endpoints.sensitive";

    /**
     * Enable endpoints.
     */
    private Boolean enabled = true;

    /**
     * Default endpoint sensitive setting.
     */
    private Boolean sensitive;

    public Boolean getEnabled() {
        return this.enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getSensitive() {
        return this.sensitive;
    }

    public void setSensitive(Boolean sensitive) {
        this.sensitive = sensitive;
    }

    /**
     * Determine if an endpoint is enabled based on its specific property and
     * taking into account the global default.
     *
     * @param environment the Spring environment or {@code null}.
     * @param enabled the endpoint property or {@code null}
     * @return if the endpoint is enabled
     */
    public static boolean isEnabled(Environment environment, Boolean enabled) {
        if (enabled != null) {
            return enabled;
        }
        if (environment != null
                && environment.containsProperty(ENDPOINTS_ENABLED_PROPERTY)) {
            return environment.getProperty(ENDPOINTS_ENABLED_PROPERTY, Boolean.class);
        }
        return true;
    }

    /**
     * Determine if an endpoint is sensitive based on its specific property and
     * taking into account the global default.
     *
     * @param environment the Spring environment or {@code null}.
     * @param sensitive the endpoint property or {@code null}
     * @param sensitiveDefault the default setting to use if no environment
     * property is defined
     * @return if the endpoint is sensitive
     */
    public static boolean isSensitive(Environment environment, Boolean sensitive,
            boolean sensitiveDefault) {
        if (sensitive != null) {
            return sensitive;
        }
        if (environment != null
                && environment.containsProperty(ENDPOINTS_SENSITIVE_PROPERTY)) {
            return environment.getProperty(ENDPOINTS_SENSITIVE_PROPERTY, Boolean.class);
        }
        return sensitiveDefault;
    }

}