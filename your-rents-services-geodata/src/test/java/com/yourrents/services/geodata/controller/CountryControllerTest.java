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

import static com.yourrents.services.geodata.util.search.PaginationUtils.numOfPages;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.repository.CountryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
class CountryControllerTest {

	static final int NUM_COUNTRIES = 246;

	final static String COUNTRY_URL = "/countries";
	@Autowired
	private MockMvc mvc;
	@Autowired
	private CountryRepository countryRepository;
	@Value("${yrs-geodata.api.basepath}")
	private String basePath;
	@Test
	void getAllCountries() throws Exception {
		mvc.perform(get(basePath + COUNTRY_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.param("page", "0")
						.param("size", "2147483647"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(NUM_COUNTRIES)))
				.andExpect(jsonPath("$.content[0].localName", is("Afghanistan")))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.totalElements", is(NUM_COUNTRIES)))
				.andExpect(jsonPath("$.last", is(true)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(2147483647)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(NUM_COUNTRIES)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void getAllRegionsWithDefaultPagination() throws Exception {
		mvc.perform(get(basePath + COUNTRY_URL)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
				.andExpect(jsonPath("$.content[0].localName", is("Afghanistan")))
				.andExpect(jsonPath("$.content[19].localName", is("Barbados")))
				.andExpect(jsonPath("$.totalPages", is(numOfPages(NUM_COUNTRIES, 20))))
				.andExpect(jsonPath("$.totalElements", is(NUM_COUNTRIES)))
				.andExpect(jsonPath("$.last", is(false)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(20)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(20)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void getByUuid() throws Exception {
		Country expected = countryRepository.findById(1)
				.orElseThrow(RuntimeException::new);
		mvc.perform(get(basePath + COUNTRY_URL + "/" +
						expected.uuid()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.uuid", is(expected.uuid().toString())))
				.andExpect(jsonPath("$.isoCode", is("AD")))
				.andExpect(jsonPath("$.englishFullName", is("Principality of Andorra")))
				.andExpect(jsonPath("$.iso3", is("AND")))
				.andExpect(jsonPath("$.localName", is("Andorra")))
				.andExpect(jsonPath("$.number", is(20)))
				.andExpect(jsonPath("$.continent.name", is("Europe")));
	}


}
