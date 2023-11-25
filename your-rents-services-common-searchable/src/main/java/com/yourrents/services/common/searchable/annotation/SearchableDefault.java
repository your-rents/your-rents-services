package com.yourrents.services.common.searchable.annotation;

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
    public String DEFAULT_CONFIG_KEY = "config";
    public String DEFAULT_FILTER_PREFIX = "filter";
    public int DEFAULT_REPEAT = 1;
    public String DEFAULT_SEPARATOR = ".";
    String DEFAULT_OPERATOR = "eq";
    String DEFAULT_STRING_OPERATOR = "containsIgnoreCase";

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
     * The default number of repeated search parameters in the OpenAPI
     * documentation.
     * 
     * @return the default number of repeated search parameters in the OpenAPI
     */
    int repeatDefault() default DEFAULT_REPEAT;

    /**
     * The prefix to use for each parameter.
     * 
     * @return the prefix to use for each parameter
     */
    String prefix() default DEFAULT_FILTER_PREFIX;

    /**
     * The reserved key used to specify the config parameters.
     * 
     * @return the reserved key used to specify the config parameters
     */
    String configKey() default DEFAULT_CONFIG_KEY;

    /**
     * The default operator to use for each type.
     * 
     * @return the default operator to use for each type
     */
    OperatorDefault[] defaultOperators() default {
            @OperatorDefault(type = String.class, operator = DEFAULT_STRING_OPERATOR),
            @OperatorDefault(type = Object.class, operator = DEFAULT_OPERATOR) };

    /**
     * Hide the OpenAPI documentation generation for this parameter.
     * 
     * @return true if the parameter should be hidden from the OpenAPI documentation
     */
    boolean hidden() default false;
}
