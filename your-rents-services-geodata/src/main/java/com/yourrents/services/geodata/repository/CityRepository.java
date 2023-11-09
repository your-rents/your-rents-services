package com.yourrents.services.geodata.repository;

import static org.jooq.impl.DSL.*;
import static com.yourrents.services.geodata.jooq.Tables.*;
import static org.jooq.Records.*;
import static org.jooq.Functions.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Select;
import org.jooq.SelectOnConditionStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.CityLocalData;
import com.yourrents.services.geodata.util.JooqUtils;
import com.yourrents.services.geodata.util.search.Searchable;

@Repository
public class CityRepository {
    private final DSLContext dsl;
    private final JooqUtils jooqUtils;

    public CityRepository(DSLContext dsl, JooqUtils jooqUtils) {
        this.dsl = dsl;
        this.jooqUtils = jooqUtils;
    }

    public Page<City> find(Searchable filter, Pageable pageable) {
        Select<?> result = jooqUtils.paginate(
                dsl,
                jooqUtils.getQueryWithConditionsAndSorts(getSelectCitySpec(),
                        filter, this::getSupportedField,
                        pageable, this::getSupportedField),
                pageable.getPageSize(), pageable.getOffset());
        List<City> cities = result.fetch(r -> {
            return new City(
                    r.get("uuid", UUID.class),
                    r.get("name", String.class),
                    r.get("localData", CityLocalData.class),
                    r.get("province", City.Province.class));
        });
        int totalRows = Objects.requireNonNullElse(
                result.fetchAny("total_rows", Integer.class), 0);
        return new PageImpl<>(cities, pageable, totalRows);
    }

    public Optional<City> findById(Integer id) {
        return getSelectCitySpec()
                .where(CITY.ID.eq(id))
                .fetchOptional()
                .map(mapping(City::new));
    }

    public Optional<City> findByExternalId(UUID externalId) {
        return getSelectCitySpec()
                .where(CITY.EXTERNAL_ID.eq(externalId))
                .fetchOptional()
                .map(mapping(City::new));
    }

    private SelectOnConditionStep<Record4<UUID, String, CityLocalData, City.Province>> getSelectCitySpec() {
        return dsl.select(
                CITY.EXTERNAL_ID.as("uuid"), CITY.NAME.as("name"),
                row(CITY_LOCAL_DATA.IT_CODICE_ISTAT, CITY_LOCAL_DATA.IT_CODICE_ERARIALE)
                        .mapping(nullOnAllNull(CityLocalData::new)).as("localData"),
                row(CITY.province().EXTERNAL_ID, CITY.province().NAME)
                        .mapping(nullOnAllNull(City.Province::new)).as("province"))
                .from(CITY).leftJoin(CITY_LOCAL_DATA).on(CITY.ID.eq(CITY_LOCAL_DATA.ID));
    }

    private Field<?> getSupportedField(String field) {
        return switch (field) {
            case "name" -> CITY.NAME;
            case "uuid" -> CITY.EXTERNAL_ID.cast(String.class);
            default ->
                throw new IllegalArgumentException(
                        "Unexpected value for filter/sort field: " + field);
        };
    }

}
