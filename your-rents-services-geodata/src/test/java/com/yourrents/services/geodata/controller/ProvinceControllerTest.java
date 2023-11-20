package com.yourrents.services.geodata.controller;

import static com.yourrents.services.geodata.util.search.PaginationUtils.numOfPages;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.repository.ProvinceRepository;
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
class ProvinceControllerTest {
	static final int NUM_PROVINCES = 107;
	final static String PROVINCE_URL = "/provinces";
	@Autowired
	MockMvc mvc;
	@Autowired
	ProvinceRepository provinceRepository;
	@Value("${yrs-geodata.api.basepath}")
	String basePath;

	@Test
	void getAllProvinces() throws Exception {
		mvc.perform(get(basePath + PROVINCE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.param("page", "0")
						.param("size", "2147483647"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(NUM_PROVINCES)))
				.andExpect(jsonPath("$.content[0].name", is("Agrigento")))
				.andExpect(jsonPath("$.content[0].region.name", is("Sicilia")))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.totalElements", is(NUM_PROVINCES)))
				.andExpect(jsonPath("$.last", is(true)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(2147483647)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(NUM_PROVINCES)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void getProvincesWithDefaultPagination() throws Exception {
		mvc.perform(get(basePath + PROVINCE_URL)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
				.andExpect(jsonPath("$.content[0].name", is("Agrigento")))
				.andExpect(jsonPath("$.totalPages", is(numOfPages(NUM_PROVINCES, 20))))
				.andExpect(jsonPath("$.totalElements", is(NUM_PROVINCES)))
				.andExpect(jsonPath("$.last", is(false)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(20)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(20)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void getProvincesWithOrderNameDesc() throws Exception {
		mvc.perform(get(basePath + PROVINCE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.param("sort", "name,DESC"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(20)))
				.andExpect(jsonPath("$.content[0].name", is("Viterbo")))
				.andExpect(jsonPath("$.totalPages", is(numOfPages(NUM_PROVINCES, 20))))
				.andExpect(jsonPath("$.totalElements", is(NUM_PROVINCES)))
				.andExpect(jsonPath("$.last", is(false)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(20)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(20)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void searchProvincesByNameWithDefaultOperator() throws Exception {
		mvc.perform(get(basePath + PROVINCE_URL)
						.contentType(MediaType.APPLICATION_JSON)
						.param("filter.name.value", "Venezia")
						.param("page", "0")
						.param("size", Integer.toString(Integer.MAX_VALUE)))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content", hasSize(1)))
				.andExpect(jsonPath("$.content[0].name", is("Venezia")))
				.andExpect(jsonPath("$.totalPages", is(1)))
				.andExpect(jsonPath("$.totalElements", is(1)))
				.andExpect(jsonPath("$.last", is(true)))
				.andExpect(jsonPath("$.first", is(true)))
				.andExpect(jsonPath("$.size", is(Integer.MAX_VALUE)))
				.andExpect(jsonPath("$.number", is(0)))
				.andExpect(jsonPath("$.numberOfElements", is(1)))
				.andExpect(jsonPath("$.empty", is(false)))
				.andExpect(jsonPath("$.sort.sorted", is(true)));
	}

	@Test
	void getByUuid() throws Exception {
		Province expected = provinceRepository.findById(1)
				.orElseThrow(RuntimeException::new);
		mvc.perform(get(basePath + PROVINCE_URL + "/" + expected.uuid()).contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name", is("Torino")))
				.andExpect(jsonPath("$.uuid", is(expected.uuid().toString())))
				.andExpect(jsonPath("$.localData.itCodiceIstat", is("1")))
				.andExpect(jsonPath("$.localData.itSigla", is("TO")))
				.andExpect(jsonPath("$.region").exists())
				.andExpect(jsonPath("$.region.name", is("Piemonte")))
				.andExpect(jsonPath("$.region.uuid").exists());
	}
}