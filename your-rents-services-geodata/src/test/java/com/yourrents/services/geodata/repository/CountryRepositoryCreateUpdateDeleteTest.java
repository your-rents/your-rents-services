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

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.model.Country.Continent;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@Transactional
class CountryRepositoryCreateUpdateDeleteTest {

	@Autowired
	CountryRepository countryRepository;

  @Test
  void createNewCountryInEurope() {
    UUID europeUuid = findEuropeUuid();
    Country newCountry = new Country(null, "ZZ", "New Country FN", "ZZZ",
        "New Country", 10, new Continent(europeUuid, null));
    Country result = countryRepository.create(newCountry);
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.isoCode()).isEqualTo("ZZ");
    assertThat(result.englishFullName()).isEqualTo("New Country FN");
    assertThat(result.iso3()).isEqualTo("ZZZ");
    assertThat(result.localName()).isEqualTo("New Country");
    assertThat(result.number()).isEqualTo(10);
    Continent continent = result.continent();
    assertThat(continent).isNotNull();
    assertThat(continent.name()).isEqualTo("Europe");
    assertThat(continent.uuid()).isEqualTo(europeUuid);
  }

  @Test
  void createNewCountryWithInvalidContinent() {
    UUID randomUUID = UUID.randomUUID();
    Country newCountry = new Country(null, "ZZ", "New Country FN", "ZZZ",
        "New Country", 10, new Continent(randomUUID, null));
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            countryRepository.create(newCountry))
        .withMessageContaining(randomUUID.toString())
        .withNoCause();
  }

  @Test
  void createNewCountryWithNoContinent() {
    Country newCountry = new Country(null, "ZZ", "New Country FN", "ZZZ",
        "New Country", 10, null);
    Country result = countryRepository.create(newCountry);
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
  }

  @Test
  void deleteAnExistingCountryWithRegions() {
    Searchable filterForItaly = FilterCriteria.of(
        FilterCondition.of("localName", "eq", "Italy"));
    Page<Country> page = countryRepository.find(filterForItaly,
        PageRequest.ofSize(1));
    Country country = page.getContent().get(0);
    UUID italyUuid = country.uuid();
    assertThatExceptionOfType(DataConflictException.class).isThrownBy(() ->
            countryRepository.delete(italyUuid))
        .withMessageContaining(country.uuid().toString());
  }

  @Test
  void deleteAnExistingCountryWithNoRegions() {
    //given
    Country country = countryRepository.findById(1).orElseThrow(RuntimeException::new);
    //when
    countryRepository.delete(country.uuid());
    //then
    Optional<Country> optResult = countryRepository.findByExternalId(country.uuid());
    assertThat(optResult).isEmpty();
  }

  @Test
  void deleteANotExistingCountry() {
    UUID randomUUID = UUID.randomUUID();
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            countryRepository.delete(randomUUID))
        .withMessageContaining(randomUUID.toString());
  }

  @Test
  void updateAnExistingCountry() {
    //given
    UUID europeUuid = findEuropeUuid();
    Country countryInAfrica = findCountryInContinent("Africa");
    assertThat(countryInAfrica.continent().uuid()).isNotEqualByComparingTo(europeUuid);

    Country newCountry = new Country(null, null, null, null,
        "Updated Country", 10,
        new Continent(europeUuid, null));
    //when
    Country result = countryRepository.update(countryInAfrica.uuid(), newCountry);
    //then
    assertThat(result).isNotNull();
    //data updated
    assertThat(result.localName()).isEqualTo("Updated Country");
    assertThat(result.number()).isEqualTo(10);
    assertThat(result.continent()).isNotNull();
    //old data not modified
    assertThat(result.continent().uuid()).isEqualTo(europeUuid);
    assertThat(result.uuid()).isEqualTo(countryInAfrica.uuid());
    assertThat(result.isoCode()).isEqualTo(countryInAfrica.isoCode());
    assertThat(result.englishFullName()).isEqualTo(countryInAfrica.englishFullName());
    assertThat(result.iso3()).isEqualTo(countryInAfrica.iso3());
  }

  @Test
  void updateANotExistingCountry() {
    UUID randomUUID = UUID.randomUUID();
    Country newCountry = new Country(null, null, null, null,
        "Updated Country", 10,
        null);
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            countryRepository.update(randomUUID, newCountry))
        .withMessageContaining(randomUUID.toString());
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
