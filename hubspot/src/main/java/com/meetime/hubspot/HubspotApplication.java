package com.meetime.hubspot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties
public class HubspotApplication {

	public static void main(String[] args) {
		SpringApplication.run(HubspotApplication.class, args);
	}

}
