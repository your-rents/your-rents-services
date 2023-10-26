package com.yourrents.services.geodata.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.CityRepository;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
public class CityControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CityRepository cityRepository;
    @Value("${yrs-geodata.api.basepath}")
    private String basePath;

    @Test
    void testGetAllCities() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", "2147483647"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(8020)))
                .andExpect(jsonPath("$.content[0].name", is("Abano Terme")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(8020)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(2147483647)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(8020)))
                .andExpect(jsonPath("$.empty", is(false)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    void testGetCitiesWithDefaultPagination() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.content[0].name", is("Abano Terme")))
                .andExpect(jsonPath("$.totalPages", is(401)))
                .andExpect(jsonPath("$.totalElements", is(8020)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(20)))
                .andExpect(jsonPath("$.empty", is(false)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    void testGetCitiesWithOrderNameDesc() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .param("sort", "name,DESC"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(20)))
                .andExpect(jsonPath("$.content[0].name", is("Vizzolo Predabissi")))
                .andExpect(jsonPath("$.totalPages", is(401)))
                .andExpect(jsonPath("$.totalElements", is(8020)))
                .andExpect(jsonPath("$.last", is(false)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(20)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(20)))
                .andExpect(jsonPath("$.empty", is(false)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    void testGetByUuid() throws Exception {
        City expected = cityRepository.findById(1).get();
        mvc.perform(get(basePath + "/cities/" + expected.uuid()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Abano Terme")))
                .andExpect(jsonPath("$.uuid", is(expected.uuid().toString())))
                .andExpect(jsonPath("$.localData").exists())
                .andExpect(jsonPath("$.province").exists())
                .andExpect(jsonPath("$.province.name", is("Padova")));
    }
}
