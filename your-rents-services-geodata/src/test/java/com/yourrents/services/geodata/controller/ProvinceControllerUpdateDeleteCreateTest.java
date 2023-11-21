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
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.repository.ProvinceRepository;
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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
class ProvinceControllerUpdateDeleteCreateTest {

	final static String PROVINCE_URL = "/provinces";
	@Autowired
	MockMvc mvc;
	@Autowired
	ProvinceRepository provinceRepository;
	@Value("${yrs-geodata.api.basepath}")
	String basePath;

	@Test
	void createNewProvinceInVenetoRegion() throws Exception {
		Page<Province> provincePage = provinceRepository.find(
				FilterCriteria.of(FilterCondition.of("region.name", "eq", "Veneto")),
				PageRequest.ofSize(1));
		UUID venetoUuid = provincePage.getContent().get(0).region().uuid();

		mvc.perform(post(basePath + PROVINCE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "name": "New Province",
								    "region": {
								        "uuid": "%s"
								    },
								    "localData": {
								        "itCodiceIstat": "11",
								        "itSigla": "22"
								    }
								}
								""".formatted(venetoUuid)))
				.andExpect(status().isCreated())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.uuid").isNotEmpty())
				.andExpect(jsonPath("$.name", is("New Province")))
				.andExpect(jsonPath("$.localData").exists())
				.andExpect(jsonPath("$.localData.itCodiceIstat", is("11")))
				.andExpect(jsonPath("$.localData.itSigla", is("22")))
				.andExpect(jsonPath("$.region").exists())
				.andExpect(jsonPath("$.region.uuid", is(venetoUuid.toString())))
				.andExpect(jsonPath("$.region.name", is("Veneto")));
	}

	@Test
	void deleteAnExistingProvince() throws Exception {
		Province province = provinceRepository.findById(1).orElseThrow(RuntimeException::new);

		//we expect a 4xx error because this province it is referenced by at least one city.
		mvc.perform(delete(basePath + PROVINCE_URL + "/" + province.uuid())
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	void deleteANotExistingProvince() throws Exception {
		UUID randomUUID = UUID.randomUUID();
		mvc.perform(delete(basePath + PROVINCE_URL + "/" + randomUUID)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().is4xxClientError());
	}

	@Test
	void updateAnExistingProvince() throws Exception {
		Page<Province> provincePage = provinceRepository.find(
				FilterCriteria.of(FilterCondition.of("region.name", "eq", "Veneto")),
				PageRequest.ofSize(1));
		UUID venetoUuid = provincePage.getContent().get(0).region().uuid();
		Province province = provinceRepository
				.findById(1).orElseThrow(RuntimeException::new);
		assertThat(province.region().uuid(), is(not(venetoUuid)));

		mvc.perform(patch(basePath + PROVINCE_URL + "/" + province.uuid())
						.contentType(MediaType.APPLICATION_JSON)
						.content("""
								{
								    "name": "Updated Province",
								    "region": {
								        "uuid": "%s"
								    },
								    "localData": {
								        "itCodiceIstat": "11",
								        "itSigla": "22"
								    }
								}
								""".formatted(venetoUuid)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.uuid", is(province.uuid().toString())))
				.andExpect(jsonPath("$.name", is("Updated Province")))
				.andExpect(jsonPath("$.localData").exists())
				.andExpect(jsonPath("$.localData.itCodiceIstat", is("11")))
				.andExpect(jsonPath("$.localData.itSigla", is("22")))
				.andExpect(jsonPath("$.region").exists())
				.andExpect(jsonPath("$.region.uuid", is(venetoUuid.toString())))
				.andExpect(jsonPath("$.region.name", is("Veneto")));
	}

}
