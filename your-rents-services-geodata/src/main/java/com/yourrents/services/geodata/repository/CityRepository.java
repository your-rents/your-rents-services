package com.yourrents.services.geodata.repository;

import static org.jooq.impl.DSL.*;
import static com.yourrents.services.geodata.jooq.Tables.*;
import static org.jooq.Records.mapping;

import java.util.Optional;
import java.util.UUID;

import org.jooq.DSLContext;
import org.jooq.Record4;
import org.jooq.SelectOnConditionStep;
import org.springframework.stereotype.Repository;

import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.CityLocalData;
import com.yourrents.services.geodata.model.Province;

@Repository
public class CityRepository {
    private final DSLContext dsl;

    public CityRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public Iterable<City> findAll() {
        return getSelectCitySpec()
                .orderBy(CITY.NAME.asc()).fetch(mapping(City::new));
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
                CITY.EXTERNAL_ID, CITY.NAME,
                row(CITY_LOCAL_DATA.IT_CODICE_ISTAT, CITY_LOCAL_DATA.IT_CODICE_ERARIALE)
                        .mapping(CityLocalData::new).as("localData"),
                row(CITY.province().EXTERNAL_ID, CITY.province().NAME)
                        .mapping(Province::new).as("province"))
                .from(CITY).join(CITY_LOCAL_DATA).on(CITY.ID.eq(CITY_LOCAL_DATA.ID));
    }    
}