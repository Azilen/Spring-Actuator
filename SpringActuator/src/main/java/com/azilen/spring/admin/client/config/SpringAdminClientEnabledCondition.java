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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * This condition checks if the client should be enabled. Two properties are checked:
 * spring.boot.admin.client.enabled and spring.boot.admin.url. The following table shows under which
 * conditions the client is active.
 *
 * <pre>
 *           | enabled: false | enabled: true (default) |
 * --------- | -------------- | ----------------------- |
 * url empty | inactive       | inactive                |
 * (default) |                |                         |
 * --------- | -------------- | ----------------------- |
 * url set   | inactive       | active                  |
 * </pre>
 */
@Component
@Lazy
public class SpringAdminClientEnabledCondition implements Condition 
{
    private static final Logger log = LoggerFactory.getLogger(SpringAdminClientEnabledCondition.class);
    
    @Override
    public boolean matches(ConditionContext cc, AnnotatedTypeMetadata atm)
    {        
        log.debug("Evaluating condition...");
        String url = cc.getEnvironment().getProperty("spring.boot.admin.url","");
        boolean enabled = Boolean.getBoolean(cc.getEnvironment().getProperty("spring.boot.admin.client.enabled","false"));
        log.debug("Spring admin URL="+url);
        log.debug("Spring admin client enabled="+enabled);
        return (StringUtils.isEmpty(url) && enabled);
    }
}
