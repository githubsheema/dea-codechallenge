package com.mycompany.ipv4manager;

/**
 * @author Mathivanan Manickam
 * Entry point for the application
 * 
 */
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;

@SpringBootApplication
public class Ipv4managerApplication {

	public static void main(String[] args) {
		SpringApplication.run(Ipv4managerApplication.class, args);
	}

	@Bean
	public MethodValidationPostProcessor methodValidationPostProcessor() {
		return new MethodValidationPostProcessor();
	}

}
