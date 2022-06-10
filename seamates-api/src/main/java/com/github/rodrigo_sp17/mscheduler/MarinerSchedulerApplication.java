package com.github.rodrigo_sp17.mscheduler;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableAsync
public class MarinerSchedulerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MarinerSchedulerApplication.class, args);
	}

	@Bean
	public OpenAPI customOpenApi() {
		return new OpenAPI().components(
				new Components()
						.addSecuritySchemes(
								"bearer-key",
								new SecurityScheme()
										.type(SecurityScheme.Type.HTTP)
										.scheme("bearer")
										.bearerFormat("JWT")))
				.info(new Info()
						.title("Marine Scheduler API")
						.description("This server stores and shares work shifts among friends.")
						.license(new License().name("GNU GPL-3.0").url("https://choosealicense.com/licenses/gpl-3.0/"))
				);
	}

}
