package com.yourrents.services.geodata;

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