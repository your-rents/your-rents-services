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

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.repository.CountryRepository;
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

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
class CountryControllerCreateUpdateDeleteTest {

  final static String COUNTRY_URL = "/countries";
  @Autowired
  MockMvc mvc;

  @Autowired
  CountryRepository countryRepository;

  @Value("${yrs-geodata.api.basepath}")
  String basePath;

  @Test
  void createNewCountryInEurope() throws Exception {
    UUID europeUuid = findEuropeUuid();
    mvc.perform(post(basePath + COUNTRY_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "isoCode": "ZZ",
                   "englishFullName": "New Country FN",
                   "iso3": "ZZZ",
                   "localName": "New Country",
                   "number": 10,
                   "continent": {
                     "uuid": "%s"
                   }
                 }
                """.formatted(europeUuid)))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.uuid").isNotEmpty());
  }

  @Test
  void deleteAnExistingCountryWithRegions() throws Exception {
    Searchable filterForCountry = FilterCriteria.of(
        FilterCondition.of("localName", "eq", "Italy"));
    Page<Country> page = countryRepository.find(filterForCountry,
        PageRequest.ofSize(1));
    Country country = page.getContent().get(0);
    assertThat(country.localName()).isEqualTo("Italy");
    //we expect a 4xx error because this country it is referenced by at least one region.
    mvc.perform(delete(basePath + COUNTRY_URL + "/" + country.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
  }

  @Test
  void deleteAnExistingCountryWithNoRegions() throws Exception {
    //given
    Country country = countryRepository.findById(1).orElseThrow(RuntimeException::new);
    assertThat(country.localName()).isEqualTo("Andorra");
    //when-then
    mvc.perform(delete(basePath + COUNTRY_URL + "/" + country.uuid())
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().is2xxSuccessful());
  }

  @Test
  void updateAnExistingCountry() throws Exception {
    //given
    Country countryInAfrica = findCountryInContinent("Africa");
    UUID europeUuid = findEuropeUuid();
    assertThat(countryInAfrica.continent().uuid()).isNotEqualByComparingTo(europeUuid);
    //when-then
    mvc.perform(patch(basePath + COUNTRY_URL + "/" + countryInAfrica.uuid())
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                   "localName": "New Country",
                   "number": 10,
                   "continent": {
                     "uuid": "%s"
                   }
                 }
                """.formatted(europeUuid)))
        .andExpect(status().is2xxSuccessful())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.localName", is("New Country")))
        .andExpect(jsonPath("$.continent.uuid", is(europeUuid.toString())));
  }

  private UUID findEuropeUuid() {
    Country aCountryInEurope = findCountryInContinent("Europe");
    return aCountryInEurope.continent().uuid();
  }

  private Country findCountryInContinent(String continentName) {
    Searchable filterForEurope = FilterCriteria.of(
        FilterCondition.of("continent.name", "eq", continentName));
    Page<Country> page = countryRepository.find(
        filterForEurope,
        PageRequest.ofSize(1));
    return page.getContent().get(0);
  }


}
