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

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.repository.AddressRepository;
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
class AddressControllerTest {

  static final int NUM_ADDRESSES = 4;

  final static String ADDRESS_URL = "/addresses";
  @Autowired
  MockMvc mvc;
  @Autowired
  AddressRepository addressRepository;
  @Value("${yrs-geodata.api.basepath}")
  String basePath;

  @Test
  void getAllAddresses() throws Exception {
    mvc.perform(get(basePath + ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("page", "0")
            .param("size", "2147483647"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(NUM_ADDRESSES)))

        .andExpect(jsonPath("$.content[0].city.name", is("Bresso")))
        .andExpect(jsonPath("$.content[1].city.name", is("Fiumicino")))
        .andExpect(jsonPath("$.content[2].city.name", is("Paddington")))
        .andExpect(jsonPath("$.content[3].city.name", is("Roma")))
        .andExpect(jsonPath("$.totalPages", is(1)))
        .andExpect(jsonPath("$.totalElements", is(NUM_ADDRESSES)))
        .andExpect(jsonPath("$.last", is(true)))
        .andExpect(jsonPath("$.first", is(true)))
        .andExpect(jsonPath("$.size", is(2147483647)))
        .andExpect(jsonPath("$.number", is(0)))
        .andExpect(jsonPath("$.numberOfElements", is(NUM_ADDRESSES)))
        .andExpect(jsonPath("$.empty", is(false)))
        .andExpect(jsonPath("$.sort.sorted", is(true)));
  }

  @Test
  void getAllAddressesInProvinceOfRomaWithOrderCityNameDesc() throws Exception {
    mvc.perform(get(basePath + ADDRESS_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .param("filter.province.name.value", "Roma")
            .param("filter.province.name.operator", "eq")
            .param("sort", "city.name,ASC")
            .param("page", "0")
            .param("size", Integer.toString(Integer.MAX_VALUE)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.content").isArray())
        .andExpect(jsonPath("$.content", hasSize(2)))
        .andExpect(jsonPath("$.content[0].city.name", is("Fiumicino")))
        .andExpect(jsonPath("$.content[1].city.name", is("Roma")));
  }

  @Test
  void getByUuid() throws Exception {
    String uUID = "00000000-0000-0000-0000-000000000004";
    mvc.perform(get(basePath + ADDRESS_URL + "/" + uUID).contentType(
            MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.addressLine1", is("Via delle Magnolie 12")))
        .andExpect(jsonPath("$.addressLine2", is("Scala A")))
        .andExpect(jsonPath("$.postalCode", is("20121")))
        .andExpect(jsonPath("$.city.name", is("Bresso")))
        .andExpect(jsonPath("$.city.uuid").isEmpty())
        .andExpect(jsonPath("$.province.name", is("Milano")))
        .andExpect(jsonPath("$.province.uuid").isNotEmpty())
        .andExpect(jsonPath("$.country.name", is("Italy")))
        .andExpect(jsonPath("$.country.uuid").isNotEmpty());
  }

  @Test
  void getByNotExistingUuid() throws Exception {
    UUID uUID = UUID.randomUUID();
    mvc.perform(get(basePath + ADDRESS_URL + "/" + uUID).contentType(
            MediaType.APPLICATION_JSON))
        .andExpect(status().is4xxClientError());
  }

}
