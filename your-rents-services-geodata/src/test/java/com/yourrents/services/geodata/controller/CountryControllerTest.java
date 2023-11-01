package com.yourrents.services.geodata.controller;

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
	@Autowired
	private MockMvc mvc;
	@Autowired
	private CountryRepository countryRepository;
	@Value("${yrs-geodata.api.basepath}")
	private String basePath;

	@Test
	void testGetByUuid() throws Exception {
		Country expected = countryRepository.findById(1)
				.orElseThrow(RuntimeException::new);
		mvc.perform(get(basePath + "/countries/" +
						expected.uuid()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.isoCode", is("AD")))
				.andExpect(jsonPath("$.englishFullName", is("Principality of Andorra")))
				.andExpect(jsonPath("$.iso3", is("AND")))
				.andExpect(jsonPath("$.localName", is("Andorra")))
				.andExpect(jsonPath("$.number", is(20)))
				.andExpect(jsonPath("$.uuid", is(expected.uuid().toString())));
	}

}