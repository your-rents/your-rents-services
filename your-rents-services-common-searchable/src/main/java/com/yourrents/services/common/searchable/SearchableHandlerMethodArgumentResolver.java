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
        List<FilterCondition> conditions = new ArrayList<FilterCondition>();
        List<String> filterNames = new ArrayList<String>();
        webRequest.getParameterMap().forEach((k, v) -> {
            if (k.startsWith("filter[")) {
                int nameEndDelimiter = k.indexOf(']', 7);
                if (nameEndDelimiter < 0) {
                    throw new IllegalArgumentException("Invalid filter parameter: " + k);
                }
                String filterBase = k.substring(0, nameEndDelimiter + 1);
                String name = filterBase.substring(7, filterBase.length() - 1);
                if (!filterNames.contains(name)) {
                    filterNames.add(name);
                    String key = Objects.requireNonNullElse(webRequest.getParameter(filterBase + "[key]"), name);
                    String operator = Objects.requireNonNullElse(webRequest.getParameter(filterBase + "[operator]"), DEFAULT_OPERATOR);
                    String value = Objects.requireNonNullElse(webRequest.getParameter(filterBase + "[value]"), "");
                    conditions.add(new FilterCondition(key, operator, value));
                }
            }
        });
        FilterCriteria result = new FilterCriteria(conditions);
        log.debug("Searchable parameter resolved to: {}", result);
        return result;
    }

}