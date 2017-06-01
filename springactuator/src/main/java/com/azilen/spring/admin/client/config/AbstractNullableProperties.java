package com.azilen.spring.admin.client.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;

/**
 *
 * @author Hardik Upadhyay
 */
public class AbstractNullableProperties implements InitializingBean
{
    private final Class<Value> targetAnnotation = Value.class;
    
    @Override
    public void afterPropertiesSet() throws Exception
    {
        for(Field field : this.getClass().getDeclaredFields())
        {
            Annotation a = field.getAnnotation(targetAnnotation);
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
