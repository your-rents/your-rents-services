package com.yourrents.services.common.searchable.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

import com.yourrents.services.common.searchable.Searchable;

/**
 * Annotation to set defaults when injecting a {@link Searchable} parameter into
 * a controller method.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.PARAMETER)
public @interface SearchableDefault {
    /**
     * Alias for {@link #supportedFields()}.
     * 
     * @return the list of known supported fields
     */
    @AliasFor("supportedFields")
    SearchableField[] value() default {};

    /**
     * The list of known supported fields.
     * 
     * @return the list of known supported fields
     */
    @AliasFor("value")
    SearchableField[] supportedFields() default {};

    /**
     * The default number of repeated search parameters in the OpenAPI documentation.
     * 
     * @return the default number of repeated search parameters in the OpenAPI
     */
    int repeatDefault() default 1;

    /**
     * Hide the OpenAPI documentation generation for this parameter.
     * 
     * @return true if the parameter should be hidden from the OpenAPI documentation
     */
    boolean hidden() default false;
}