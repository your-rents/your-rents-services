package com.yourrents.services.common.searchable;

/*-
 * #%L
 * YourRents Common Searchable
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;

public class SearchableHandlerMethodArgumentResolver
        implements HandlerMethodArgumentResolver, ApplicationContextAware, InitializingBean {
    private static final String DEFAULT_FILTER_PREFIX = "filter";
    public static final String DEFAULT_OPERATOR = "eq";
    public static final String DEFAULT_STRING_OPERATOR = "containsIgnoreCase";
    private static final Logger log = LoggerFactory.getLogger(SearchableHandlerMethodArgumentResolver.class);

    private ApplicationContext applicationContext;
    private GenericConversionService conversionService;

    public SearchableHandlerMethodArgumentResolver() {
    }

    public SearchableHandlerMethodArgumentResolver(GenericConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Searchable.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String filterPrefix = getFilterPrefix(parameter);
        List<FilterCondition> conditions = new ArrayList<FilterCondition>();
        List<String> filterKeys = new ArrayList<String>();
        webRequest.getParameterMap().forEach((k, v) -> {
            if (k.startsWith(filterPrefix)) {
                int keyEndDelimiter = k.lastIndexOf('.');
                if (keyEndDelimiter < filterPrefix.length()) {
                    throw new IllegalArgumentException("Invalid filter parameter: " + k);
                }
                String filterBase = k.substring(0, keyEndDelimiter);
                String key = filterBase.substring(filterPrefix.length(), filterBase.length());
                if (!filterKeys.contains(key)) {
                    log.debug("Found filter parameter: {}", k);
                    log.debug("Extracted filter base: {}", filterBase);
                    log.debug("Extracted filter key: {}", key);
                    filterKeys.add(key);
                    String field = Objects.requireNonNullElse(webRequest.getParameter(filterBase + ".field"), key);
                    String operator = Objects.requireNonNullElse(webRequest.getParameter(filterBase + ".operator"),
                            getDefaultOperator(field, parameter));
                    Object value = convertValue(field,
                            Objects.requireNonNullElse(webRequest.getParameter(filterBase + ".value"), ""),
                            parameter);
                    conditions.add(new FilterCondition(field, operator, value));
                }
            }
        });
        FilterCriteria result = new FilterCriteria(conditions);
        log.debug("Searchable parameter resolved to: {}", result);
        return result;
    }


    private String getFilterPrefix(MethodParameter parameter) {
        String result = DEFAULT_FILTER_PREFIX;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            result = defaults.prefix();
        }
        return result + ".";
    }

    private String getDefaultOperator(String field, MethodParameter parameter) {
        String result = DEFAULT_STRING_OPERATOR;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            Optional<SearchableField> searchableField = Arrays.stream(defaults.supportedFields())
                    .filter(sf -> sf.name().equals(field))
                    .findFirst();
            if (searchableField.isPresent()) {
                Class<?> targetType = searchableField.get().type();
                if (targetType != String.class) {
                    result = DEFAULT_OPERATOR;
                }
            }
        }
        return result;
    }

    private Object convertValue(String field, String stringValue, MethodParameter parameter) {
        Object result = stringValue;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            Optional<SearchableField> searchableField = Arrays.stream(defaults.supportedFields())
                    .filter(sf -> sf.name().equals(field))
                    .findFirst();
            if (searchableField.isPresent()) {
                Class<?> targetType = searchableField.get().type();
                if (targetType != String.class) {
                    result = conversionService.convert(stringValue, targetType);
                }
            }
        }
        return result;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.conversionService == null) {
            this.conversionService = applicationContext.getBean(GenericConversionService.class);
        }
    }

}
