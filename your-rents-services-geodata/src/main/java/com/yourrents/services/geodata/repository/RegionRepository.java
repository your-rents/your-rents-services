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
import static com.yourrents.services.geodata.jooq.Tables.PROVINCE;
import static com.yourrents.services.geodata.jooq.Tables.REGION;
import static com.yourrents.services.geodata.jooq.Tables.REGION_LOCAL_DATA;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.jooq.tables.records.RegionLocalDataRecord;
import com.yourrents.services.geodata.jooq.tables.records.RegionRecord;
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
import org.springframework.transaction.annotation.Transactional;

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

	/**
	 * Create a new Region
	 * @return the new created region
	 */
	@Transactional(readOnly = false)
	public Region create(Region region) {
		Integer countryId = null;
		if (region.country() != null) {
			countryId = dsl.select(COUNTRY.ID)
					.from(COUNTRY)
					.where(COUNTRY.EXTERNAL_ID.eq(region.country().uuid()))
					.fetchOptional(COUNTRY.ID).orElseThrow(
							() -> new DataNotFoundException("Country not found: "
									+ region.country().uuid()));
		}
		RegionRecord newRegion = dsl.newRecord(REGION);
		newRegion.setName(region.name());
		newRegion.setCountryId(countryId);
		newRegion.insert();
		if (region.localData() != null) {
			RegionLocalDataRecord newLocalData = dsl.newRecord(REGION_LOCAL_DATA);
			newLocalData.setId(newRegion.getId());
			newLocalData.setItCodiceIstat(region.localData().itCodiceIstat());
			newLocalData.insert();
		}
		return findById(newRegion.getId()).orElseThrow();
	}

  /**
   * Delete a region only if there are no referenced provinces associated with it.
   *
   * @return true if the region has been deleted, false otherwise
   * @throws DataNotFoundException if the region does not exist
   * @throws DataConflictException if there is at least one province associated to it
   */
  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer regionId = dsl.select(REGION.ID)
        .from(REGION)
        .where(REGION.EXTERNAL_ID.eq(uuid))
        .fetchOptional(REGION.ID).orElseThrow(
            () -> new DataNotFoundException("Region not found: " + uuid));
    boolean provincesExist = dsl.fetchExists(PROVINCE, PROVINCE.REGION_ID.eq(regionId));
    if (provincesExist) {
      throw new DataConflictException(
          "Unable to delete the region with UUID: " + uuid
              + " because it is referenced by at least one province");
    }
    dsl.delete(REGION_LOCAL_DATA)
        .where(REGION_LOCAL_DATA.ID.eq(regionId))
        .execute();
    return dsl.deleteFrom(REGION)
        .where(REGION.ID.eq(regionId))
        .execute() > 0;
  }

  /**
   * Update a region.
   * <p>
   * You can update the name, the country and the local data. You can't update the region uuid.
   * <p>
   * Only not null fields are used to update the region.
   *
   * @param uuid the uuid of the region to be updated.
   * @param region the data of region to be updated.
   * @return the updated region
   * @throws DataNotFoundException if the region does not exist
   */
  @Transactional(readOnly = false)
  public Region update(UUID uuid, Region region) {
    RegionRecord dbRegion = dsl.selectFrom(REGION)
        .where(REGION.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Region not found: " + uuid));
    if (region.name() != null) {
      dbRegion.setName(region.name());
    }
    if (region.country() != null && region.country().uuid() != null) {
      Integer countryId = dsl.select(COUNTRY.ID)
          .from(COUNTRY)
          .where(COUNTRY.EXTERNAL_ID.eq(region.country().uuid()))
          .fetchOptional(COUNTRY.ID).orElseThrow(
              () -> new IllegalArgumentException("Country not found: "
                  + region.country().uuid()));
      dbRegion.setCountryId(countryId);
    }
    dbRegion.update();
    if (region.localData() != null) {
      RegionLocalDataRecord localData =
          dsl.newRecord(REGION_LOCAL_DATA);
      localData.setId(dbRegion.getId());
      localData.setItCodiceIstat(region.localData().itCodiceIstat());
      localData.merge();
    }
    return findById(dbRegion.getId())
        .orElseThrow(() -> new RuntimeException("failed to update region: " + uuid));
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
