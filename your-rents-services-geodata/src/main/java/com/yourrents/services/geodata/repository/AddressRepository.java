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

import static com.yourrents.services.geodata.jooq.Tables.ADDRESS;
import static org.jooq.Functions.nullOnAllNull;
import static org.jooq.impl.DSL.row;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.common.util.jooq.JooqUtils;
import com.yourrents.services.geodata.jooq.tables.records.AddressRecord;
import com.yourrents.services.geodata.mapper.AddressMapper;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.model.Address.AddressCity;
import com.yourrents.services.geodata.model.Address.AddressCountry;
import com.yourrents.services.geodata.model.Address.AddressProvince;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.model.Province;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record7;
import org.jooq.Select;
import org.jooq.SelectJoinStep;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public class AddressRepository {

  private final DSLContext dsl;
  private final JooqUtils jooqUtils;
  private final AddressMapper addressMapper;
  private final CityRepository cityRepository;
  private final ProvinceRepository provinceRepository;
  private final CountryRepository countryRepository;

  public AddressRepository(DSLContext dsl, JooqUtils jooqUtils, AddressMapper addressMapper,
      CityRepository cityRepository, ProvinceRepository provinceRepository,
      CountryRepository countryRepository) {
    this.dsl = dsl;
    this.jooqUtils = jooqUtils;
    this.addressMapper = addressMapper;
    this.cityRepository = cityRepository;
    this.provinceRepository = provinceRepository;
    this.countryRepository = countryRepository;
  }

  public Page<Address> find(Searchable filter, Pageable pageable) {
    Select<?> result = jooqUtils.paginate(
        dsl,
        jooqUtils.getQueryWithConditionsAndSorts(getSelectAddressSpec(),
            filter, this::getSupportedField,
            pageable, this::getSupportedField),
        pageable.getPageSize(), pageable.getOffset());

    List<Address> addresses = result.fetch(addressMapper::map);
    int totalRows = Objects.requireNonNullElse(
        result.fetchAny("total_rows", Integer.class), 0);
    return new PageImpl<>(addresses, pageable, totalRows);
  }

  public Optional<Address> findById(Integer id) {
    return getSelectAddressSpec()
        .where(ADDRESS.ID.eq(id))
        .fetchOptional()
        .map(addressMapper::map);
  }

  public Optional<Address> findByExternalId(UUID externalId) {
    return getSelectAddressSpec()
        .where(ADDRESS.EXTERNAL_ID.eq(externalId))
        .fetchOptional()
        .map(addressMapper::map);
  }

  /**
   * Create a new Address.
   *
   * @return the new created Address
   * @throws DataNotFoundException if any linked city, province or country cannot be found.
   */
  @Transactional(readOnly = false)
  public Address create(Address address) {
    AddressRecord newAddress = dsl.newRecord(ADDRESS);
    updateAddressRecord(newAddress, address);
    newAddress.insert();
    return findById(newAddress.getId()).orElseThrow();
  }

  /**
   * Delete an address
   *
   * @return true if the address has been deleted, false otherwise
   * @throws DataNotFoundException if the address does not exist
   */
  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer addressId = dsl.select(ADDRESS.ID)
        .from(ADDRESS)
        .where(ADDRESS.EXTERNAL_ID.eq(uuid))
        .fetchOptional(ADDRESS.ID).orElseThrow(
            () -> new DataNotFoundException("Address not found: " + uuid));
    return dsl.deleteFrom(ADDRESS)
        .where(ADDRESS.ID.eq(addressId))
        .execute() > 0;
  }

  /**
   * Update an existing Address.
   *
   * @return the updated Address
   * @throws DataNotFoundException if any linked city, province or country cannot be found.
   */
  @Transactional(readOnly = false)
  public Address update(UUID uuid, Address address) {
    AddressRecord dbAddress = dsl.selectFrom(ADDRESS)
        .where(ADDRESS.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Record not found: " + uuid));
    updateAddressRecord(dbAddress, address);
    dbAddress.update();
    return findById(dbAddress.getId())
        .orElseThrow(() -> new RuntimeException("failed to update address: " + uuid));
  }

  private void updateAddressRecord(AddressRecord addressRecord, Address address) {
    if (address.addressLine1() != null) {
      addressRecord.setAddressLine_1(address.addressLine1());
    }
    if (address.addressLine2() != null) {
      addressRecord.setAddressLine_2(address.addressLine2());
    }
    if (address.postalCode() != null) {
      addressRecord.setPostalCode(address.postalCode());
    }
    if (address.city() != null) {
      if (address.city().uuid() != null) {
        City city = cityRepository.findByExternalId(address.city().uuid()).orElseThrow(
            () -> new DataNotFoundException("City not found: "
                + address.city().uuid()));
        addressRecord.setCityExternalId(city.uuid());
        addressRecord.setCity(city.name());
      } else {
        addressRecord.setCity(address.city().name());
        addressRecord.setCityExternalId(null);
      }
    }
    if (address.province() != null) {
      if (address.province().uuid() != null) {
        Province province = provinceRepository.findByExternalId(address.province().uuid())
            .orElseThrow(
                () -> new DataNotFoundException("Province not found: "
                    + address.province().uuid()));
        addressRecord.setProvinceExternalId(province.uuid());
        addressRecord.setProvince(province.name());
      } else {
        addressRecord.setProvince(address.province().name());
        addressRecord.setProvinceExternalId(null);
      }
    }
    if (address.country() != null) {
      if (address.country().uuid() != null) {
        Country country = countryRepository.findByExternalId(address.country().uuid()).orElseThrow(
            () -> new DataNotFoundException("Country not found: "
                + address.country().uuid()));
        addressRecord.setCountryExternalId(country.uuid());
        addressRecord.setCountry(country.localName());
      } else {
        addressRecord.setCountry(address.country().localName());
        addressRecord.setCountryExternalId(null);
      }
    }
  }

  private SelectJoinStep<Record7<UUID, String, String, String, AddressCity, AddressProvince, AddressCountry>> getSelectAddressSpec() {
    return dsl.select(
            ADDRESS.EXTERNAL_ID.as("uuid"),
            ADDRESS.ADDRESS_LINE_1.as("addressLine1"),
            ADDRESS.ADDRESS_LINE_2.as("addressLine2"),
            ADDRESS.POSTAL_CODE.as("postalCode"),
            row(ADDRESS.CITY_EXTERNAL_ID, ADDRESS.CITY)
                .mapping(nullOnAllNull(AddressCity::new)).as("city"),
            row(ADDRESS.PROVINCE_EXTERNAL_ID, ADDRESS.PROVINCE)
                .mapping(nullOnAllNull(Address.AddressProvince::new)).as("province"),
            row(ADDRESS.COUNTRY_EXTERNAL_ID, ADDRESS.COUNTRY)
                .mapping(nullOnAllNull(Address.AddressCountry::new)).as("country"))
        .from(ADDRESS);
  }

  private Field<?> getSupportedField(String field) {
    return switch (field) {
      case "uuid" -> ADDRESS.EXTERNAL_ID;
      case "addressLine1" -> ADDRESS.ADDRESS_LINE_1;
      case "addressLine2" -> ADDRESS.ADDRESS_LINE_2;
      case "postalCode" -> ADDRESS.POSTAL_CODE;
      case "city.name" -> ADDRESS.CITY;
      case "city.uuid" -> ADDRESS.CITY_EXTERNAL_ID;
      case "province.name" -> ADDRESS.PROVINCE;
      case "province.uuid" -> ADDRESS.PROVINCE_EXTERNAL_ID;
      case "country.localName" -> ADDRESS.COUNTRY;
      case "country.uuid" -> ADDRESS.COUNTRY_EXTERNAL_ID;
      default -> throw new IllegalArgumentException(
          "Unexpected value for filter/sort field: " + field);
    };
  }

}
