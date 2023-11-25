package com.yourrents.services.common.searchable.springdoc.customizer;

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

    @Override
    public Operation customize(Operation operation, HandlerMethod handlerMethod) {
        getSearchableMethodParameter(handlerMethod).ifPresent(methodParameter -> {
            SearchableDefault searchableDefault = methodParameter.getParameterAnnotation(SearchableDefault.class);
            if (searchableDefault != null && searchableDefault.hidden()) {
                return;
            }
            String filterPrefix = getFilterPrefix(searchableDefault);
            operation.addParametersItem(new Parameter().in("query")
                    .name(filterPrefix + SearchableDefault.DEFAULT_SEPARATOR + getConfigKey(searchableDefault)
                            + SearchableDefault.DEFAULT_SEPARATOR + "combinator")
                    .description("The combinator to use for combining the filter conditions")
                    .required(false)
                    .schema(new io.swagger.v3.oas.models.media.StringSchema().addEnumItem("AND").addEnumItem("OR")));
            if (searchableDefault != null) {
                Arrays.stream(searchableDefault.supportedFields()).forEach(searchableField -> {
                    int repeat = getRepeat(searchableDefault, searchableField);
                    for (int i = 1; i <= repeat; i++) {
                        addFilterParameters(operation, filterPrefix, getKey(searchableField.name(), i, repeat),
                                getDefaulOperator(searchableField), searchableField.name());
                    }
                });
            }
            if (searchableDefault != null && searchableDefault.supportedFields().length == 0) {
                int repeat = getRepeat(searchableDefault);
                for (int i = 1; i <= repeat; i++) {
                    addFilterParameters(operation, filterPrefix, getKey(i, repeat),
                            getDefaulOperator(null), null);
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

    private void addFilterParameters(Operation operation, String prefix, String key, String operatorDefault,
            String fieldDefault) {
        operation.addParametersItem(new Parameter().in("query")
                .name(prefix + SearchableDefault.DEFAULT_SEPARATOR + key + SearchableDefault.DEFAULT_SEPARATOR
                        + "field")
                .description("Field name for the filter")
                .example(fieldDefault)
                .required(false)
                .schema(new io.swagger.v3.oas.models.media.StringSchema()));
        StringSchema operatorSchema = new io.swagger.v3.oas.models.media.StringSchema();
        operatorSchema.setDefault(operatorDefault);
        operation.addParametersItem(new Parameter().in("query")
                .name(prefix + SearchableDefault.DEFAULT_SEPARATOR + key + SearchableDefault.DEFAULT_SEPARATOR
                        + "operator")
                .description("The comparison operator for the filter (the default is containsIgnoreCase)")
                .required(false)
                .schema(operatorSchema));
        operation.addParametersItem(new Parameter().in("query")
                .name(prefix + SearchableDefault.DEFAULT_SEPARATOR + key + SearchableDefault.DEFAULT_SEPARATOR
                        + "value")
                .description("The comparison value for the filter")
                .required(false)
                .schema(new io.swagger.v3.oas.models.media.StringSchema()));
    }

    private int getRepeat(SearchableDefault searchableDefault) {
        return searchableDefault != null ? searchableDefault.repeatDefault() : SearchableDefault.DEFAULT_REPEAT;
    }

    private int getRepeat(SearchableDefault searchableDefault, SearchableField searchableField) {
        return searchableField != null
                ? (searchableField.repeat() >= 0 ? searchableField.repeat() : getRepeat(searchableDefault))
                : SearchableDefault.DEFAULT_REPEAT;
    }

    private String getDefaulOperator(SearchableField searchableField) {
        if (searchableField != null) {
            return searchableField.type() == String.class ? SearchableDefault.DEFAULT_STRING_OPERATOR
                    : SearchableDefault.DEFAULT_OPERATOR;
        }
        return SearchableDefault.DEFAULT_STRING_OPERATOR;
    }

    private String getKey(int current, int totalRepeat) {
        return totalRepeat > 1 ? DEFAULT_KEY_PREFIX + current : DEFAULT_KEY_PREFIX;
    }

    private String getKey(String prefix, int current, int totalRepeat) {
        return totalRepeat > 1 ? prefix + SearchableDefault.DEFAULT_SEPARATOR + DEFAULT_KEY_PREFIX + current : prefix;
    }

    private String getConfigKey(SearchableDefault searchableDefault) {
        return searchableDefault != null ? searchableDefault.configKey() : SearchableDefault.DEFAULT_CONFIG_KEY;
    }

    private String getFilterPrefix(SearchableDefault searchableDefault) {
        return searchableDefault != null ? searchableDefault.prefix() : SearchableDefault.DEFAULT_FILTER_PREFIX;
    }

}
