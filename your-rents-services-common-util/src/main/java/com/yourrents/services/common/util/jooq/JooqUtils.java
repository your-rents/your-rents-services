package com.yourrents.services.common.util.jooq;

import static org.jooq.impl.DSL.*;

import java.util.function.Function;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.SelectFinalStep;
import org.jooq.SelectQuery;
import org.jooq.SortField;
import org.jooq.Table;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.yourrents.services.common.searchable.Searchable;

@Service
public class JooqUtils {

        public JooqUtils() {
        }

        /**
         * Paginate a query
         * 
         * Inspired by:
         * https://blog.jooq.org/calculating-pagination-metadata-without-extra-roundtrips-in-sql/
         * 
         * @author Lucio Benfante 
         * 
         * @param ctx
         * @param original
         * @param limit
         * @param offset
         * @return
         */
        public Select<?> paginate(
                        DSLContext ctx,
                        Select<?> original,
                        int limit,
                        long offset) {
                Table<?> u = original.asTable("u");
                Field<Integer> totalRows = count().over().as("total_rows");
                Field<Integer> row = rowNumber().over().as("row");

                Table<?> t = ctx
                                .select(u.asterisk())
                                .select(totalRows, row)
                                .from(u)
                                .limit(limit)
                                .offset(offset)
                                .asTable("t");

                Select<?> result = ctx
                                .select(t.fields(original.getSelect().toArray(Field[]::new)))
                                .select(
                                                count().over().as("actual_page_size"),
                                                field(max(t.field(row)).over().eq(t.field(totalRows)))
                                                                .as("last_page"),
                                                t.field(totalRows),
                                                t.field(row),
                                                t.field(row).minus(inline(1)).div(limit).plus(inline(1))
                                                                .as("current_page"))
                                .from(t);
                return result;
        }

        private Condition getCondition(Searchable filter, Function<String, Field<?>> fieldMapper) {
                return getCondition(filter, fieldMapper, true);
        }

        private Condition getCondition(Searchable filter, Function<String, Field<?>> fieldMapper,
                        boolean ignoreNotSupported) {
                return filter.getFilter().stream()
                                .filter(c -> !ignoreNotSupported || isFieldSupported(c.getField().toString(), fieldMapper))
                                .map(c -> {
                                        Field<?> field = fieldMapper.apply(c.getField().toString());
                                        return buildStringCondition(
                                                        field.coerce(String.class),
                                                        c.getOperator().toString(),
                                                        c.getValue().toString());
                                }).reduce(trueCondition(), Condition::and);
        }

        private SortField<?>[] getSortFields(Pageable pageable, Function<String, Field<?>> fieldMapper) {
                return getSortFields(pageable, fieldMapper, true);
        }

        private SortField<?>[] getSortFields(Pageable pageable, Function<String, Field<?>> fieldMapper,
                        boolean ignoreNotSupported) {
                return pageable.getSort()
                                .filter(sort -> !ignoreNotSupported
                                                || isFieldSupported(sort.getProperty(), fieldMapper))
                                .map(sort -> {
                                        Field<?> field = fieldMapper.apply(sort.getProperty());
                                        if (sort.isAscending()) {
                                                return field.asc();
                                        } else {
                                                return field.desc();
                                        }
                                }).stream().toArray(SortField[]::new);
        }

        private boolean isFieldSupported(String field, Function<String, Field<?>> fieldMapper) {
                try {
                        if (fieldMapper.apply(field) != null) {
                                return true;
                        } else {
                                return false;
                        }
                } catch (IllegalArgumentException e) {
                        return false;
                }
        }

        public Select<?> getQueryWithConditionsAndSorts(SelectFinalStep<?> query,
                        Searchable filter, Function<String, Field<?>> filterFieldMapper,
                        Pageable pageable, Function<String, Field<?>> sortFieldMapper) {
                SelectQuery<?> result = query.getQuery();
                result.addConditions(getCondition(filter, filterFieldMapper));
                result.addOrderBy(getSortFields(pageable, sortFieldMapper));
                return result;
        }

        /**
         * Build a condition for a field
         * 
         * @param field
         * @param operator
         * @param value
         * @return
         */
        private Condition buildStringCondition(Field<String> field, String operator, String value) {
                return switch (operator) {
                        case "eq" -> field.eq(value);
                        case "ne" -> field.ne(value);
                        case "gt" -> field.gt(value);
                        case "ge" -> field.ge(value);
                        case "lt" -> field.lt(value);
                        case "le" -> field.le(value);
                        case "contains" -> field.contains(value);
                        case "containsIgnoreCase" -> field.containsIgnoreCase(value);
                        case "startsWith" -> field.startsWith(value);
                        case "endsWith" -> field.endsWith(value);
                        default -> throw new IllegalArgumentException("Unsupported operator: " + operator);
                };
        }
}