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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Continent;
import com.yourrents.services.geodata.repository.ContinentRepository;
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
class ContinentControllerCreateUpdateDeleteTest {


  final static String CONTINENT_URL = "/continents";

  @Autowired
  private MockMvc mvc;
  @Autowired
  private ContinentRepository continentRepository;
  @Value("${yrs-geodata.api.basepath}")
  private String basePath;

  @Test
  void createNewContinent() throws Exception {
    mvc.perform(post(basePath + CONTINENT_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "code": "AT",
                   "name": "Atlantis"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code", is("AT")))
        .andExpect(jsonPath("$.name", is("Atlantis")))
        .andExpect(jsonPath("$.uuid").isNotEmpty());
  }

  @Test
  void updateAnExistingContinent() throws Exception {
    //given
    Continent continent = continentRepository.findById(1).orElseThrow(RuntimeException::new);

    //when-then
    mvc.perform(patch(basePath + CONTINENT_URL + "/" + continent.uuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "code": "AT",
                   "name": "Atlantis"
                }
                """))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.code", is("AT")))
        .andExpect(jsonPath("$.name", is("Atlantis")))
        .andExpect(jsonPath("$.uuid", is(continent.uuid().toString())));
  }

  @Test
  void deleteAnExistingCountryWithCountries() throws Exception {
    Continent continent = continentRepository.findById(1).orElseThrow(RuntimeException::new);
    //we expect a 4xx error because this continent it is referenced by at least one country.
    mvc.perform(delete(basePath + CONTINENT_URL + "/" + continent.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  void deleteAnExistingCountryWithNoCountries() throws Exception {
    //given
    Continent continent = continentRepository.findById(1000000).orElseThrow(RuntimeException::new);
    //when-then
    mvc.perform(delete(basePath + CONTINENT_URL + "/" + continent.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void deleteANotExistingContinent() throws Exception {
    UUID randomUUID = UUID.randomUUID();
    mvc.perform(delete(basePath + CONTINENT_URL + "/" + randomUUID)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
  }

}
