package com.springboot.appbanco;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration

public class AppConfig {
	
	@Value("${config.base.endpoint.client}")
	private String url;
	
	

	@Bean
	@Qualifier("client")
	public WebClient registrarWebClient() {

		return WebClient.create(url); //EndPoint para conectarse con ese MS.
	}
	
	
}
