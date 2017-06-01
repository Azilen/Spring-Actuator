package com.azilen.spring.admin.client.config;

import java.net.InetAddress;
import java.net.UnknownHostException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 *
 * @author Hardik Upadhyay
 */
@Component
public class ServerProperties extends AbstractNullableProperties
{
    @Value("${server.port}")
    private String port;
    
    @Value("${server.address}")
    private String address;
    
    @Value("${server.contextpath}")
    private String contextPath;
    
    @Value("${server.diplayName:application}")    
    private String displayName;
    
    @Value("${server.servletPath:/}")
    private String servletPath; 
    
    @Value("${server.ssl:false}")
    private String ssl;
   
    public Integer getPort()
    {
        return Integer.parseInt(port);
    }

    public void setPort(String port)
    {
        this.port = port;
    }

    public InetAddress getAddress()
    {
        try
        {
            return InetAddress.getByName(address);
        }
        catch(UnknownHostException ex)
        {
            return null;
        }
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public String getContextPath()
    {
        return contextPath;
    }

    public void setContextPath(String contextPath)
    {
        this.contextPath = contextPath;
    }

    public String getDisplayName()
    {
        return displayName;
    }

    public void setDisplayName(String displayName)
    {
        this.displayName = displayName;
    }

    public String getServletPath()
    {
        return servletPath;
    }

    public void setServletPath(String servletPath)
    {
        this.servletPath = servletPath;
    }

    public boolean isSsl()
    {
        return Boolean.parseBoolean(ssl);
    }

    public void setSsl(String ssl)
    {
        this.ssl = ssl;
    }       
    /*
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
