package com.yourrents.services.geodata.repository;

import static com.yourrents.services.geodata.jooq.Tables.CONTINENT;
import static org.jooq.Records.mapping;

import com.yourrents.services.geodata.model.Continent;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

@Repository
public class ContinentRepository {

	private final DSLContext dsl;

	ContinentRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	public Optional<Continent> findById(Integer id) {
		return getSelectedCountries()
				.where(CONTINENT.ID.eq(id))
				.fetchOptional()
				.map(mapping(Continent::new));
	}

	public Optional<Continent> findByExternalId(UUID externalId) {
		return getSelectedCountries()
				.where(CONTINENT.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Continent::new));
	}

	private SelectJoinStep<Record3<String, String, UUID>> getSelectedCountries() {
		return dsl.select(
						CONTINENT.CODE.as("code"),
						CONTINENT.NAME.as("name"),
						CONTINENT.EXTERNAL_ID.as("uuid"))
				.from(CONTINENT);
	}

}
