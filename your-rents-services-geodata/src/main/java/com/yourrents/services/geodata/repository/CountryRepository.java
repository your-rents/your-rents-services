package com.yourrents.services.geodata.repository;

import static com.yourrents.services.geodata.jooq.Tables.COUNTRY;
import static org.jooq.Records.mapping;

import com.yourrents.services.geodata.model.Country;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record6;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepository {

	private final DSLContext dsl;

	CountryRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	public Optional<Country> findById(Integer id) {
		return getSelectedCountries()
				.where(COUNTRY.ID.eq(id))
				.fetchOptional()
				.map(mapping(Country::new));
	}

	public Optional<Country> findByExternalId(UUID externalId) {
		return getSelectedCountries()
				.where(COUNTRY.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Country::new));
	}

	private SelectJoinStep<Record6<String, String, String, String, Integer, UUID>> getSelectedCountries() {
		return dsl.select(
						COUNTRY.ISO_CODE.as("isoCode"),
						COUNTRY.ENGLISH_FULL_NAME.as("englishFullName"),
						COUNTRY.ISO_3.as("iso3"),
						COUNTRY.LOCAL_NAME.as("localName"),
						COUNTRY.NUMBER.as("number"),
						COUNTRY.EXTERNAL_ID.as("uuid"))
				.from(COUNTRY);
	}

}
