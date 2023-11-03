package com.yourrents.services.geodata.util.search;

import java.util.List;
import java.util.Map;

public class FilterCriteria implements Searchable {
    private List<FilterCondition> conditions;

    public FilterCriteria(List<FilterCondition> conditions) {
        this.conditions = conditions;
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