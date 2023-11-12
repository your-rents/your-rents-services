package com.yourrents.services.geodata.controller;

import static com.yourrents.services.geodata.jooq.Tables.PROVINCE;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.jooq.DSLContext;
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

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.CityRepository;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
@AutoConfigureMockMvc
@Transactional
class CityControllerTest {
    @Autowired
    private MockMvc mvc;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DSLContext dsl;
    @Value("${yrs-geodata.api.basepath}")
    private String basePath;

    @Test
    void testGetAllCities() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .param("page", "0")
                .param("size", Integer.toString(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(8020)))
                .andExpect(jsonPath("$.content[0].name", is("Abano Terme")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(8020)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(Integer.MAX_VALUE)))
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
    void testSearchCitiesByNameWithDefaultOperator() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .param("filter[name][value]", "Terme")
                .param("page", "0")
                .param("size", Integer.toString(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(42)))
                .andExpect(jsonPath("$.content[0].name", is("Abano Terme")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(42)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(Integer.MAX_VALUE)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(42)))
                .andExpect(jsonPath("$.empty", is(false)))
                .andExpect(jsonPath("$.sort.sorted", is(true)));
    }

    @Test
    void testSearchCityByProvinceName() throws Exception {
        mvc.perform(get(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .param("filter[province.name][value]", "Verona")
                .param("filter[province.name][operator]", "eq")
                .param("page", "0")
                .param("size", Integer.toString(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(96)))
                .andExpect(jsonPath("$.content[0].name", is("Affi")))
                .andExpect(jsonPath("$.content[0].province.name", is("Verona")))
                .andExpect(jsonPath("$.totalPages", is(1)))
                .andExpect(jsonPath("$.totalElements", is(96)))
                .andExpect(jsonPath("$.last", is(true)))
                .andExpect(jsonPath("$.first", is(true)))
                .andExpect(jsonPath("$.size", is(Integer.MAX_VALUE)))
                .andExpect(jsonPath("$.number", is(0)))
                .andExpect(jsonPath("$.numberOfElements", is(96)))
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

    @Test
    void testCreateNewCityInVeronaProvince() throws Exception {
        Page<City> cityInVeronaProvince = cityRepository.find(
                FilterCriteria.of(FilterCondition.of("province.name", "eq", "Verona")),
                PageRequest.ofSize(1));
        UUID veronaProvinceUuid = cityInVeronaProvince.getContent().get(0).province().uuid();

        mvc.perform(post(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "New City",
                            "province": {
                                "uuid": "%s"
                            },
                            "localData": {
                                "itCodiceIstat": "123456",
                                "itCodiceErariale": "1234"
                            }
                        }
                        """.formatted(veronaProvinceUuid)))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.name", is("New City")))
                .andExpect(jsonPath("$.localData").exists())
                .andExpect(jsonPath("$.localData.itCodiceIstat", is("123456")))
                .andExpect(jsonPath("$.localData.itCodiceErariale", is("1234")))
                .andExpect(jsonPath("$.province").exists())
                .andExpect(jsonPath("$.province.uuid", is(veronaProvinceUuid.toString())))
                .andExpect(jsonPath("$.province.name", is("Verona")));
    }

    @Test
    void testCreateNewCityWithoutProvinceAndLocalData() throws Exception {
        mvc.perform(post(basePath + "/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "New City"
                        }
                        """))
                .andExpect(status().isCreated())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid").isNotEmpty())
                .andExpect(jsonPath("$.name", is("New City")))
                .andExpect(jsonPath("$.localData").doesNotExist())
                .andExpect(jsonPath("$.province").doesNotExist());
    }

    @Test
    void testDeleteAnExistingCity() throws Exception {
        City city = cityRepository.findById(1).get();
        mvc.perform(delete(basePath + "/cities/" + city.uuid()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
        assertThat(cityRepository.findById(1).isPresent(), is(false));
    }

    @Test
    void testDeleteANotExistingCity() throws Exception {
        mvc.perform(delete(basePath + "/cities/" + UUID.randomUUID()).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateAnExistingCity() throws Exception {
        City city = cityRepository.findById(1).get();
        UUID newProvinceUuid = dsl.select(PROVINCE.EXTERNAL_ID)
                .from(PROVINCE)
                .where(PROVINCE.ID.eq(23)) // 23 is the id of the province of Verona
                .fetchAny(PROVINCE.EXTERNAL_ID);
        mvc.perform(patch(basePath + "/cities/" + city.uuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Updated City",
                            "province": {
                                "uuid": "%s"
                            },
                            "localData": {
                                "itCodiceIstat": "123456",
                                "itCodiceErariale": "1234"
                            }
                        }
                        """.formatted(newProvinceUuid)))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid", is(city.uuid().toString())))
                .andExpect(jsonPath("$.name", is("Updated City")))
                .andExpect(jsonPath("$.localData").exists())
                .andExpect(jsonPath("$.localData.itCodiceIstat", is("123456")))
                .andExpect(jsonPath("$.localData.itCodiceErariale", is("1234")))
                .andExpect(jsonPath("$.province").exists())
                .andExpect(jsonPath("$.province.uuid", is(newProvinceUuid.toString())))
                .andExpect(jsonPath("$.province.name", is("Verona")));
    }

    @Test
    void testUpdateANotExistingCity() throws Exception {
        mvc.perform(patch(basePath + "/cities/" + UUID.randomUUID())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Updated City",
                            "province": {
                                "uuid": "%s"
                            },
                            "localData": {
                                "itCodiceIstat": "123456",
                                "itCodiceErariale": "1234"
                            }
                        }
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isNotFound());
    }

    @Test
    void testUpdateACityWithANotExistingProvince() throws Exception {
        City city = cityRepository.findById(1).get();
        mvc.perform(patch(basePath + "/cities/" + city.uuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Updated City",
                            "province": {
                                "uuid": "%s"
                            },
                            "localData": {
                                "itCodiceIstat": "123456",
                                "itCodiceErariale": "1234"
                            }
                        }
                        """.formatted(UUID.randomUUID())))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateAnExistingCityWithoutProvinceAndLocalData() throws Exception {
        City city = cityRepository.findById(1).get();
        mvc.perform(patch(basePath + "/cities/" + city.uuid())
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "name": "Updated City"
                        }
                        """))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.uuid", is(city.uuid().toString())))
                .andExpect(jsonPath("$.name", is("Updated City")))
                .andExpect(jsonPath("$.localData").exists())
                .andExpect(jsonPath("$.localData.itCodiceIstat", is(city.localData().itCodiceIstat())))
                .andExpect(jsonPath("$.localData.itCodiceErariale", is(city.localData().itCodiceErariale())))
                .andExpect(jsonPath("$.province").exists())
                .andExpect(jsonPath("$.province.uuid", is(city.province().uuid().toString())))
                .andExpect(jsonPath("$.province.name", is(city.province().name())));
    }
}
