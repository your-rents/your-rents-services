package com.yourrents.services.geodata.repository;

import static org.jooq.impl.DSL.*;
import static com.yourrents.services.geodata.jooq.Tables.*;
import static org.jooq.Records.mapping;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record4;
import org.jooq.Select;
import org.jooq.SelectOnConditionStep;
import org.jooq.SortField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.CityLocalData;
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.util.JooqUtils;

@Repository
public class CityRepository {
    private final DSLContext dsl;

    public CityRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Page<City> find(Pageable pageable) {
        Select<?> result = JooqUtils.paginate(
                dsl,
                getSelectCitySpec(),
                getCitySortFields(pageable),
                pageable.getPageSize(), pageable.getOffset());
        List<City> cities = result.fetch(r -> {
            return new City(
                    r.get("uuid", UUID.class),
                    r.get("name", String.class),
                    r.get("localData", CityLocalData.class),
                    r.get("province", Province.class));
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

    private SelectOnConditionStep<Record4<UUID, String, CityLocalData, Province>> getSelectCitySpec() {
        return dsl.select(
                CITY.EXTERNAL_ID.as("uuid"), CITY.NAME.as("name"),
                row(CITY_LOCAL_DATA.IT_CODICE_ISTAT, CITY_LOCAL_DATA.IT_CODICE_ERARIALE)
                        .mapping(CityLocalData::new).as("localData"),
                row(CITY.province().EXTERNAL_ID, CITY.province().NAME)
                        .mapping(Province::new).as("province"))
                .from(CITY).join(CITY_LOCAL_DATA).on(CITY.ID.eq(CITY_LOCAL_DATA.ID));
    }

    private SortField<?>[] getCitySortFields(Pageable pageable) {
        List<String> allowedSortFields = List.of("name");
        return pageable.getSort()
                .filter(sort -> allowedSortFields.contains(sort.getProperty()))
                .map(sort -> {
                    Field<?> field = switch (sort.getProperty()) {
                        case "name" -> CITY.NAME;
                        default ->
                            throw new IllegalArgumentException(
                                    "Unexpected value for sort property: " + sort.getProperty());

                    };
                    if (sort.isAscending()) {
                        return field.asc();
                    } else {
                        return field.desc();
                    }
                }).stream().toArray(SortField[]::new);
    }

}
