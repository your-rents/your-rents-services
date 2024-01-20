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

import static org.assertj.core.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import com.yourrents.services.common.searchable.annotation.OperatorDefault;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;
import com.yourrents.services.common.searchable.springdoc.model.Searchable;

public class SearchableOperationCustomizerTest {

    private SearchableOperationCustomizer customizer;

    @BeforeEach
    void setUp() throws Exception {
        this.customizer = new SearchableOperationCustomizer();
    }

    @Test
    void defaultStringOperator() {
        assertThat(customizer.getDefaulOperator(null, null)).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
    }

    @Test
    void defaultObjectOperator() {
        MethodParameter methodParameter = getParameterOfMethod("methodWithObjectField",
                Searchable.class);
        SearchableDefault searchableDefault = methodParameter.getParameterAnnotation(SearchableDefault.class);
        SearchableField searchableField = searchableDefault.supportedFields()[0];
        assertThat(customizer.getDefaulOperator(searchableDefault.defaultOperators(), searchableField))
                .isEqualTo(SearchableDefault.DEFAULT_OPERATOR);
    }

    @Test
    void customizedDefaultOperatorsWithNoSupportedFields() {
        MethodParameter methodParameter = getParameterOfMethod("methodWithCustomOperators",
                Searchable.class);
        SearchableDefault searchableDefault = methodParameter.getParameterAnnotation(SearchableDefault.class);
        assertThat(customizer.getDefaulOperator(searchableDefault.defaultOperators(), null))
                .isEqualTo("customStringOperator");
    }

    @Test
    void customizedDefaultOperatorsWithSupportedFields() {
        MethodParameter methodParameter = getParameterOfMethod("methodWithCustomOperatorsAndFields",
                Searchable.class);
        SearchableDefault searchableDefault = methodParameter.getParameterAnnotation(SearchableDefault.class);
        assertThat(customizer.getDefaulOperator(searchableDefault.defaultOperators(), searchableDefault.supportedFields()[0]))
                .isEqualTo("customStringOperator");
        assertThat(customizer.getDefaulOperator(searchableDefault.defaultOperators(), searchableDefault.supportedFields()[1]))
                .isEqualTo("customObjectOperator");
    }


    private Class<?> getControllerClass() {
        return Sample.class;
    }

    private MethodParameter getParameterOfMethod(String name, Class<?>... argumentTypes) {
        try {
            return new MethodParameter(getControllerClass().getMethod(name, argumentTypes), 0);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private interface Sample {
        void methodWithNoCustomization(Searchable searchable);

        void methodWithObjectField(
                @SearchableDefault(supportedFields = @SearchableField(name = "field1", type = UUID.class)) Searchable searchable);

        void methodWithCustomOperators(@SearchableDefault(defaultOperators = {
                @OperatorDefault(type = String.class, operator = "customStringOperator"),
                @OperatorDefault(type = Object.class, operator = "customObjectOperator")
        }) Searchable searchable);

        void methodWithCustomOperatorsAndFields(@SearchableDefault(
            supportedFields = {
                @SearchableField(name = "field1", type = String.class),
                @SearchableField(name = "field2", type = UUID.class)
            },
            defaultOperators = {
                @OperatorDefault(type = String.class, operator = "customStringOperator"),
                @OperatorDefault(type = Object.class, operator = "customObjectOperator")
        }) Searchable searchable);

    }
}
