package com.java.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.filter.ShallowEtagHeaderFilter;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
@Configuration
@EnableWebMvc
@ComponentScan("com.java.controller")
public class SpringConfig {

	
	@Bean
	@Scope("singleton")
	public BeanNameUrlHandlerMapping getHandlerMapping() {
		return new BeanNameUrlHandlerMapping();
	}
	
	
	@Bean
	public ViewResolver getResolver() {
		return new InternalResourceViewResolver("/WEB-INF/views/",".jsp");
	}
	
	@Bean("eTagFilter")
	public ShallowEtagHeaderFilter getEtag() {
		ShallowEtagHeaderFilter tag= new ShallowEtagHeaderFilter();
		return tag;
	}
	
	
}
