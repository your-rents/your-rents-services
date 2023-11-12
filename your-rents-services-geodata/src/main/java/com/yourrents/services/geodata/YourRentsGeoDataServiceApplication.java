package com.yourrents.services.geodata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;
import com.yourrents.services.common.util.exception.GlobalExceptionHandler;
import com.yourrents.services.common.util.jooq.JooqUtils;

@SpringBootApplication
public class YourRentsGeoDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(YourRentsGeoDataServiceApplication.class, args);
	}

	@Bean
	public SearchableArgumentResolverConfigurer searchableArgumentResolverConfigurer() {
		return new SearchableArgumentResolverConfigurer();
	}

	@Bean
	public GlobalExceptionHandler globalExceptionHandler() {
		return new GlobalExceptionHandler();
	}

	@Bean
	public JooqUtils jooqUtils() {
		return new JooqUtils();
	}
}
