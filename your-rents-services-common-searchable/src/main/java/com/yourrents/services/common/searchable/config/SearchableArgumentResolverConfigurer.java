package com.yourrents.services.common.searchable.config;

import java.util.List;

import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.yourrents.services.common.searchable.SearchableHandlerMethodArgumentResolver;

@Component
public class SearchableArgumentResolverConfigurer implements WebMvcConfigurer {
    
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new SearchableHandlerMethodArgumentResolver());
    }
}