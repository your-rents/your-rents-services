package com.yourrents.services.geodata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@TestConfiguration(proxyBeanMethods = false)
public class TestYourRentsGeoDataServiceApplication {
	static final String POSTGRES_IMAGE = "postgres:15";

	@Bean
	@ServiceConnection
	PostgreSQLContainer<?> postgresContainer() {
		return new PostgreSQLContainer<>(DockerImageName.parse(POSTGRES_IMAGE));
	}

	public static void main(String[] args) {
		SpringApplication.from(YourRentsGeoDataServiceApplication::main)
				.with(TestYourRentsGeoDataServiceApplication.class).run(args);
	}

}
