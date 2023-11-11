package com.yourrents.services.common.searchable;

import java.util.Map;

public interface Searchable {
    Map<String, ? extends SearchCondition<?, ?, ?>> getFilter();
}