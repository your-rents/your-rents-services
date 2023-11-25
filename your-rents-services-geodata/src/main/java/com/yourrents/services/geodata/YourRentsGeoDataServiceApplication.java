package com.yourrents.services.geodata;

/*-
 * #%L
 * YourRents GeoData Service
 * %%
 * Copyright (C) 2023 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

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
