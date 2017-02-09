package com.azilen.spring.admin.client.config;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class AdminClientProperties extends AbstractNullableProperties implements ApplicationListener<ApplicationContextEvent>
{   
    /**
     * Client-management-URL to register with. Inferred at runtime, can be
     * overriden in case the reachable URL is different (e.g. Docker).
     */
    @Value("${spring.boot.admin.client.management-url}")
    private String managementUrl;

    /**
     * Client-service-URL register with. Inferred at runtime, can be overriden
     * in case the reachable URL is different (e.g. Docker).
     */    
    @Value("${spring.boot.admin.client.service-url}")        
    private String serviceUrl;

    /**
     * Client-health-URL to register with. Inferred at runtime, can be overriden
     * in case the reachable URL is different (e.g. Docker). Must be unique in
     * registry.
     */
    @Value("${spring.boot.admin.client.health-url}")   
    private String healthUrl;

    /**
     * Admin clinet Name. Defaults to ${spring.boot.admin.client.name}
     */
    @Value ("${spring.boot.admin.client.name:spring-admin-client}")
    private String adminClientName;
    
    /**
     * Name to register with. Defaults to ${spring.application.name}
     */
    @Value ("${spring.application.name:spring-admin-web-app}")
    private String name;  

    @Value ("${endpoints.health.id:health}")
    private String healthEndpointId;
    
    /**
     * Should the registered urls be built with server.address or with hostname.
     */
    @Value ("${spring.boot.admin.client.prefer-ip:false}")
    private String preferIp;
    
    private Integer serverPort;

    private Integer managementPort;
    
    @Autowired
    private ServerProperties server;
    
    @Autowired
    private ManagementServerProperties mgmtServer;
    
    @Override
    public void onApplicationEvent(ApplicationContextEvent event)
    {
        if(event instanceof ContextStartedEvent || event instanceof ContextRefreshedEvent)
        {
            serverPort = server.getPort();
            managementPort = mgmtServer.getPort() != null ? mgmtServer.getPort() : serverPort;
        }
    }

    public String getManagementUrl()
    {
        if(managementUrl != null)
        {
            return managementUrl;
        }

        if((managementPort == null || managementPort.equals(serverPort))
                && getServiceUrl() != null)
        {
            return append(serviceUrl.replace(server.getContextPath(), ""),
                    mgmtServer.getContextPath());
        }

        if(managementPort == null)
        {
            throw new IllegalStateException(
                    "serviceUrl must be set when deployed to servlet-container");
        }

        if(isPreferIp())
        {            
            InetAddress address = server.getAddress();
            if(address == null)
            {
                address = getHostAddress();
            }
            return append(append(createLocalUri(address.getHostAddress(), managementPort),
                    server.getContextPath()), mgmtServer.getContextPath());

        }
        return append(
                append(createLocalUri(getHostAddress().getCanonicalHostName(), managementPort),
                        server.getContextPath()),
                mgmtServer.getContextPath());
    }

    public void setManagementUrl(String managementUrl)
    {
        this.managementUrl = managementUrl;
    }

    public String getHealthUrl()
    {
        if(healthUrl != null)
        {
            return healthUrl;
        }
        healthUrl = append(getServiceUrl(), healthEndpointId);
        return healthUrl;
    }

    public void setHealthUrl(String healthUrl)
    {
        this.healthUrl = healthUrl;
    }

    public String getServiceUrl()
    {
        if(serviceUrl != null)
        {
        	
            return serviceUrl;
        }

        if(serverPort == null)
        {
            throw new IllegalStateException(
                    "serviceUrl must be set when deployed to servlet-container");
        }

        if(isPreferIp())
        {
            InetAddress address = server.getAddress();
            if(address == null)
            {
                address = getHostAddress();
            }
            serviceUrl = append(createLocalUri(address.getHostAddress(), serverPort),
                    server.getContextPath());
            return serviceUrl;
        }
        serviceUrl = append(createLocalUri(getHostAddress().getCanonicalHostName(), serverPort),
                server.getContextPath());
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl)
    {
        this.serviceUrl = serviceUrl;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public void setPreferIp(String preferIp)
    {
        this.preferIp = preferIp;
    }

    public boolean isPreferIp()
    {
        return Boolean.parseBoolean(preferIp);
    }

     public String getAdminClientName()
    {
        return adminClientName;
    }

    public void setAdminClientName(String adminClientName)
    {
        this.adminClientName = adminClientName;
    }
    
    private String createLocalUri(String host, int port)
    {
        String scheme = server.isSsl() ? "https" : "http";
        return scheme + "://" + host + ":" + port;
    }

    private String append(String uri, String path)
    {
        String baseUri = uri.replaceFirst("/+$", "");
        if(StringUtils.isEmpty(path))
        {
            return baseUri;
        }

        String normPath = path.replaceFirst("^/+", "").replaceFirst("/+$", "");
        return baseUri + "/" + normPath;
    }

    private InetAddress getHostAddress()
    {
        try
        {
            return InetAddress.getLocalHost();
        }
        catch(UnknownHostException ex)
        {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
    /**
    @Override
    public void afterPropertiesSet() throws Exception
    {
        for(Field field : this.getClass().getDeclaredFields())
        {
            Annotation a = field.getAnnotation(Value.class);
            if(a != null && !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())))
            {
                field.setAccessible(true);
                String value = (String) field.get(this);
                if(value.startsWith("${"))
                {
                    field.set(this, null);
                }
            }
        }
    }
    */
}
