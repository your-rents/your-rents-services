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

import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Continent;
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
class ContinentRepositoryCreateUpdateDeleteTest {

  @Autowired
  ContinentRepository continentRepository;

  @Test
  void createNewContinent() {
    Continent newContinent = new Continent(null, "NC", "New Continent");
    Continent result = continentRepository.create(newContinent);
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isNotNull();
    assertThat(result.code()).isEqualTo("NC");
    assertThat(result.name()).isEqualTo("New Continent");
  }

  @Test
  void deleteAnExistingContinentWithCountries() {
    final Continent europe = continentRepository.findById(4).orElseThrow(RuntimeException::new);
    assertThat(europe.name()).isEqualTo("Europe");
    assertThatExceptionOfType(DataConflictException.class).isThrownBy(() ->
            continentRepository.delete(europe.uuid()))
        .withMessageContaining(europe.uuid().toString());
  }

  @Test
  void deleteAnExistingContinentWithNoCountries() {
    //given
    Continent continent = continentRepository.findById(1000000).orElseThrow(RuntimeException::new);
    //when
    continentRepository.delete(continent.uuid());
    //then
    Optional<Continent> optResult = continentRepository.findByExternalId(continent.uuid());
    assertThat(optResult).isEmpty();
  }

  @Test
  void deleteANotExistingContinent() {
    UUID randomUUID = UUID.randomUUID();
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            continentRepository.delete(randomUUID))
        .withMessageContaining(randomUUID.toString());
  }

  @Test
  void updateAnExistingContinent() {
    //given
    final Continent europe = continentRepository.findById(4).orElseThrow(RuntimeException::new);
    assertThat(europe.name()).isEqualTo("Europe");

    Continent newContinent = new Continent(null, "UC", "Update Continent");
    //when
    Continent result = continentRepository.update(europe.uuid(), newContinent);
    //then
    assertThat(result).isNotNull();
    assertThat(result.uuid()).isEqualTo(europe.uuid());
    assertThat(result.code()).isEqualTo(newContinent.code());
    assertThat(result.name()).isEqualTo(newContinent.name());
  }

  @Test
  void updateANotExistingContinent() {
    UUID randomUUID = UUID.randomUUID();
    Continent newContinent = new Continent(null, "UC", "Update Continent");
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            continentRepository.update(randomUUID, newContinent))
        .withMessageContaining(randomUUID.toString());
  }

}
