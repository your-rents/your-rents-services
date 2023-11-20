package com.yourrents.services.common.searchable;

public interface SearchCondition<K, O, V> {
    K getField();
    O getOperator();
    V getValue();
}