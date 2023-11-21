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

import static com.yourrents.services.geodata.jooq.Tables.COUNTRY;
import static com.yourrents.services.geodata.jooq.Tables.REGION;
import static com.yourrents.services.geodata.jooq.Tables.REGION_LOCAL_DATA;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.model.Region;
import com.yourrents.services.geodata.model.RegionLocalData;
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

@Repository
public class RegionRepository {

	private final DSLContext dsl;
	private final JooqUtils jooqUtils;

	RegionRepository(DSLContext dsl, JooqUtils jooqUtils) {
		this.dsl = dsl;
		this.jooqUtils = jooqUtils;
	}

	public Page<Region> find(Searchable filter, Pageable pageable) {
		Select<?> result = jooqUtils.paginate(
				dsl,
				jooqUtils.getQueryWithConditionsAndSorts(getSelectRegionSpec(),
						filter, this::getSupportedField,
						pageable, this::getSupportedField),
				pageable.getPageSize(), pageable.getOffset());
		List<Region> provinces = result.fetch(r ->
				new Region(
						r.get("uuid", UUID.class),
						r.get("name", String.class),
						r.get("localData", RegionLocalData.class),
						r.get("country", Region.Country.class))
		);
		int totalRows = Objects.requireNonNullElse(
				result.fetchAny("total_rows", Integer.class), 0);
		return new PageImpl<>(provinces, pageable, totalRows);
	}

	public Optional<Region> findById(Integer id) {
		return getSelectRegionSpec()
				.where(REGION.ID.eq(id))
				.fetchOptional()
				.map(mapping(Region::new));
	}

	public Optional<Region> findByExternalId(UUID externalId) {
		return getSelectRegionSpec()
				.where(REGION.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Region::new));
	}

	private SelectOnConditionStep<Record4<UUID, String, RegionLocalData, Region.Country>> getSelectRegionSpec() {
		return dsl.select(
						REGION.EXTERNAL_ID.as("uuid"),
						REGION.NAME.as("name"),
						row(REGION_LOCAL_DATA.IT_CODICE_ISTAT)
								.mapping(nullOnAllNull(RegionLocalData::new)).as("localData"),
						row(COUNTRY.EXTERNAL_ID, COUNTRY.LOCAL_NAME)
								.mapping(nullOnAllNull(Region.Country::new)).as("country"))
				.from(REGION)
				.leftJoin(REGION_LOCAL_DATA).on(REGION.ID.eq(REGION_LOCAL_DATA.ID))
				.leftJoin(COUNTRY).on(REGION.COUNTRY_ID.eq(COUNTRY.ID));
	}

	private Field<?> getSupportedField(String field) {
		return switch (field) {
			case "name" -> REGION.NAME;
			case "uuid" -> REGION.EXTERNAL_ID.cast(String.class);
			case "country.localName" -> REGION.country().LOCAL_NAME.cast(String.class);
			default -> throw new IllegalArgumentException(
					"Unexpected value for filter/sort field: " + field);
		};
	}

}
