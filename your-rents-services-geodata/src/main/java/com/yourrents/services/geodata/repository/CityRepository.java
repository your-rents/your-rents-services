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
import org.springframework.transaction.annotation.Transactional;

import com.yourrents.services.geodata.exception.DataNotFoundException;
import com.yourrents.services.geodata.jooq.tables.records.CityLocalDataRecord;
import com.yourrents.services.geodata.jooq.tables.records.CityRecord;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.CityLocalData;
import com.yourrents.services.geodata.util.JooqUtils;
import com.yourrents.services.geodata.util.search.Searchable;

@Repository
@Transactional(readOnly = true)
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

        /**
         * Create a new city
         * 
         * @return the created city
         */
        @Transactional(readOnly = false)
        public City create(City city) {
                Integer provinceId = null;
                if (city.province() != null) {
                        provinceId = dsl.select(PROVINCE.ID)
                                        .from(PROVINCE)
                                        .where(PROVINCE.EXTERNAL_ID.eq(city.province().uuid()))
                                        .fetchOptional(PROVINCE.ID).orElseThrow(
                                                        () -> new IllegalArgumentException("Province not found: "
                                                                        + city.province().uuid()));
                }
                Integer cityId = dsl.insertInto(CITY)
                                .set(CITY.NAME, city.name())
                                .set(CITY.PROVINCE_ID, provinceId)
                                .returning(CITY.ID)
                                .fetchOne()
                                .getId();
                if (city.localData() != null) {
                        dsl.insertInto(CITY_LOCAL_DATA)
                                        .set(CITY_LOCAL_DATA.ID, cityId)
                                        .set(CITY_LOCAL_DATA.IT_CODICE_ISTAT, city.localData().itCodiceIstat())
                                        .set(CITY_LOCAL_DATA.IT_CODICE_ERARIALE, city.localData().itCodiceErariale())
                                        .execute();
                }
                return findById(cityId).orElseThrow();
        }

        /**
         * Delete a city
         * 
         * @return true if the city has been deleted, false otherwise
         * @throws DataNotFoundException if the city does not exist
         */
        @Transactional(readOnly = false)
        public boolean delete(UUID uuid) {
                Integer cityId = dsl.select(CITY.ID)
                                .from(CITY)
                                .where(CITY.EXTERNAL_ID.eq(uuid))
                                .fetchOptional(CITY.ID).orElseThrow(
                                                () -> new DataNotFoundException("City not found: " + uuid));
                dsl.delete(CITY_LOCAL_DATA)
                                .where(CITY_LOCAL_DATA.ID.eq(cityId))
                                .execute();
                return dsl.deleteFrom(CITY)
                                .where(CITY.ID.eq(cityId))
                                .execute() > 0;
        }

        /**
         * Update a city.
         * 
         * You can update the name, the province and the local data.
         * You can't update the city uuid.
         * You can't update the province data, you can only change the province.
         * 
         * Only not null fields are used to update the city.
         * 
         * @param uuid the uuid of the city to update
         * @param city the data of city to update.
         * @return the updated city
         * @throws DataNotFoundException if the city does not exist
         */
        @Transactional(readOnly = false)
        public City update(UUID uuid, City city) {
                CityRecord dbCity = dsl.selectFrom(CITY)
                                .where(CITY.EXTERNAL_ID.eq(uuid))
                                .fetchOptional().orElseThrow(
                                                () -> new DataNotFoundException("City not found: " + uuid));
                if (city.name() != null) {
                        dbCity.setName(city.name());
                }
                if (city.province() != null) {
                        Integer provinceId = dsl.select(PROVINCE.ID)
                                        .from(PROVINCE)
                                        .where(PROVINCE.EXTERNAL_ID.eq(city.province().uuid()))
                                        .fetchOptional(PROVINCE.ID).orElseThrow(
                                                        () -> new IllegalArgumentException("Province not found: "
                                                                        + city.province().uuid()));
                        dbCity.setProvinceId(provinceId);
                }
                dbCity.update();
                if (city.localData() != null) {
                        CityLocalDataRecord localData = dsl.newRecord(CITY_LOCAL_DATA);
                        localData.setId(dbCity.getId());
                        localData.setItCodiceIstat(city.localData().itCodiceIstat());
                        localData.setItCodiceErariale(city.localData().itCodiceErariale());
                        localData.merge();
                }
                return findById(dbCity.getId()).orElseThrow();
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
                        case "province.name" -> CITY.province().NAME;
                        case "province.uuid" -> CITY.province().EXTERNAL_ID.cast(String.class);
                        default ->
                                throw new IllegalArgumentException(
                                                "Unexpected value for filter/sort field: " + field);
                };
        }

}
