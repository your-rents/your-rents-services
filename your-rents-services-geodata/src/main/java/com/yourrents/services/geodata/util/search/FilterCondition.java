package com.yourrents.services.geodata.util.search;

public class FilterCondition implements SearchCondition<String, String, String> {
    private String field;
    private String operator;
    private String value;

    public FilterCondition(String field, String operator, String value) {
        this.field = field;
        this.operator = operator;
        this.value = value;
    }

    @Override
    public String getKey() {
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
