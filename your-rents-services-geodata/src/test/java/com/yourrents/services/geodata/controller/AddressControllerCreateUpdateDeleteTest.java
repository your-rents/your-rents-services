package com.yourrents.services.geodata.controller;

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
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.AddressRepository;
import com.yourrents.services.geodata.repository.CityRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
class AddressControllerCreateUpdateDeleteTest {

  final static String ADDRESS_URL = "/addresses";
  @Autowired
  MockMvc mvc;
  @Autowired
  AddressRepository addressRepository;

  @Autowired
  CityRepository cityRepository;


  @Value("${yrs-geodata.api.basepath}")
  String basePath;

  @Test
  void createNewAddressWithNoLinkedDetails() throws Exception {
    mvc.perform(post(basePath + ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "addressLine1": "123 Maple Street",
                   "addressLine2": "Apt 4B",
                   "postalCode": "L9T 2P3",
                   "city": {
                      "name": "Oakville"
                   },
                   "country": {
                     "name": "Canada"
                   }
                 }
                """))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.addressLine1", is("123 Maple Street")))
        .andExpect(jsonPath("$.addressLine2", is("Apt 4B")))
        .andExpect(jsonPath("$.postalCode", is("L9T 2P3")))
        .andExpect(jsonPath("$.city.name", is("Oakville")))
        .andExpect(jsonPath("$.city.uuid").isEmpty())
        .andExpect(jsonPath("$.province").isEmpty())
        .andExpect(jsonPath("$.country.name", is("Canada")));
  }

  @Test
  void createNewAddressWithLinkedCityAndProvinceAndNotLinkedCountry() throws Exception {
    City city = cityRepository.findById(1).orElseThrow();
    mvc.perform(post(basePath + ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "addressLine1": "123 Maple Street",
                   "addressLine2": "Apt 4B",
                   "postalCode": "L9T 2P3",
                   "city": {
                      "uuid": "%s"
                   },
                   "province": {
                      "uuid": "%s"
                   },
                   "country": {
                     "name": "Themyscira"
                   }
                 }
                """.formatted(city.uuid(), city.province().uuid())))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.city.name", is("Abano Terme")))
        .andExpect(jsonPath("$.province.name", is("Padova")))
        .andExpect(jsonPath("$.country.name", is("Themyscira")));
  }

  @Test
  void createNewAddressWithInvalidLinkedProvince() throws Exception {
    City city = cityRepository.findById(1).orElseThrow();
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(post(basePath + ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "addressLine1": "123 Maple Street",
                   "addressLine2": "Apt 4B",
                   "postalCode": "L9T 2P3",
                   "city": {
                      "uuid": "%s"
                   },
                   "province": {
                      "uuid": "%s"
                   },
                   "country": {
                     "name": "Themyscira"
                   }
                 }
                """.formatted(city.uuid(), randomUUID)))
        .andExpect(status().is4xxClientError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
  }

  @Test
  void deleteAnExistingAddress() throws Exception {
    //given
    Address address = addressRepository.findById(1000000000).orElseThrow();
    assertThat(address.uuid()).isNotNull();

    //when-then
    mvc.perform(delete(basePath + ADDRESS_URL + "/" + address.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void deleteANotExistingAddress() throws Exception {
    //given
    UUID randomUUID = UUID.randomUUID();

    //when-then
    mvc.perform(delete(basePath + ADDRESS_URL + "/" + randomUUID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

  @Test
  void updateAddressLine1AndCountryOnAnExistingAddress() throws Exception {
    Address oldAddress = addressRepository.findById(1000000000).orElseThrow();
    mvc.perform(patch(basePath + ADDRESS_URL + "/" + oldAddress.uuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "addressLine1": "New Address",
                   "country": {
                     "name": "New Country"
                   }
                }
                """))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.addressLine1", is("New Address")))
        .andExpect(jsonPath("$.addressLine2", is(oldAddress.addressLine2())))
        .andExpect(jsonPath("$.postalCode", is(oldAddress.postalCode())))
        .andExpect(jsonPath("$.city.name", is(oldAddress.city().name())))
        .andExpect(jsonPath("$.city.uuid", is(oldAddress.city().uuid())))
        .andExpect(jsonPath("$.province.name", is(oldAddress.province().name())))
        .andExpect(jsonPath("$.province.uuid", is(oldAddress.province().uuid())))
        .andExpect(jsonPath("$.country.name", is("New Country")))
        .andExpect(jsonPath("$.country.uuid").isEmpty());
  }

}
