package com.yourrents.services.common.searchable;

import static org.assertj.core.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

class SearchableHandlerMethodArgumentResolverTest {

    MethodParameter supportedMethodParameter;
    HandlerMethodArgumentResolver resolver;

    @BeforeEach
    void setUp() throws Exception {
        this.supportedMethodParameter = getParameterOfMethod("supportedMethod", Searchable.class);
        this.resolver = new SearchableHandlerMethodArgumentResolver();                
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
    @SuppressWarnings("null")
    void testResolveArgumentWithFilterValueRequestParams() throws Exception {
		var request = new MockHttpServletRequest();
		request.addParameter("filter.field1.value", "A value for field1");
		request.addParameter("filter.field2.value", "A value for field2");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null, new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(2);
        assertThat(conditions.get(0).getField()).isEqualTo("field1");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field1");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableHandlerMethodArgumentResolver.DEFAULT_OPERATOR);
        assertThat(conditions.get(1).getField()).isEqualTo("field2");
        assertThat(conditions.get(1).getValue()).isEqualTo("A value for field2");
        assertThat(conditions.get(1).getOperator()).isEqualTo(SearchableHandlerMethodArgumentResolver.DEFAULT_OPERATOR);
    }

    @Test
    @SuppressWarnings("null")
    void testResolveArgumentWithFilterValueRequestParamsWithComplexName() throws Exception {
		var request = new MockHttpServletRequest();
		request.addParameter("filter.parent.field.value", "A value for field in parent");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null, new ServletWebRequest(request), null);
        assertThat(result).isNotNull();
        List<? extends SearchCondition<?, ?, ?>> conditions = result.getFilter();
        assertThat(conditions).isNotNull();
        assertThat(conditions).hasSize(1);
        assertThat(conditions.get(0).getField()).isEqualTo("parent.field");
        assertThat(conditions.get(0).getValue()).isEqualTo("A value for field in parent");
        assertThat(conditions.get(0).getOperator()).isEqualTo(SearchableHandlerMethodArgumentResolver.DEFAULT_OPERATOR);
    }

    @Test
    @SuppressWarnings("null")
    void testResolveArgumentWithAllFilterRequestParams() throws Exception {
		var request = new MockHttpServletRequest();
		request.addParameter("filter.key1.field", "field1");
		request.addParameter("filter.key1.operator", "An operator for field1");
		request.addParameter("filter.key1.value", "A value for field1");
        request.addParameter("filter.key2.field", "field2");
        request.addParameter("filter.key2.operator", "An operator for field2");
		request.addParameter("filter.key2.value", "A value for field2");

        assertThat(resolver.supportsParameter(supportedMethodParameter)).isTrue();

        Searchable result = (Searchable) resolver.resolveArgument(supportedMethodParameter, null, new ServletWebRequest(request), null);
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
    }
}
