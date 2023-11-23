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
import com.yourrents.services.geodata.model.Continent;
import com.yourrents.services.geodata.repository.ContinentRepository;
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
class ContinentControllerTest {

	static final int NUM_CONTINENTS = 7;

	final static String CONTINENT_URL = "/continents";

	@Autowired
	private MockMvc mvc;
	@Autowired
	private ContinentRepository continentRepository;
	@Value("${yrs-geodata.api.basepath}")
	private String basePath;


	@Test
	void getByUuid() throws Exception {
		Continent expected = continentRepository.findById(1)
				.orElseThrow(RuntimeException::new);
		mvc.perform(get(basePath + CONTINENT_URL + "/" +
						expected.uuid()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.code", is("AF")))
				.andExpect(jsonPath("$.name", is("Africa")))
				.andExpect(jsonPath("$.uuid", is(expected.uuid().toString())));
	}

	@Test
	void getAll() throws Exception {
		mvc.perform(get(basePath + CONTINENT_URL).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$", hasSize(NUM_CONTINENTS)))
				.andExpect(jsonPath("$[0].name", is("Africa")))
				.andExpect(jsonPath("$[6].name", is("South America")));

	}

}
