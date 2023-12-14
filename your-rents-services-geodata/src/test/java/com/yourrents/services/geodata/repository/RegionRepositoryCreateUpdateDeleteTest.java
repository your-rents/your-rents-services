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
import com.yourrents.services.geodata.model.GeoReference;
import com.yourrents.services.geodata.model.Region;
import com.yourrents.services.geodata.model.RegionLocalData;
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
class RegionRepositoryCreateUpdateDeleteTest {

	@Autowired
	RegionRepository regionRepository;

  @Autowired
  CountryRepository countryRepository;

	@Test
	void createNewRegionInItaly() {
		UUID italyUuid = findItalyUuid();
		Region newRegion = new Region(null, "New Region",
				new RegionLocalData("NR"),
				new GeoReference(italyUuid, null));

		Region result = regionRepository.create(newRegion);

		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("New Region");
		assertThat(result.country().name()).isEqualTo("Italy");
		assertThat(result.country().uuid()).isEqualTo(italyUuid);

		RegionLocalData regionLocalData = result.localData();
		assertThat(regionLocalData).isNotNull();
		assertThat(regionLocalData.itCodiceIstat()).isEqualTo("NR");
	}

  @Test
  void createNewRegionWithInvalidCountry() {
    UUID randomUUID = UUID.randomUUID();
    Region newRegion = new Region(null, "New Region",
        null,
        new GeoReference(randomUUID, null));
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            regionRepository.create(newRegion))
        .withMessageContaining(randomUUID.toString())
        .withNoCause();
  }

  @Test
  void createNewRegionWithNoCountryAndNoLocalData() {
    Region newRegion = new Region(null, "New Region",
        null,
        null);
    Region result = regionRepository.create(newRegion);
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("New Region");
    assertThat(result.country()).isNull();
    assertThat(result.localData()).isNull();
  }

  @Test
  void deleteAnExistingRegionWithProvinces() {
    Searchable filterForVeneto = FilterCriteria.of(
        FilterCondition.of("name", "eq", "Veneto"));
    Page<Region> page = regionRepository.find(filterForVeneto,
        PageRequest.ofSize(1));
    Region region = page.getContent().get(0);
    UUID venetoUuid = region.uuid();
    assertThatExceptionOfType(DataConflictException.class).isThrownBy(() ->
            regionRepository.delete(venetoUuid))
        .withMessageContaining(region.uuid().toString());
  }

  @Test
  void deleteAnExistingRegionWithNoProvinces() {
    //given
    Searchable filterForNewRegion = FilterCriteria.of(
        FilterCondition.of("name", "eq", "ZZZ No Provinces Region"));
    Page<Region> page = regionRepository.find(filterForNewRegion,
        PageRequest.ofSize(1));
    Region region = page.getContent().get(0);
    assertThat(region.name()).isEqualTo("ZZZ No Provinces Region");
    //when
    regionRepository.delete(region.uuid());
    //then
    Optional<Region> optResult = regionRepository.findByExternalId(region.uuid());
    assertThat(optResult).isEmpty();
  }

  @Test
  void deleteANotExistingRegion() {
    UUID randomUUID = UUID.randomUUID();
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            regionRepository.delete(randomUUID))
        .withMessageContaining(randomUUID.toString());
  }

  @Test
  void updateAnExistingRegion() {
    //given
    Region region = regionRepository.findById(1).orElseThrow(RuntimeException::new);
    UUID countryId = findFirstCountryId();
    assertThat(region.country().uuid()).isNotEqualByComparingTo(countryId);

    Region updateRegion = new Region(null, "Update Region",
        new RegionLocalData("11"),
        new GeoReference(countryId, null));
    //when
    Region result = regionRepository.update(region.uuid(), updateRegion);
    //then
    assertThat(result).isNotNull();
    assertThat(result.name()).isEqualTo("Update Region");
    assertThat(result.country().name()).isEqualTo("Andorra");
    assertThat(result.country().uuid()).isEqualTo(countryId);
    RegionLocalData regionLocalData = result.localData();
    assertThat(regionLocalData).isNotNull();
    assertThat(regionLocalData.itCodiceIstat()).isEqualTo("11");
  }

  @Test
  void updateANotExistingRegion() {
    UUID randomUUID = UUID.randomUUID();
    Region updateRegion = new Region(null, "Update Region", null, null);
    assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
            regionRepository.update(randomUUID, updateRegion))
        .withMessageContaining(randomUUID.toString());
  }

	private UUID findItalyUuid() {
		Searchable filterForItaly = FilterCriteria.of(
				FilterCondition.of("country.name", "eq", "Italy"));
		Page<Region> provincePage = regionRepository.find(filterForItaly,
				PageRequest.ofSize(1));
		return provincePage.getContent().get(0).country().uuid();
	}

  private UUID findFirstCountryId() {
    return countryRepository.findById(1)
        .map(com.yourrents.services.geodata.model.Country::uuid)
        .orElseThrow(RuntimeException::new);
  }
}
