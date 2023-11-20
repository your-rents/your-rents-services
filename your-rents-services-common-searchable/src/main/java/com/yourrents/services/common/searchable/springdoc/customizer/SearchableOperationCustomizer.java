package com.yourrents.services.common.searchable.springdoc.customizer;

import java.util.Arrays;
import java.util.Optional;

import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.core.MethodParameter;
import org.springframework.web.method.HandlerMethod;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;

import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.media.StringSchema;
import io.swagger.v3.oas.models.parameters.Parameter;

public class SearchableOperationCustomizer implements OperationCustomizer {
    private static final String DEFAULT_KEY_PREFIX = "k";
    private static final int DEFAULT_REPEAT = 1;

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        getSearchableMethodParameter(handlerMethod).ifPresent(methodParameter -> {
            SearchableDefault searchableDefault = methodParameter.getParameterAnnotation(SearchableDefault.class);
            if (searchableDefault != null && searchableDefault.hidden()) {
                return;
            }
            if (searchableDefault != null) {
                Arrays.stream(searchableDefault.supportedFields()).forEach(searchableField -> {
                    int repeat = getRepeat(searchableDefault, searchableField);
                    for (int i = 1; i <= repeat; i++) {
                        addFilterParameters(operation, getKey(searchableField.name(), i, repeat), searchableField.name());
                    }
                });
            }
            if (searchableDefault != null && searchableDefault.supportedFields().length == 0) {
                int repeat = getRepeat(searchableDefault);
                for (int i = 1; i <= repeat; i++) {
                    addFilterParameters(operation, getKey(i, repeat), null);
                }
            }
        });
        return operation;
    }

    private Optional<MethodParameter> getSearchableMethodParameter(HandlerMethod handlerMethod) {
        return Arrays.stream(handlerMethod.getMethodParameters())
                .filter(methodParameter -> methodParameter.getParameterType().isAssignableFrom(Searchable.class))
                .findFirst();
    }

    private void addFilterParameters(Operation operation, String key, String fieldDefault) {
        operation.addParametersItem(new Parameter().in("query")
                .name("filter." + key + ".field").description("Field name for the filter")
                .example(fieldDefault)
                .required(false)
                .schema(new io.swagger.v3.oas.models.media.StringSchema()));
        StringSchema operatorSchema = new io.swagger.v3.oas.models.media.StringSchema();
        operatorSchema.setDefault("containsIgnoreCase");
        operation.addParametersItem(new Parameter().in("query")
                .name("filter." + key + ".operator")
                .description("The comparison operator for the filter (the default is containsIgnoreCase)")
                .required(false)
                .schema(operatorSchema));
        operation.addParametersItem(new Parameter().in("query")
                .name("filter." + key + ".value").description("The comparison value for the filter")
                .required(false)
                .schema(new io.swagger.v3.oas.models.media.StringSchema()));
    }

    private int getRepeat(SearchableDefault searchableDefault) {
        return searchableDefault != null ? searchableDefault.repeatDefault() : DEFAULT_REPEAT;
    }

    private int getRepeat(SearchableDefault searchableDefault, SearchableField searchableField) {
        return searchableField != null
                ? (searchableField.repeat() >= 0 ? searchableField.repeat() : getRepeat(searchableDefault))
                : DEFAULT_REPEAT;
    }

    private String getKey(int current, int totalRepeat) {
        return totalRepeat > 1 ? DEFAULT_KEY_PREFIX + current : DEFAULT_KEY_PREFIX;
    }

    private String getKey(String prefix, int current, int totalRepeat) {
        return totalRepeat > 1 ? prefix + "." + DEFAULT_KEY_PREFIX + current : prefix;
    }

}