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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatExceptionOfType;

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.model.GeoReference;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@Transactional
class AddressRepositoryCreateUpdateDeleteTest {

  @Autowired
  AddressRepository addressRepository;
  @Autowired
  CityRepository cityRepository;
  @Autowired
  CountryRepository countryRepository;

  @Test
  void crateNewAddress() {
    //given
    City city = cityRepository.findById(1).orElseThrow();
    Country country = countryRepository.findById(1).orElseThrow();
    Address newAddress = new Address(null, "via roma 10", "first floor", "30038",
        new GeoReference(city.uuid(), null),
        new GeoReference(city.province().uuid(), null),
        new GeoReference(country.uuid(), null)
    );
    //when
    Address result = addressRepository.create(newAddress);
    //then
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.addressLine1()).isEqualTo("via roma 10");
    assertThat(result.addressLine2()).isEqualTo("first floor");
    assertThat(result.postalCode()).isEqualTo("30038");
    assertThat(result.city().uuid()).isEqualTo(city.uuid());
    assertThat(result.city().name()).isEqualTo(city.name());
    assertThat(result.province().uuid()).isEqualTo(city.province().uuid());
    assertThat(result.province().name()).isEqualTo(city.province().name());
    assertThat(result.country().uuid()).isEqualTo(country.uuid());
    assertThat(result.country().name()).isEqualTo(country.localName());
  }

  @Test
  void crateNewAddressWithNoLinkedDetails() {
    //given
    Address newAddress = new Address(null, "via roma 10", "first floor", "30038",
        new GeoReference(null, "Cloud City"),
        new GeoReference(null, "Eldermoor Enclave"),
        new GeoReference(null, "Astraloria")
    );
    //when
    Address result = addressRepository.create(newAddress);
    //then
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.addressLine1()).isEqualTo("via roma 10");
    assertThat(result.addressLine2()).isEqualTo("first floor");
    assertThat(result.postalCode()).isEqualTo("30038");
    assertThat(result.city().uuid()).isNull();
    assertThat(result.city().name()).isEqualTo("Cloud City");
    assertThat(result.province().uuid()).isNull();
    assertThat(result.province().name()).isEqualTo("Eldermoor Enclave");
    assertThat(result.country().uuid()).isNull();
    assertThat(result.country().name()).isEqualTo("Astraloria");
  }

  @Test
  void createNewAddressWithNotExistingProvince() {
    //given
    City city = cityRepository.findById(1).orElseThrow();
    UUID randomUUID = UUID.randomUUID();
    Country country = countryRepository.findById(1).orElseThrow();
    Address newAddress = new Address(null, "via roma 10", "first floor", "30038",
        new GeoReference(city.uuid(), null),
        new GeoReference(randomUUID, null),
        new GeoReference(country.uuid(), null)
    );
    //when-then
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            addressRepository.create(newAddress))
        .withMessageContaining(randomUUID.toString())
        .withNoCause();
  }

  @Test
  void deleteAnExistingAddress() {
    //given
    Address address = addressRepository.findById(1000000000).orElseThrow();
    assertThat(address.uuid()).isNotNull();
    //when
    boolean delete = addressRepository.delete(address.uuid());
    //then
    assertThat(delete).isTrue();
    Optional<Address> optResult = addressRepository.findByExternalId(address.uuid());
    assertThat(optResult).isEmpty();
  }

  @Test
  void deleteANotExistingAddress() {
    UUID randomUUID = UUID.randomUUID();
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            addressRepository.delete(randomUUID))
        .withMessageContaining(randomUUID.toString());
  }

  @Test
  void updateAnExistingAddress() {
    //given
    Address oldAddress = addressRepository.findById(1000000000).orElseThrow();
    City city = cityRepository.findById(1).orElseThrow();
    Address updateAddress = new Address(null, "via roma 10", null, null,
        new GeoReference(city.uuid(), null),
        new GeoReference(city.province().uuid(), null),
        null
    );
    //when
    Address result = addressRepository.update(oldAddress.uuid(), updateAddress);
    //then
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.addressLine1()).isEqualTo("via roma 10");
    assertThat(result.addressLine2()).isEqualTo(oldAddress.addressLine2());
    assertThat(result.postalCode()).isEqualTo(oldAddress.postalCode());
    assertThat(result.city().uuid()).isEqualTo(city.uuid());
    assertThat(result.city().name()).isEqualTo(city.name());
    assertThat(result.province().uuid()).isEqualTo(city.province().uuid());
    assertThat(result.province().name()).isEqualTo(city.province().name());
    assertThat(result.country().uuid()).isEqualTo(oldAddress.country().uuid());
    assertThat(result.country().name()).isEqualTo(oldAddress.country().name());
  }

}
