package com.yourrents.services.common.searchable;

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
