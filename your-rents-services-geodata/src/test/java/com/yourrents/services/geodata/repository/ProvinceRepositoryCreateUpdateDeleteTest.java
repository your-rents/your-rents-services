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
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.model.ProvinceLocalData;
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
class ProvinceRepositoryCreateUpdateDeleteTest {


	@Autowired
	ProvinceRepository provinceRepository;

	@Test
	void createNewProvinceInVeneto() {
    //given
		UUID venetoUuid = findVenetoUuid();
		Province newProvince = new Province(null, "New Province",
				new ProvinceLocalData("10", "NP"),
				new GeoReference(venetoUuid, null));
    //when
		Province result = provinceRepository.create(newProvince);
    //then
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("New Province");
		assertThat(result.region().name()).isEqualTo("Veneto");
		assertThat(result.region().uuid()).isEqualTo(venetoUuid);
		ProvinceLocalData provinceLocalData = result.localData();
		assertThat(provinceLocalData).isNotNull();
		assertThat(provinceLocalData.itCodiceIstat()).isEqualTo("10");
		assertThat(provinceLocalData.itSigla()).isEqualTo("NP");
	}

	@Test
	void createNewProvinceWithInvalidRegion() {
		UUID randomUUID = UUID.randomUUID();
		Province newProvince = new Province(null, "New Province",
				null,
				new GeoReference(randomUUID, null));
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.create(newProvince))
				.withMessageContaining(randomUUID.toString())
				.withNoCause();
	}

	@Test
	void createNewProvinceWithNoRegionAndNoLocalData() {
    //given
		Province newProvince = new Province(null, "New Province",
				null,
				null);
    //when
		Province result = provinceRepository.create(newProvince);
    //then
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("New Province");
		assertThat(result.region()).isNull();
		assertThat(result.localData()).isNull();
	}

	@Test
	void deleteAnExistingProvince() {
    //given
		Searchable filterForVenezia = FilterCriteria.of(
				FilterCondition.of("name", "eq", "Venezia"));
		Page<Province> provincePage = provinceRepository.find(filterForVenezia,
				PageRequest.ofSize(1));
		Province province = provincePage.getContent().get(0);
		UUID veneziaUuid = province.uuid();
    //when-then
		assertThatExceptionOfType(DataConflictException.class).isThrownBy(() ->
						provinceRepository.delete(veneziaUuid))
				.withMessageContaining(province.uuid().toString());
	}

	@Test
	void deleteANotExistingProvince() {
		UUID randomUUID = UUID.randomUUID();
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.delete(randomUUID))
				.withMessageContaining(randomUUID.toString());
	}

	@Test
	void updateAnExistingProvince() {
    //given
		Province province = provinceRepository.findById(1).orElseThrow(RuntimeException::new);
		UUID venetoUuid = findVenetoUuid();
		assertThat(province.region().uuid()).isNotEqualByComparingTo(venetoUuid);
		Province updateProvince = new Province(null, "Update Province",
				new ProvinceLocalData("11", "22"),
				new GeoReference(venetoUuid, null));
    //when
		Province result = provinceRepository.update(province.uuid(), updateProvince);
    //then
		assertThat(result).isNotNull();
		assertThat(result.name()).isEqualTo("Update Province");
		assertThat(result.region().name()).isEqualTo("Veneto");
		assertThat(result.region().uuid()).isEqualTo(venetoUuid);
		ProvinceLocalData provinceLocalData = result.localData();
		assertThat(provinceLocalData).isNotNull();
		assertThat(provinceLocalData.itCodiceIstat()).isEqualTo("11");
		assertThat(provinceLocalData.itSigla()).isEqualTo("22");
	}

	@Test
	void updateANotExistingProvince() {
		UUID randomUUID = UUID.randomUUID();
		Province updateProvince = new Province(null, "Update Province", null, null);
		assertThatExceptionOfType(DataNotFoundException.class).isThrownBy(() ->
						provinceRepository.update(randomUUID, updateProvince))
				.withMessageContaining(randomUUID.toString());
	}

	private UUID findVenetoUuid() {
		Searchable filterForVeneto = FilterCriteria.of(
				FilterCondition.of("region.name", "eq", "Veneto"));
		Page<Province> provincePage = provinceRepository.find(filterForVeneto,
				PageRequest.ofSize(1));
		return provincePage.getContent().get(0).region().uuid();
	}
}
