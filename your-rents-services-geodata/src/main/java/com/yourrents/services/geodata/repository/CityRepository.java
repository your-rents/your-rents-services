package com.yourrents.services.geodata.repository;

import static com.yourrents.services.geodata.jooq.tables.City.*;

import java.util.List;
import java.util.UUID;

import org.jooq.DSLContext;
import org.springframework.stereotype.Repository;

import com.yourrents.services.geodata.jooq.tables.records.CityRecord;

@Repository
public class CityRepository {
    private final DSLContext dsl;

    public CityRepository(DSLContext dsl) {
        this.dsl = dsl;
    }

    public List<CityRecord> findAll() {
        return dsl.selectFrom(CITY)
            .orderBy(CITY.NAME.asc()).fetch();
    }

    public CityRecord findById(Integer id) {
        return dsl.selectFrom(CITY)
                .where(CITY.ID.eq(id)).fetchOne();
    }

    public CityRecord findByExternalId(UUID externalId) {
        return dsl.selectFrom(CITY)
                .where(CITY.EXTERNAL_ID.eq(externalId)).fetchOne();
    }
}