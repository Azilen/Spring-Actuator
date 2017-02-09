package com.azilen.spring.admin.client.config;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.azilen.spring.actuate.autoconfigure.CompositeHealthIndicatorConfiguration;
import com.azilen.spring.actuate.autoconfigure.HealthIndicatorProperties;
import com.azilen.spring.actuate.endpoint.health.HealthAggregator;
import com.azilen.spring.actuate.endpoint.health.HealthIndicator;
import com.azilen.spring.actuate.endpoint.health.MongoHealthIndicator;
import com.azilen.spring.actuate.endpoint.health.OrderedHealthAggregator;
import com.azilen.spring.admin.client.service.ApplicationRegistrator;
import com.azilen.spring.admin.client.web.BasicAuthHttpRequestInterceptor;
import com.azilen.spring.admin.client.web.SpringAdminClientApplicationListener;
import com.azilen.spring.common.configure.condition.ConditionalOnBean;
import com.azilen.spring.common.configure.condition.ConditionalOnEnabledHealthIndicator;
import com.azilen.spring.common.configure.condition.ConditionalOnMissingBean;
import com.azilen.spring.common.configure.condition.EnableConfigurationProperties;
/**
 *
 * @author Hardik Upadhyay
 */
@Configuration
@ComponentScan (basePackages ={"com.azilen"})
@EnableConfigurationProperties({ HealthIndicatorProperties.class })
public class SpringAdminClientApplicationConfig
{
    @Autowired
    private AdminClientProperties client;

    @Autowired
    private AdminProperties admin;
    
    private final HealthIndicatorProperties properties;
    
    public SpringAdminClientApplicationConfig(HealthIndicatorProperties properties) {
    	this.properties=properties;
	}

    /**
     * Task that registers the application at the spring-boot-admin application.
     */
    @Bean    
    public ApplicationRegistrator registrator()
    {
        return new ApplicationRegistrator(createRestTemplate(admin), admin, client);
    }

    /**
     * 
     * @param admin
     * @return 
     */
    protected RestTemplate createRestTemplate(AdminProperties admin)
    {
        RestTemplate template = new RestTemplate();
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        if(admin.getUsername() != null)
        {
            template.setInterceptors(Arrays.<ClientHttpRequestInterceptor>asList(
                    new BasicAuthHttpRequestInterceptor(admin.getUsername(), admin.getPassword())));
        }

        return template;
    }
    
    
    
    
    /*public @Bean
 	MongoDbFactory mongoDbFactory() throws Exception {
 		return new SimpleMongoDbFactory(new MongoClient(), "custom_db");
 	}

 	public @Bean
 	MongoTemplate mongoTemplate() throws Exception {

 		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());

 		return mongoTemplate;

 	}*/
    
    
    @Bean
	@ConditionalOnMissingBean(HealthAggregator.class)
	public OrderedHealthAggregator healthAggregator() {
		OrderedHealthAggregator healthAggregator = new OrderedHealthAggregator();
		if (this.properties.getOrder() != null) {
			healthAggregator.setStatusOrder(this.properties.getOrder());
		}
		return healthAggregator;
	}
    
    
    @Configuration
	@ConditionalOnBean(MongoTemplate.class)
	@ConditionalOnEnabledHealthIndicator("mongo")
	public static class MongoHealthIndicatorConfiguration extends
			CompositeHealthIndicatorConfiguration<MongoHealthIndicator, MongoTemplate> {

		private final Map<String, MongoTemplate> mongoTemplates;

		public MongoHealthIndicatorConfiguration(
				Map<String, MongoTemplate> mongoTemplates) {
			this.mongoTemplates = mongoTemplates;
		}

		@Bean
		@ConditionalOnMissingBean(name = "mongoHealthIndicator")
		public HealthIndicator mongoHealthIndicator() {
			return createHealthIndicator(this.mongoTemplates);
		}

	}
    
    
    

    /**
     * ApplicationListener triggering registration after being ready/shutdown
     * @return 
     */
    @Bean    
    public SpringAdminClientApplicationListener registrationListener()
    {
        SpringAdminClientApplicationListener listener = new SpringAdminClientApplicationListener(registrator());
        listener.setAutoRegister(admin.isAutoRegistration());
        listener.setAutoDeregister(admin.isAutoDeregistration());
        listener.setRegisterPeriod(admin.getPeriod());
        return listener;
    }
    
    /**
     * It is required to support @PropertySource and @Value
     * @return 
    */ 
//    @Bean
//    public static PropertySourcesPlaceholderConfigurer propertyConfigInDev()
//    {
//        PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
//        placeholderConfigurer.setIgnoreResourceNotFound(false);
//        placeholderConfigurer.setIgnoreUnresolvablePlaceholders(true);
//        return placeholderConfigurer;
//    }
    
//    @Bean
//    public PropertyPlaceholderConfigurer getPropertyPlaceHilder()
//    {
//        PropertyPlaceholderConfigurer p = new PropertyPlaceholderConfigurer();
//        Resource r2 = new ClassPathResource("spring-admin.properties");
//        p.setLocations(new Resource[]{r2});
//        p.setIgnoreUnresolvablePlaceholders(true);
//        return p;
//    }
    
 

	
}