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

import static com.yourrents.services.geodata.jooq.Tables.PROVINCE;
import static com.yourrents.services.geodata.jooq.Tables.PROVINCE_LOCAL_DATA;
import static com.yourrents.services.geodata.jooq.Tables.REGION;
import static com.yourrents.services.geodata.jooq.tables.City.CITY;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.jooq.tables.records.ProvinceLocalDataRecord;
import com.yourrents.services.geodata.jooq.tables.records.ProvinceRecord;
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.model.ProvinceLocalData;
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
public class ProvinceRepository {

	private final DSLContext dsl;
	private final JooqUtils jooqUtils;

	ProvinceRepository(DSLContext dsl, JooqUtils jooqUtils) {
		this.dsl = dsl;
		this.jooqUtils = jooqUtils;
	}

	public Page<Province> find(Searchable filter, Pageable pageable) {
		Select<?> result = jooqUtils.paginate(
				dsl,
				jooqUtils.getQueryWithConditionsAndSorts(getSelectProvinceSpec(),
						filter, this::getSupportedField,
						pageable, this::getSupportedField),
				pageable.getPageSize(), pageable.getOffset());
		List<Province> provinces = result.fetch(r ->
				new Province(
						r.get("uuid", UUID.class),
						r.get("name", String.class),
						r.get("localData", ProvinceLocalData.class),
						r.get("region", Province.Region.class))
		);
		int totalRows = Objects.requireNonNullElse(
				result.fetchAny("total_rows", Integer.class), 0);
		return new PageImpl<>(provinces, pageable, totalRows);
	}

	public Optional<Province> findById(Integer id) {
		return getSelectProvinceSpec()
				.where(PROVINCE.ID.eq(id))
				.fetchOptional()
				.map(mapping(Province::new));
	}

	public Optional<Province> findByExternalId(UUID externalId) {
		return getSelectProvinceSpec()
				.where(PROVINCE.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Province::new));
	}

	/**
	 * Create a new Province
	 *
	 * @return the new created province
	 */
	@Transactional(readOnly = false)
	public Province create(Province province) {
		Integer regionId = null;
		if (province.region() != null) {
			regionId = dsl.select(REGION.ID)
					.from(REGION)
					.where(REGION.EXTERNAL_ID.eq(province.region().uuid()))
					.fetchOptional(REGION.ID).orElseThrow(
							() -> new DataNotFoundException("Region not found: "
									+ province.region().uuid()));
		}
		ProvinceRecord newProvince = dsl.newRecord(PROVINCE);
		newProvince.setName(province.name());
		newProvince.setRegionId(regionId);
		newProvince.insert();
		if (province.localData() != null) {
			ProvinceLocalDataRecord newLocalData = dsl.newRecord(PROVINCE_LOCAL_DATA);
			newLocalData.setId(newProvince.getId());
			newLocalData.setItCodiceIstat(province.localData().itCodiceIstat());
			newLocalData.setItSigla(province.localData().itSigla());
			newLocalData.insert();
		}
		return findById(newProvince.getId()).orElseThrow();
	}


	/**
	 * Delete a province only if there are no referenced cities associated with it.
	 *
	 * @return true if the province has been deleted, false otherwise
	 * @throws DataNotFoundException if the province does not exist
	 * @throws IllegalArgumentException if there is at least one city associated to it
	 */
	@Transactional(readOnly = false)
	public boolean delete(UUID uuid) {
		Integer provinceId = dsl.select(PROVINCE.ID)
				.from(PROVINCE)
				.where(PROVINCE.EXTERNAL_ID.eq(uuid))
				.fetchOptional(PROVINCE.ID).orElseThrow(
						() -> new DataNotFoundException("Province not found: " + uuid));
		boolean citiesExist = dsl.fetchExists(CITY, CITY.PROVINCE_ID.eq(provinceId));
		if (citiesExist) {
			throw new DataConflictException(
					"Unable to delete the province with UUID: " + uuid
							+ " because it is referenced by at least one city");
		}
		dsl.delete(PROVINCE_LOCAL_DATA)
				.where(PROVINCE_LOCAL_DATA.ID.eq(provinceId))
				.execute();
		return dsl.deleteFrom(PROVINCE)
				.where(PROVINCE.ID.eq(provinceId))
				.execute() > 0;
	}


	/**
	 * Update a province.
	 * <p>
	 * You can update the name, the region and the local data. You can't update the province uuid.
	 * You can't update the region data, you can only change the region.
	 * <p>
	 * Only not null fields are used to update the province.
	 *
	 * @param uuid     the uuid of the province to be updated.
	 * @param province the data of province to be updated.
	 * @return the updated province
	 * @throws DataNotFoundException if the province does not exist
	 */
	@Transactional(readOnly = false)
	public Province update(UUID uuid, Province province) {
		ProvinceRecord dbProvince = dsl.selectFrom(PROVINCE)
				.where(PROVINCE.EXTERNAL_ID.eq(uuid))
				.fetchOptional().orElseThrow(
						() -> new DataNotFoundException("Province not found: " + uuid));
		if (province.name() != null) {
			dbProvince.setName(province.name());
		}
		if (province.region() != null) {
			Integer regionId = dsl.select(REGION.ID)
					.from(REGION)
					.where(REGION.EXTERNAL_ID.eq(province.region().uuid()))
					.fetchOptional(REGION.ID).orElseThrow(
							() -> new IllegalArgumentException("Region not found: "
									+ province.region().uuid()));
			dbProvince.setRegionId(regionId);
		}
		dbProvince.update();
		if (province.localData() != null) {
			ProvinceLocalDataRecord localData =
					dsl.newRecord(PROVINCE_LOCAL_DATA);
			localData.setId(dbProvince.getId());
			localData.setItCodiceIstat(province.localData().itCodiceIstat());
			localData.setItSigla(province.localData().itSigla());
			localData.merge();
		}
		return findById(dbProvince.getId())
				.orElseThrow(() -> new RuntimeException("failed to update province: " + uuid));
	}


	private SelectOnConditionStep<Record4<UUID, String, ProvinceLocalData, Province.Region>> getSelectProvinceSpec() {
		return dsl.select(
						PROVINCE.EXTERNAL_ID.as("uuid"),
						PROVINCE.NAME.as("name"),
						row(PROVINCE_LOCAL_DATA.IT_CODICE_ISTAT, PROVINCE_LOCAL_DATA.IT_SIGLA)
								.mapping(nullOnAllNull(ProvinceLocalData::new)).as("localData"),
						row(REGION.EXTERNAL_ID, REGION.NAME)
								.mapping(nullOnAllNull(Province.Region::new)).as("region"))
				.from(PROVINCE)
				.leftJoin(PROVINCE_LOCAL_DATA).on(PROVINCE.ID.eq(PROVINCE_LOCAL_DATA.ID))
				.leftJoin(REGION).on(PROVINCE.REGION_ID.eq(REGION.ID));
	}

	private Field<?> getSupportedField(String field) {
		return switch (field) {
			case "name" -> PROVINCE.NAME;
			case "uuid" -> PROVINCE.EXTERNAL_ID.cast(String.class);
			case "region.name" -> PROVINCE.region().NAME.cast(String.class);
			default -> throw new IllegalArgumentException(
					"Unexpected value for filter/sort field: " + field);
		};
	}

}
