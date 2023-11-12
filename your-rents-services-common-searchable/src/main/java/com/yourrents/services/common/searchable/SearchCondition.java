package com.yourrents.services.common.searchable;

public interface SearchCondition<K, O, V> {
    K getKey();
    O getOperator();
    V getValue();
}