package com.yourrents.services.common.searchable;

import java.util.List;

public interface Searchable {
    List<? extends SearchCondition<?, ?, ?>> getFilter();
}