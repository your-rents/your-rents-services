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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Region;
import com.yourrents.services.geodata.repository.CountryRepository;
import com.yourrents.services.geodata.repository.RegionRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.yourrents.services.geodata.model.Country;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
class RegionControllerCreateUpdateDeleteTest {

  static final int NUM_REGIONS = 21;//there are test data regions
  final static String REGION_URL = "/regions";
  @Autowired
  MockMvc mvc;
  @Autowired
  RegionRepository regionRepository;

  @Autowired
  CountryRepository countryRepository;
  @Value("${yrs-geodata.api.basepath}")
  String basePath;

  @Test
  void createNewRegionInItalyCountry() throws Exception {
    Page<Region> page = regionRepository.find(
        FilterCriteria.of(FilterCondition.of("country", "eq", "Italy")),
        PageRequest.ofSize(1));
    UUID countryUuid = page.getContent().get(0).country().uuid();

    mvc.perform(post(basePath + REGION_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "name": "New Region",
                    "country": {
                        "uuid": "%s"
                    },
                    "localData": {
                        "itCodiceIstat": "11"
                    }
                }
                """.formatted(countryUuid)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.uuid").isNotEmpty())
        .andExpect(jsonPath("$.name", is("New Region")))
        .andExpect(jsonPath("$.localData").exists())
        .andExpect(jsonPath("$.localData.itCodiceIstat", is("11")))
        .andExpect(jsonPath("$.country").exists())
        .andExpect(jsonPath("$.country.uuid", is(countryUuid.toString())))
        .andExpect(jsonPath("$.country.localName", is("Italy")));
  }

  @Test
  void deleteAnExistingRegion() throws Exception {
    Region region = regionRepository.findById(1).orElseThrow(RuntimeException::new);

    //we expect a 4xx error because this region it is referenced by at least one province.
    mvc.perform(delete(basePath + REGION_URL + "/" + region.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  void updateAnExistingRegion() throws Exception {
    //given
    UUID countryUuid = countryRepository.findById(1)
        .map(Country::uuid)
        .orElseThrow(RuntimeException::new);
    Region region = regionRepository
        .findById(1).orElseThrow(RuntimeException::new);
    assertThat(region.country().uuid(), is(not(countryUuid)));
    //when-then
    mvc.perform(patch(basePath + REGION_URL + "/" + region.uuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                    "name": "Updated Region",
                    "country": {
                        "uuid": "%s"
                    },
                    "localData": {
                        "itCodiceIstat": "11"
                    }
                }
                """.formatted(countryUuid)))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.uuid", is(region.uuid().toString())))
        .andExpect(jsonPath("$.name", is("Updated Region")))
        .andExpect(jsonPath("$.localData").exists())
        .andExpect(jsonPath("$.localData.itCodiceIstat", is("11")))
        .andExpect(jsonPath("$.country").exists())
        .andExpect(jsonPath("$.country.uuid", is(countryUuid.toString())))
        .andExpect(jsonPath("$.country.localName", is("Andorra")));
  }

}
