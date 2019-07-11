package com.esolutions.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@Configuration
public class YodleeBeans {

	@Bean("restTemplate")
	public RestTemplate getRestemplate() {
		return new RestTemplate();
	}

	@Bean("mapper")
	public ObjectMapper getObjectMapper() {
		return new ObjectMapper();
	}

	@Bean("headers")
	public YodleeAPIHeaders getYodleeAPIHeaders() {
		return new YodleeAPIHeaders();
	}

}
