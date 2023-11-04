package com.yourrents.services.geodata.repository;

import static com.yourrents.services.geodata.jooq.Tables.CONTINENT;
import static com.yourrents.services.geodata.jooq.Tables.COUNTRY;
import static org.jooq.Records.mapping;

import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.util.JooqUtils;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Select;
import org.jooq.SelectOnConditionStep;
import org.jooq.SortField;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class CountryRepository {

	private final DSLContext dsl;

	CountryRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	public Page<Country> find(Pageable pageable) {
		Select<?> result = JooqUtils.paginate(
				dsl,
				getSelectedCountries(),
				getCountriesSortFields(pageable),
				pageable.getPageSize(), pageable.getOffset());

		List<Country> countries = result.fetch(r -> new Country(
				r.get("uuid", UUID.class),
				r.get("isoCode", String.class),
				r.get("englishFullName", String.class),
				r.get("iso3", String.class),
				r.get("localName", String.class),
				r.get("number", Integer.class),
				r.get("continentUuid", UUID.class)));
		int totalRows = Objects.requireNonNullElse(
				result.fetchAny("total_rows", Integer.class), 0);
		return new PageImpl<>(countries, pageable, totalRows);
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

	private SelectOnConditionStep<Record7<UUID, String, String, String, String, Integer, UUID>> getSelectedCountries() {
		return dsl.select(
						COUNTRY.EXTERNAL_ID.as("uuid"),
						COUNTRY.ISO_CODE.as("isoCode"),
						COUNTRY.ENGLISH_FULL_NAME.as("englishFullName"),
						COUNTRY.ISO_3.as("iso3"),
						COUNTRY.LOCAL_NAME.as("localName"),
						COUNTRY.NUMBER.as("number"),
						CONTINENT.EXTERNAL_ID.as("continentUuid"))
				.from(COUNTRY).leftJoin(CONTINENT)
				.on(COUNTRY.CONTINENT_ID.eq(CONTINENT.ID));
	}

	private SortField<?>[] getCountriesSortFields(Pageable pageable) {
		List<String> allowedSortFields = List.of("number");
		return pageable.getSort()
				.filter(sort -> allowedSortFields.contains(sort.getProperty()))
				.map(sort -> {
					Field<?> field = switch (sort.getProperty()) {
						case "number" -> COUNTRY.NUMBER;
						default -> throw new IllegalArgumentException(
								"Unexpected value for sort property: " + sort.getProperty());

					};
					if (sort.isAscending()) {
						return field.asc();
					} else {
						return field.desc();
					}
				}).stream().toArray(SortField[]::new);
	}

}
