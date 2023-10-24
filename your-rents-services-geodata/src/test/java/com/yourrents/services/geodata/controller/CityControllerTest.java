package com.yourrents.services.geodata.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;

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
    void testFindAll() throws Exception {
        mvc.perform(get(basePath + "/cities").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(8020)))
                .andExpect(jsonPath("$[0].name", is("Abano Terme")));
    }

    @Test
    void testFindByUuid() throws Exception {
        City expected = cityRepository.findById(1).get();
        mvc.perform(get(basePath + "/cities/"+expected.uuid()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name", is("Abano Terme")))
                .andExpect(jsonPath("$.uuid", is(expected.uuid().toString())))
                .andExpect(jsonPath("$.localData").exists())
                .andExpect(jsonPath("$.province").exists())
                .andExpect(jsonPath("$.province.name", is("Padova")));
    }
}
