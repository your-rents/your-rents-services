package com.yourrents.services.geodata;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.yourrents.services.common.searchable.config.SearchableArgumentResolverConfigurer;

@SpringBootApplication
public class YourRentsGeoDataServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(YourRentsGeoDataServiceApplication.class, args);
	}

	@Bean
	public SearchableArgumentResolverConfigurer searchableArgumentResolverConfigurer() {
		return new SearchableArgumentResolverConfigurer();
	}

}
