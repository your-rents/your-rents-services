package com.yourrents.services.geodata;

import org.springdoc.core.providers.ObjectMapperProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;
import com.yourrents.services.common.searchable.springdoc.SearchableOpenAPIConverter;
import com.yourrents.services.common.searchable.springdoc.customizer.SearchableOperationCustomizer;
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

	@Lazy(false)
	@Bean
	SearchableOpenAPIConverter searchableOpenAPIConverter(ObjectMapperProvider objectMapperProvider) {
		return new SearchableOpenAPIConverter(objectMapperProvider);
	}	

	@Bean
	SearchableOperationCustomizer searchableOperationCustomizer() {
		return new SearchableOperationCustomizer();
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
