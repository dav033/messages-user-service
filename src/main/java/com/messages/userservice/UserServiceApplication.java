package com.messages.userservice;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
     
@SpringBootApplication
@RestController
@EnableJpaRepositories("com.messages.userservice")
@ComponentScan(basePackages = "com.messages.userservice")
@EntityScan("com.messages.userservice")
public class UserServiceApplication implements CommandLineRunner {

	private static org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(UserServiceApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
		LOG.info("User Service Adpplication Started");
	}

	public void run(String... args) throws Exception {
		LOG.info("Application started ...");

	} 

	@GetMapping("/hello")
	public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
		return String.format("Hello %s!", name);
	}

	// @Configuration
	// @EnableJpaRepositories(basePackages = "com.messages.userservice")
	// public class JpaConfig {
	// }

}
