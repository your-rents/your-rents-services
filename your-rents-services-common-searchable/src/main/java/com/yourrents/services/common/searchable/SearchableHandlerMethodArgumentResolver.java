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
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.yourrents.services.common.searchable.annotation.OperatorDefault;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;

public class SearchableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    private static final Logger log = LoggerFactory.getLogger(SearchableHandlerMethodArgumentResolver.class);

    private ConversionService conversionService;

    public SearchableHandlerMethodArgumentResolver() {
        this.conversionService = DefaultConversionService.getSharedInstance();
    }

    public SearchableHandlerMethodArgumentResolver(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Searchable.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        List<FilterCondition> conditions = new ArrayList<FilterCondition>();
        List<String> filterKeys = new ArrayList<String>();
        Map<String, String[]> parameterMap = webRequest.getParameterMap();
        String filterPrefix = getFilterPrefix(parameter);
        String configPrefix = filterPrefix + getConfigKey(parameter) + ".";
        log.debug("Searching combinator configuration parameter: {}", configPrefix + "combinator");
        String combinatorValue = Objects.requireNonNullElse(webRequest.getParameter(configPrefix + "combinator"),
                EnumCombinator.AND.name());
        EnumCombinator combinator = EnumCombinator.valueOf(combinatorValue);
        parameterMap.forEach((k, v) -> {
            if (k.startsWith(filterPrefix) && !k.startsWith(configPrefix)) {
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
        FilterCriteria result = new FilterCriteria(conditions, combinator);
        log.debug("Searchable parameter resolved to: {}", result);
        return result;
    }

    private String getFilterPrefix(MethodParameter parameter) {
        String result = SearchableDefault.DEFAULT_FILTER_PREFIX;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            result = defaults.prefix();
        }
        return result + ".";
    }

    private String getConfigKey(MethodParameter parameter) {
        String result = SearchableDefault.DEFAULT_CONFIG_KEY;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            result = defaults.configKey();
        }
        return result;
    }

    private String getDefaultOperator(String field, MethodParameter parameter) {
        String result = SearchableDefault.DEFAULT_STRING_OPERATOR;
        SearchableDefault defaults = parameter.getParameterAnnotation(SearchableDefault.class);
        if (defaults != null) {
            Optional<SearchableField> searchableField = Arrays.stream(defaults.supportedFields())
                    .filter(sf -> sf.name().equals(field))
                    .findFirst();
            if (searchableField.isPresent()) {
                Class<?> targetType = searchableField.get().type();
                Optional<OperatorDefault> operatorDefault = Arrays.stream(defaults.defaultOperators())
                        .filter(od -> od.type().isAssignableFrom(targetType))
                        .findFirst();
                if (operatorDefault.isPresent()) {
                    result = operatorDefault.get().operator();
                } else {
                    if (targetType != String.class) {
                        result = SearchableDefault.DEFAULT_OPERATOR;
                    }
                }
            } else {
                Class<?> targetType = String.class;
                Optional<OperatorDefault> operatorDefault = Arrays.stream(defaults.defaultOperators())
                        .filter(od -> od.type().isAssignableFrom(targetType))
                        .findFirst();
                if (operatorDefault.isPresent()) {
                    result = operatorDefault.get().operator();
                } else {
                    result = SearchableDefault.DEFAULT_STRING_OPERATOR;
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

}
