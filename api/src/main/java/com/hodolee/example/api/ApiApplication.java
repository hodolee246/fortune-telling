package com.hodolee.example.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
		"com.hodolee.example.api",
		"com.hodolee.example.core",
		"com.hodolee.example.domain",
		"com.hodolee.example.infra"
})
@EnableJpaRepositories(basePackages = "com.hodolee.example.domain.fortune.domain")
@EntityScan(basePackages = "com.hodolee.example.domain.fortune.domain")
public class ApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiApplication.class, args);
	}

}
