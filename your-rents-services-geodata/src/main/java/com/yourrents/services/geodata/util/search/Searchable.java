package com.yourrents.services.geodata.util.search;

import java.util.Map;

public interface Searchable {
    Map<String, ? extends SearchCondition<?, ?, ?>> getFilter();
}