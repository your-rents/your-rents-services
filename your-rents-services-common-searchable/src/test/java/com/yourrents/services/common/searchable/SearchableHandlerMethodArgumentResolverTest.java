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

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;
import com.yourrents.services.common.searchable.annotation.OperatorDefault;

class SearchableHandlerMethodArgumentResolverTest {

    MethodParameter supportedMethodParameter;
    SearchableHandlerMethodArgumentResolver resolver;

    @BeforeEach
    void setUp() throws Exception {
        this.supportedMethodParameter = getParameterOfMethod("supportedMethod", Searchable.class);
        GenericConversionService conversionService = new DefaultConversionService();
        this.resolver = new SearchableHandlerMethodArgumentResolver(conversionService);
    }

    @Test
    void supportsSearchable() {
        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();
    }

    @Test
    void doesNotSupportNonSearchable() {
        assertThat(resolver.supportsParameter(getParameterOfMethod("unsupportedMethod", String.class))).isFalse();
    }

    @Test
    void testResolveArgumentWithFilterValueRequestParams() throws Exception {
        var request = new MockHttpServletRequest();
        request.addParameter("filter.field1.value", "A value for field1");
        request.addParameter("filter.field2.value", "A value for field2");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0).getField()).isEqualTo("field1");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field1");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
        assertThat(conditions.get(1).getField()).isEqualTo("field2");
        assertThat(conditions.get(1).getValue()).isEqualTo("A value for field2");
        assertThat(conditions.get(1).getOperator()).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
    }

    @Test
    void testResolveArgumentWithFilterValueRequestParamsWithComplexName() throws Exception {
        var request = new MockHttpServletRequest();
        request.addParameter("filter.parent.field.value", "A value for field in parent");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("parent.field");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field in parent");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
    }

    @Test
    void testResolveArgumentWithCustomDefaultOperator() throws Exception {
        MethodParameter methodWithDefaultOperatorConfiguration = getParameterOfMethod(
                "methodWithDefaultOperatorConfiguration",
                Searchable.class);
        var request = new MockHttpServletRequest();
        request.addParameter("filter.field1.value", "A value for field1");
        request.addParameter("filter.field2.value", "A value for field2");
        request.addParameter("filter.field3.value", "0000-0000-0000-0000-0001");
        request.addParameter("filter.field4.value", "123");
        request.addParameter("filter.field5.value", "A value for field5");
        request.addParameter("filter.field5.operator", "field5SpecificOperator");

        assertThat(resolver.supportsParameter(methodWithDefaultOperatorConfiguration)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(methodWithDefaultOperatorConfiguration, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(5);
        assertThat(conditions.get(0).getField()).isEqualTo("field1");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field1");
        assertThat(conditions.get(0).getOperator()).isEqualTo("stringCustomDefaultOperator");
        assertThat(conditions.get(1).getField()).isEqualTo("field2");
        assertThat(conditions.get(1).getValue()).isEqualTo("A value for field2");
        assertThat(conditions.get(1).getOperator()).isEqualTo("stringCustomDefaultOperator");
        assertThat(conditions.get(2).getField()).isEqualTo("field3");
        assertThat(conditions.get(2).getValue()).isEqualTo(UUID.fromString("0000-0000-0000-0000-0001"));
        assertThat(conditions.get(2).getOperator()).isEqualTo("uuidCustomDefaultOperator");
        assertThat(conditions.get(3).getField()).isEqualTo("field4");
        assertThat(conditions.get(3).getValue()).isEqualTo(123);
        assertThat(conditions.get(3).getOperator()).isEqualTo(SearchableDefault.DEFAULT_OPERATOR);
        assertThat(conditions.get(4).getField()).isEqualTo("field5");
        assertThat(conditions.get(4).getValue()).isEqualTo("A value for field5");
        assertThat(conditions.get(4).getOperator()).isEqualTo("field5SpecificOperator");
    }

    @Test
    void testResolveArgumentWithAllFilterRequestParams() throws Exception {
        var request = new MockHttpServletRequest();
        request.addParameter("filter.key1.field", "field1");
        request.addParameter("filter.key1.operator", "An operator for field1");
        request.addParameter("filter.key1.value", "A value for field1");
        request.addParameter("filter.key2.field", "field2");
        request.addParameter("filter.key2.operator", "An operator for field2");
        request.addParameter("filter.key2.value", "A value for field2");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0).getField()).isEqualTo("field1");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field1");
        assertThat(conditions.get(0).getOperator()).isEqualTo("An operator for field1");
        assertThat(conditions.get(1).getField()).isEqualTo("field2");
        assertThat(conditions.get(1).getValue()).isEqualTo("A value for field2");
        assertThat(conditions.get(1).getOperator()).isEqualTo("An operator for field2");
    }

    @Test
    void testResolveArgumentWithCombinatorParam() throws Exception {
        var request = new MockHttpServletRequest();
        request.addParameter("filter.key1.field", "field1");
        request.addParameter("filter.key1.operator", "An operator for field1");
        request.addParameter("filter.key1.value", "A value for field1");
        request.addParameter("filter.config.combinator", "OR");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("field1");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field1");
        assertThat(conditions.get(0).getOperator()).isEqualTo("An operator for field1");
        assertThat(result.getCombinator()).isEqualTo(EnumCombinator.OR);
    }

    @Test
    void testResolveArgumentWithFilterPrefix() throws Exception {
        MethodParameter methodWithPrefixConfiguration = getParameterOfMethod("methodWithPrefixConfiguration",
                Searchable.class);

        var request = new MockHttpServletRequest();
        request.addParameter("customPrefix.field.value", "A value for field");

        assertThat(resolver.supportsParameter(methodWithPrefixConfiguration)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(methodWithPrefixConfiguration, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("field");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
    }

    @Test
    void testResolveArgumentWithConfigKeyConfiguration() throws Exception {
        MethodParameter methodWithConfigKeyConfiguration = getParameterOfMethod("methodWithConfigKeyConfiguration",
                Searchable.class);

        var request = new MockHttpServletRequest();
        request.addParameter("filter.field.value", "A value for field");
        request.addParameter("filter.customConfigKey.combinator", "OR");

        assertThat(resolver.supportsParameter(methodWithConfigKeyConfiguration)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(methodWithConfigKeyConfiguration, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("field");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_STRING_OPERATOR);
        assertThat(result.getCombinator()).isEqualTo(EnumCombinator.OR);
    }

    @Test
    void testResolveArgumentWithIntegerSearchParam() throws Exception {
        MethodParameter methodWithIntegerSearchParam = getParameterOfMethod("methodWithIntegerSearchParam",
                Searchable.class);

        var request = new MockHttpServletRequest();
        request.addParameter("filter.field.value", "123");

        assertThat(resolver.supportsParameter(methodWithIntegerSearchParam)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(methodWithIntegerSearchParam, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("field");
        assertThat(conditions.get(0).getValue()).isEqualTo(123);
        assertThat(conditions.get(0).getValue()).isInstanceOf(Integer.class);
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_OPERATOR);
    }

    @Test
    void testResolveArgumentWithUUIDSearchParam() throws Exception {
        MethodParameter methodWithIntegerSearchParam = getParameterOfMethod("methodWithUUIDSearchParam",
                Searchable.class);

        var request = new MockHttpServletRequest();
        request.addParameter("filter.field.value", "0000-0000-0000-0000-0001");

        assertThat(resolver.supportsParameter(methodWithIntegerSearchParam)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(methodWithIntegerSearchParam, null,
                new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("field");
        assertThat(conditions.get(0).getValue()).isEqualTo(UUID.fromString("0000-0000-0000-0000-0001"));
        assertThat(conditions.get(0).getValue()).isInstanceOf(UUID.class);
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableDefault.DEFAULT_OPERATOR);
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
        void supportedMethod(Searchable searchable);

        void unsupportedMethod(String string);

        void methodWithPrefixConfiguration(@SearchableDefault(prefix = "customPrefix") Searchable searchable);

        void methodWithConfigKeyConfiguration(@SearchableDefault(configKey = "customConfigKey") Searchable searchable);

        void methodWithIntegerSearchParam(
                @SearchableDefault(supportedFields = {
                        @SearchableField(name = "field", type = Integer.class) }) Searchable searchable);

        void methodWithUUIDSearchParam(
                @SearchableDefault(supportedFields = {
                        @SearchableField(name = "field", type = UUID.class) }) Searchable searchable);

        void methodWithDefaultOperatorConfiguration(
                @SearchableDefault(supportedFields = {
                        @SearchableField(name = "field2", type = String.class),
                        @SearchableField(name = "field3", type = UUID.class),
                        @SearchableField(name = "field4", type = Integer.class) }, defaultOperators = {
                                @OperatorDefault(type = String.class, operator = "stringCustomDefaultOperator"),
                                @OperatorDefault(type = UUID.class, operator = "uuidCustomDefaultOperator") }) Searchable searchable);
    }
}
