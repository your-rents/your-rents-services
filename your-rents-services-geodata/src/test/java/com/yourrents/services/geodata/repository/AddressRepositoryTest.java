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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Address;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@Transactional
class AddressRepositoryTest {

  static final int NUM_ADDRESSES_TEST = 4;


  @Autowired
  AddressRepository addressRepository;

  @Test
  void findAll() {
    Page<Address> result = addressRepository.find(FilterCriteria.of(),
        PageRequest.ofSize(Integer.MAX_VALUE));
    assertThat(result, iterableWithSize(NUM_ADDRESSES_TEST));
  }

  @Test
  void findByExternalId() {
    Address address = addressRepository.findByExternalId(
        UUID.fromString("00000000-0000-0000-0000-000000000001")).orElseThrow();
    assertThat(address, notNullValue());
    assertThat(address.city().name(), equalTo("Paddington"));
    assertThat(address.country().localName(), equalTo("United Kingdom"));
  }

  @Test
  void findFilteredByCountryLocalName() {
    //given
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("city.name")));
    FilterCriteria filter = FilterCriteria.of(
        FilterCondition.of("country.localName", "eq", "Italy"));
    //when
    Page<Address> result = addressRepository.find(filter, pageable);
    //then
    assertThat(result, iterableWithSize(3));
    assertThat(result.getContent().get(0).city().name(), equalTo("Roma"));
    assertThat(result.getContent().get(1).city().name(), equalTo("Fiumicino"));
    assertThat(result.getContent().get(2).city().name(), equalTo("Bresso"));
  }

  @Test
  void findFilteredByProvinceName() {
    //given
    Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.asc("city.name")));
    FilterCriteria filter = FilterCriteria.of(FilterCondition.of("province.name", "eq", "Roma"));
    //when
    Page<Address> result = addressRepository.find(filter, pageable);
    //then
    assertThat(result, iterableWithSize(2));
    assertThat(result.getContent().get(0).city().name(), equalTo("Fiumicino"));
    assertThat(result.getContent().get(1).city().name(), equalTo("Roma"));
  }

}
