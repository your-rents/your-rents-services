package com.yourrents.services.geodata.util.search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    public Map<String, FilterCondition> getFilter() {
        return conditions.stream().collect(java.util.stream.Collectors.toMap(FilterCondition::getKey, c -> c));
    }

    @Override
    public String toString() {
        return "FilterCriteria [conditions=" + conditions + "]";
    }

}