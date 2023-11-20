package com.yourrents.services.common.searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

public class SearchableHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    public static final String DEFAULT_OPERATOR = "containsIgnoreCase";
    private static final Logger log = LoggerFactory.getLogger(SearchableHandlerMethodArgumentResolver.class);

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return Searchable.class.equals(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, @Nullable ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest, @Nullable WebDataBinderFactory binderFactory) throws Exception {
        String filterPrefix = "filter.";
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
                    String operator = Objects.requireNonNullElse(webRequest.getParameter(filterBase + ".operator"), DEFAULT_OPERATOR);
                    String value = Objects.requireNonNullElse(webRequest.getParameter(filterBase + ".value"), "");
                    conditions.add(new FilterCondition(field, operator, value));
                }
            }
        });
        FilterCriteria result = new FilterCriteria(conditions);
        log.debug("Searchable parameter resolved to: {}", result);
        return result;
    }

}