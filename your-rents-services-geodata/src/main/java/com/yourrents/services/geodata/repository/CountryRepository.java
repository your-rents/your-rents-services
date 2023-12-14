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
import static com.yourrents.services.geodata.jooq.Tables.REGION;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.jooq.tables.records.CountryRecord;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.model.GeoReference;

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
import org.springframework.transaction.annotation.Transactional;

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
            r.get("continent", GeoReference.class))
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

  /**
   * Create a new Country
   * @return the new created country
   */
  @Transactional(readOnly = false)
  public Country create(Country country) {
    Integer continentId = null;
    if (country.continent() != null && country.continent().uuid() != null) {
      continentId = dsl.select(CONTINENT.ID)
          .from(CONTINENT)
          .where(CONTINENT.EXTERNAL_ID.eq(country.continent().uuid()))
          .fetchOptional(CONTINENT.ID).orElseThrow(
              () -> new DataNotFoundException("Continent not found: "
                  + country.continent().uuid()));
    }
    CountryRecord newCountry = dsl.newRecord(COUNTRY);
    newCountry.setIsoCode(country.isoCode());
    newCountry.setEnglishFullName(country.englishFullName());
    newCountry.setIso_3(country.iso3());
    newCountry.setLocalName(country.localName());
    newCountry.setNumber(country.number());
    newCountry.setContinentId(continentId);
    newCountry.insert();
    return findById(newCountry.getId()).orElseThrow();
  }

  /**
   * Delete a country only if there are no regions associated with it.
   *
   * @return true if the country has been deleted, false otherwise
   * @throws DataNotFoundException if the country does not exist
   * @throws DataConflictException if there is at least one region associated to it
   */
  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer countryId = dsl.select(COUNTRY.ID)
        .from(COUNTRY)
        .where(COUNTRY.EXTERNAL_ID.eq(uuid))
        .fetchOptional(COUNTRY.ID).orElseThrow(
            () -> new DataNotFoundException("Country not found: " + uuid));
    boolean regionExists = dsl.fetchExists(REGION, REGION.COUNTRY_ID.eq(countryId));
    if (regionExists) {
      throw new DataConflictException(
          "Unable to delete the country with UUID: " + uuid
              + " because it is referenced by at least one region");
    }
    return dsl.deleteFrom(COUNTRY)
        .where(COUNTRY.ID.eq(countryId))
        .execute() > 0;
  }


  /**
   * Update a country.
   * <p>
   * You can update the name, the continent. You can't update the country uuid.
   * <p>
   * Only not null fields are used to update the country.
   *
   * @param uuid the uuid of the country to be updated.
   * @param country the data of country to be updated.
   * @return the updated country
   * @throws DataNotFoundException if the country does not exist
   */
  @Transactional(readOnly = false)
  public Country update(UUID uuid, Country country) {
    CountryRecord dbCountry = dsl.selectFrom(COUNTRY)
        .where(COUNTRY.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Country not found: " + uuid));
    if (country.isoCode() != null) {
      dbCountry.setIsoCode(country.isoCode());
    }
    if (country.englishFullName() != null) {
      dbCountry.setEnglishFullName(country.englishFullName());
    }
    if (country.iso3() != null) {
      dbCountry.setIso_3(country.iso3());
    }
    if (country.localName() != null) {
      dbCountry.setLocalName(country.localName());
    }
    if (country.number() != null) {
      dbCountry.setNumber(country.number());
    }

    if (country.continent() != null && country.continent().uuid() != null) {
      Integer continentId = dsl.select(CONTINENT.ID)
          .from(CONTINENT)
          .where(CONTINENT.EXTERNAL_ID.eq(country.continent().uuid()))
          .fetchOptional(CONTINENT.ID).orElseThrow(
              () -> new IllegalArgumentException("Continent not found: "
                  + country.continent().uuid()));
      dbCountry.setContinentId(continentId);
    }
    dbCountry.update();
    return findById(dbCountry.getId())
        .orElseThrow(() -> new RuntimeException("failed to update country: " + uuid));
  }

  private SelectOnConditionStep<Record7<UUID, String, String, String, String, Integer, GeoReference>> getSelectCountrySpec() {
		return dsl.select(
						COUNTRY.EXTERNAL_ID.as("uuid"),
						COUNTRY.ISO_CODE.as("isoCode"),
						COUNTRY.ENGLISH_FULL_NAME.as("englishFullName"),
						COUNTRY.ISO_3.as("iso3"),
						COUNTRY.LOCAL_NAME.as("localName"),
						COUNTRY.NUMBER.as("number"),
						row(CONTINENT.EXTERNAL_ID, CONTINENT.NAME)
                .mapping(nullOnAllNull(GeoReference::new)).as("continent"))
				.from(COUNTRY).leftJoin(CONTINENT)
				.on(COUNTRY.CONTINENT_ID.eq(CONTINENT.ID));
	}

    private Field<?> getSupportedSortField(String field) {
        return switch (field) {
			case "uuid" -> COUNTRY.EXTERNAL_ID;
			case "isoCode" -> COUNTRY.ISO_CODE;
			case "englishFullName" -> COUNTRY.ENGLISH_FULL_NAME;
			case "iso3" -> COUNTRY.ISO_3;
			case "localName" -> COUNTRY.LOCAL_NAME;
			case "number" -> COUNTRY.NUMBER;
      case "continent.name" -> COUNTRY.continent().NAME;
            default ->
                throw new IllegalArgumentException(
                        "Unexpected value for filter/sort field: " + field);
        };
    }

}
