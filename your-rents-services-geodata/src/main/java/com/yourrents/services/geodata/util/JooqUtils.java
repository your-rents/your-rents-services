package com.yourrents.services.geodata.util;

import static org.jooq.impl.DSL.*;

import java.util.ArrayList;
import java.util.List;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Select;
import org.jooq.SortField;
import org.jooq.SortOrder;
import org.jooq.Table;

public class JooqUtils {

        /**
         * Paginate a query
         * 
         * Thanks to
         * https://blog.jooq.org/calculating-pagination-metadata-without-extra-roundtrips-in-sql/
         * 
         * @param ctx
         * @param original
         * @param sort
         * @param limit
         * @param offset
         * @return
         */
        public static Select<?> paginate(
                        DSLContext ctx,
                        Select<?> original,
                        SortField<?>[] sort,
                        int limit,
                        long offset) {
                Table<?> u = original.asTable("u");
                Field<Integer> totalRows = count().over().as("total_rows");
                Field<Integer> row = rowNumber().over().orderBy(adaptSortFieldsToTable(sort, u)).as("row");

                Table<?> t = ctx
                                .select(u.asterisk())
                                .select(totalRows, row)
                                .from(u)
                                .orderBy(adaptSortFieldsToTable(sort, u))
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
                                .from(t)
                                .orderBy(adaptSortFieldsToTable(sort, t));
                return result;
        }

        private static SortField<?>[] adaptSortFieldsToTable(SortField<?>[] sort, Table<?> table) {
                List<SortField<?>> result = new ArrayList<>();
                for (SortField<?> sortField : sort) {
                        Field<?> field = table.field(sortField.getName());
                        if (sortField.getOrder().equals(SortOrder.DESC)) {
                                result.add(field.desc());
                        } else {
                                result.add(field.asc());
                        }
                }
                return result.toArray(SortField[]::new);
        }
}