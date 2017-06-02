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
public class ManagementServerProperties extends AbstractNullableProperties
{
    @Value("${management.port}")
    private String port;
    @Value("${management.address}")
    private String address;
    @Value("${management.contextPath}")
    private String contextPath;

    public Integer getPort()
    {
        return ((this.port == null) ||("".equals(port))) ? null : Integer.parseInt(port);
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
}
