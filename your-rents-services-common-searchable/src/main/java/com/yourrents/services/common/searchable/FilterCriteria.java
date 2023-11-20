package com.yourrents.services.common.searchable;

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