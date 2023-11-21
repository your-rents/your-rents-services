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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {

    @Bean
    PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer(
            @Value("${spring.data.web.pageable.max-page-size:2000}") int maxPageSize,
            @Value("${spring.data.web.pageable.default-page-size:20}") int defaultPageSize) {
        return pageableResolver -> {
            pageableResolver.setMaxPageSize(maxPageSize);
            pageableResolver.setFallbackPageable(PageRequest.ofSize(defaultPageSize));
        };
    }

}
