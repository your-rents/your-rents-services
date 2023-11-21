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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FilterCriteria implements Searchable {
    private List<FilterCondition> conditions;

    public FilterCriteria() {
        this.conditions = new ArrayList<>();
    }

    public FilterCriteria(List<FilterCondition> conditions) {
        this.conditions = conditions;
    }

    public static FilterCriteria of(FilterCondition... conditions) {
        return new FilterCriteria(Arrays.asList(conditions));
    }

    public FilterCriteria addCondition(FilterCondition condition) {
        conditions.add(condition);
        return this;
    }

    @Override
    public List<FilterCondition> getFilter() {
        return this.conditions;
    }

    @Override
    public String toString() {
        return "FilterCriteria [conditions=" + conditions + "]";
    }

}
