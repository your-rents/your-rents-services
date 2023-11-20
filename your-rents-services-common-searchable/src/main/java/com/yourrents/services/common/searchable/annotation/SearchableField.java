package com.yourrents.services.common.searchable.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.core.annotation.AliasFor;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface SearchableField {
    /**
     * Alias for {@link #name()}.
     * 
     * @return the name of the field
     */
    @AliasFor("name")
    String value() default "";

    /**
     * The name of the field.
     * 
     * @return the name of the field
     */
    @AliasFor("value")
    String name() default "";

    /**
     * The number of times this field can be repeated in the OpenAPI documentation.
     * 
     * @return the number of times this field can be repeated in the OpenAPI.
     *   Negative values will not be considered, so the general repeat value will be used.
     */
    int repeat() default -1;
}