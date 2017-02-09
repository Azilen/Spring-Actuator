package com.azilen.spring.admin.client.service;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;
/**
 *
 * @author Hardik Upadhyay
 */
@Component
public class MissingPropertiesBeanPostProcessr implements BeanPostProcessor
{
    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRegistrator.class);

    private final Class<?> targetAnnotationClazz = Value.class;
    
    @Override
    public Object postProcessBeforeInitialization(Object bean, String name) throws BeansException
    {
        LOGGER.debug("BeforeInitialization : " + name);
        return bean;  // you can return any other object as well
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String name) throws BeansException
    {
        LOGGER.debug("BeforeInitialization : " + name );
        afterPropertiesSet(bean);
        return bean;  // you can return any other object as well
    }
    
    void afterPropertiesSet(Object bean)
    {
        try
        {
            for(Field field : bean.getClass().getDeclaredFields())
            {
                Annotation a = field.getAnnotation(Value.class);
                if(a != null && !(Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())))
                {
                    field.setAccessible(true);
                    String value = (String) field.get(bean);
                    LOGGER.debug("Filed Name="+field.getName()+" Fiels Value="+value);
                }
            }
        }
        catch(Exception ex)
        {
            LOGGER.error("Exception:",ex);
        }
       
    }
}
