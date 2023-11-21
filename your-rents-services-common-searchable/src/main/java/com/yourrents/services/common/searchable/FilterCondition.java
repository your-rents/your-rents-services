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

public class FilterCondition implements SearchCondition<String, String, String> {
    private String field;
    private String operator;
    private String value;

    public FilterCondition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    public static FilterCondition of(String field, String operator, String value) {
        return new FilterCondition(field, operator, value);
    }

    @Override
    public String getField() {
        return field;
    }

    @Override
    public String getOperator() {
        return operator;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "FilterCondition [field=" + field + ", operator=" + operator + ", value=" + value + "]";
    }

}
