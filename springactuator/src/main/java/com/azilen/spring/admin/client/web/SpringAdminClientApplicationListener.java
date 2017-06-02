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
package com.azilen.spring.admin.client.web;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.ContextStartedEvent;
import org.springframework.core.Ordered;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;

import com.azilen.spring.admin.client.service.ApplicationRegistrator;

/**
 * Listener responsible for starting and stopping the registration task when the
 * application is ready.
 *
 * @author Johannes Edmeier
 */
public class SpringAdminClientApplicationListener implements ApplicationListener<ApplicationContextEvent>,Ordered
{

    private static final Logger LOGGER = LoggerFactory.getLogger(SpringAdminClientApplicationListener.class);
    
    private final ApplicationRegistrator registrator;
    private final TaskScheduler taskScheduler;
    private boolean autoDeregister = false;
    private boolean autoRegister = true;
    private long registerPeriod = 10000L;
    private volatile ScheduledFuture<?> scheduledTask;    
    
    public SpringAdminClientApplicationListener(ApplicationRegistrator registrator,
            TaskScheduler taskScheduler)
    {
        this.registrator = registrator;
        this.taskScheduler = taskScheduler;
    }

    public SpringAdminClientApplicationListener(ApplicationRegistrator registrator,
            ScheduledExecutorService scheduler)
    {
        this(registrator, new ConcurrentTaskScheduler(scheduler));
    }

    public SpringAdminClientApplicationListener(ApplicationRegistrator registrator)
    {
        this(registrator, Executors.newSingleThreadScheduledExecutor());
    }    
    
    @Override
    public void onApplicationEvent(ApplicationContextEvent ce)
    {
        if(ce instanceof ContextStartedEvent || ce instanceof ContextRefreshedEvent)
        {
            registerApp();
        }
        else if(ce instanceof ContextClosedEvent)
        {
            deRegisterApp();
        }
    }
        
    private void registerApp()
    {
        if(autoRegister)
        {
            startRegisterTask();
        }
    }

    private void deRegisterApp()
    {
        stopRegisterTask();

        if(autoDeregister)
        {
            registrator.deregister();
        }
    }

    public void startRegisterTask()
    {
        if(scheduledTask != null && !scheduledTask.isDone())
        {
            return;
        }

        scheduledTask = taskScheduler.scheduleAtFixedRate(new Runnable()
        {
            @Override
            public void run()
            {
                registrator.register();
            }
        }, registerPeriod);
        LOGGER.debug("Scheduled registration task for every {}ms", registerPeriod);
    }

    public void stopRegisterTask()
    {
        if(scheduledTask != null && !scheduledTask.isDone())
        {
            scheduledTask.cancel(true);
            LOGGER.debug("Canceled registration task");
        }
    }

    public void setAutoDeregister(boolean autoDeregister)
    {
        this.autoDeregister = autoDeregister;
    }

    public void setAutoRegister(boolean autoRegister)
    {
        this.autoRegister = autoRegister;
    }

    public void setRegisterPeriod(long registerPeriod)
    {
        this.registerPeriod = registerPeriod;
    }


    @Override
    public int getOrder()
    {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
