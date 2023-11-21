package com.yourrents.services.geodata.repository;

/*-
 * #%L
 * YourRents GeoData Service
 * %%
 * Copyright (C) 2023 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.yourrents.services.geodata.jooq.Tables.CONTINENT;
import static com.yourrents.services.geodata.jooq.Tables.COUNTRY;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.model.Country;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Select;
import org.jooq.SelectOnConditionStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepository {

	private final DSLContext dsl;
	private final JooqUtils jooqUtils;

	CountryRepository(DSLContext dsl, JooqUtils jooqUtils) {
		this.dsl = dsl;
		this.jooqUtils = jooqUtils;
	}

	public Page<Country> find(Searchable filter, Pageable pageable) {
		Select<?> result = jooqUtils.paginate(
				dsl,
				jooqUtils.getQueryWithConditionsAndSorts(getSelectCountrySpec(),
						filter, this::getSupportedSortField,
						pageable, this::getSupportedSortField),
				pageable.getPageSize(), pageable.getOffset());
		List<Country> countries = result.fetch(r ->
				new Country(
						r.get("uuid", UUID.class),
						r.get("isoCode", String.class),
						r.get("englishFullName", String.class),
						r.get("iso3", String.class),
						r.get("localName", String.class),
						r.get("number", Integer.class),
						r.get("continent", Country.Continent.class))
		);
		int totalRows = Objects.requireNonNullElse(
				result.fetchAny("total_rows", Integer.class), 0);
		return new PageImpl<>(countries, pageable, totalRows);
	}


	public Optional<Country> findById(Integer id) {
		return getSelectCountrySpec()
				.where(COUNTRY.ID.eq(id))
				.fetchOptional()
				.map(mapping(Country::new));
	}

	public Optional<Country> findByExternalId(UUID externalId) {
		return getSelectCountrySpec()
				.where(COUNTRY.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Country::new));
	}

	private SelectOnConditionStep<Record7<UUID, String, String, String, String, Integer, Country.Continent>> getSelectCountrySpec() {
		return dsl.select(
						COUNTRY.EXTERNAL_ID.as("uuid"),
						COUNTRY.ISO_CODE.as("isoCode"),
						COUNTRY.ENGLISH_FULL_NAME.as("englishFullName"),
						COUNTRY.ISO_3.as("iso3"),
						COUNTRY.LOCAL_NAME.as("localName"),
						COUNTRY.NUMBER.as("number"),
						row(CONTINENT.EXTERNAL_ID, CONTINENT.NAME)
								.mapping(nullOnAllNull(Country.Continent::new)).as("continent"))
				.from(COUNTRY).leftJoin(CONTINENT)
				.on(COUNTRY.CONTINENT_ID.eq(CONTINENT.ID));
	}

    private Field<?> getSupportedSortField(String field) {
        return switch (field) {
			case "uuid" -> COUNTRY.EXTERNAL_ID.cast(String.class);
			case "isoCode" -> COUNTRY.ISO_CODE;
			case "englishFullName" -> COUNTRY.ENGLISH_FULL_NAME;
			case "iso3" -> COUNTRY.ISO_3;
			case "localName" -> COUNTRY.LOCAL_NAME;
			case "number" -> COUNTRY.NUMBER.cast(String.class);
            default ->
                throw new IllegalArgumentException(
                        "Unexpected value for filter/sort field: " + field);
        };
    }

}
