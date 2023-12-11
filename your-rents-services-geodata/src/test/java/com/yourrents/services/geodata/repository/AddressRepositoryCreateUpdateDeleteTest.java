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
import com.yourrents.services.geodata.model.Address.AddressCity;
import com.yourrents.services.geodata.model.Address.AddressCountry;
import com.yourrents.services.geodata.model.Address.AddressProvince;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.Country;
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
        new AddressCity(city.uuid(), null),
        new AddressProvince(city.province().uuid(), null),
        new AddressCountry(country.uuid(), null)
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
    assertThat(result.country().localName()).isEqualTo(country.localName());
  }

  @Test
  void crateNewAddressWithNoLinkedDetails() {
    //given
    Address newAddress = new Address(null, "via roma 10", "first floor", "30038",
        new AddressCity(null, "Cloud City"),
        new AddressProvince(null, "Eldermoor Enclave"),
        new AddressCountry(null, "Astraloria")
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
    assertThat(result.country().localName()).isEqualTo("Astraloria");
  }

  @Test
  void createNewAddressWithNotExistingProvince() {
    //given
    City city = cityRepository.findById(1).orElseThrow();
    UUID randomUUID = UUID.randomUUID();
    Country country = countryRepository.findById(1).orElseThrow();
    Address newAddress = new Address(null, "via roma 10", "first floor", "30038",
        new AddressCity(city.uuid(), null),
        new AddressProvince(randomUUID, null),
        new AddressCountry(country.uuid(), null)
    );
    //when-then
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            addressRepository.create(newAddress))
        .withMessageContaining(randomUUID.toString())
        .withNoCause();
  }


}
