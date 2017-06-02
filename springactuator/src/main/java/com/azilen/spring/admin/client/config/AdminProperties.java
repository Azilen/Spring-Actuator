/*
 * Copyright 2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.azilen.spring.admin.client.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminProperties implements InitializingBean
{

    /**
     * The admin servers url to register at
     */
    @Value ("${spring.boot.admin.url}")
    private String url;

    /**
     * The admin rest-apis path.
     */
    @Value ("${spring.boot.admin.api-path:api/applications}")
    private String apiPath;

    /**
     * Time interval (in ms) the registration is repeated
     */
    @Value ("${spring.boot.admin.period:10000}")
    private String period;

    /**
     * Username for basic authentication on admin server
     */
    @Value ("${spring.boot.admin.username}")
    private String username;

    /**
     * Password for basic authentication on admin server
     */
    @Value ("${spring.boot.admin.password}")
    private String password;

    /**
     * Enable automatic deregistration on shutdown
     */
    @Value ("${spring.boot.admin.auto-deregistration:true}")
    private String autoDeregistration;

    /**
     * Enable automatic registration when the application is ready
     */
    @Value ("${spring.boot.admin.auto-registration:true}")
    private String autoRegistration;
    
    public void setUrl(String url)
    {
        this.url = url;
    }

    public String getUrl()
    {
        return url;
    }

    public void setApiPath(String apiPath)
    {
        this.apiPath = apiPath;
    }

    public String getApiPath()
    {
        return apiPath;
    }

    public String[] getAdminUrl()
    {
        String adminUrls[] = new String[]{url};
        for(int i = 0; i < adminUrls.length; i++)
        {
            adminUrls[i] += "/" + apiPath;
        }
        return adminUrls;
    }

    public long getPeriod()
    {
        return Long.parseLong(period);
    }

    public void setPeriod(String period)
    {
        this.period = period;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getPassword()
    {
        return password;
    }

    public boolean isAutoDeregistration()
    {
        return Boolean.parseBoolean(autoDeregistration);
    }

    public void setAutoDeregistration(String autoDeregistration)
    {
        this.autoDeregistration = autoDeregistration;
    }

    public boolean isAutoRegistration()
    {
        return Boolean.parseBoolean(autoRegistration);
    }

    public void setAutoRegistration(String autoRegistration)
    {
        this.autoRegistration = autoRegistration;
    }
    
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
}
