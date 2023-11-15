package com.yourrents.services.geodata.repository;

import static com.yourrents.services.geodata.jooq.Tables.PROVINCE;
import static com.yourrents.services.geodata.jooq.Tables.PROVINCE_LOCAL_DATA;
import static com.yourrents.services.geodata.jooq.Tables.REGION;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.Records.mapping;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.jooq.JooqUtils;
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
