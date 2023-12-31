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
     * The type to which this field should be converted.
     * 
     * @return the type to which this field should be converted
     */
    Class<?> type() default String.class;

    /**
     * The number of times this field can be repeated in the OpenAPI documentation.
     * 
     * @return the number of times this field can be repeated in the OpenAPI.
     *         Negative values will not be considered, so the general repeat value
     *         will be used.
     */
    int repeat() default -1;
}
