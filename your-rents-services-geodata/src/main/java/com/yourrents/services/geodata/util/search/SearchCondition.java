package com.yourrents.services.geodata.util.search;

public interface SearchCondition<K, O, V> {
    K getKey();
    O getOperator();
    V getValue();
}