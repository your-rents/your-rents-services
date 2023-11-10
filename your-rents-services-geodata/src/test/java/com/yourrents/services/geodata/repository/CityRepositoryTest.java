package com.yourrents.services.geodata.repository;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.CityLocalData;
import com.yourrents.services.geodata.util.search.FilterCondition;
import com.yourrents.services.geodata.util.search.FilterCriteria;
import com.yourrents.services.geodata.util.search.Searchable;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
@Transactional
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testFindAll() {
        Page<City> result = cityRepository.find(FilterCriteria.of(), PageRequest.ofSize(Integer.MAX_VALUE));
        assertThat(result, iterableWithSize(8020));
    }

    @Test
    void testFindFirstPageWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(FilterCriteria.of(), pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Abano Terme"));
        assertThat(result.getContent().get(9).name(), equalTo("Acate"));
    }

    @Test
    void testFindFirstPageWithOrderByNameDesc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.desc("name")));
        Page<City> result = cityRepository.find(FilterCriteria.of(), pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Vizzolo Predabissi"));
        assertThat(result.getContent().get(9).name(), equalTo("Vittuone"));
    }

    @Test
    void testFindLastPageWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(801, 10, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(FilterCriteria.of(), pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Vittuone"));
        assertThat(result.getContent().get(9).name(), equalTo("Vizzolo Predabissi"));
    }

    @Test
    void testFindFilteredByNameEqualsWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("name")));
        FilterCriteria filter = FilterCriteria.of(FilterCondition.of("name", "eq", "Abano Terme"));
        Page<City> result = cityRepository.find(filter, pageable);
        assertThat(result, iterableWithSize(1));
        assertThat(result.getContent().get(0).name(), equalTo("Abano Terme"));
    }

    @Test
    void testFindFilteredByNameContainsIgnoreCaseWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc("name")));
        FilterCriteria filter = FilterCriteria.of(FilterCondition.of("name", "containsIgnoreCase", "Terme"));
        Page<City> result = cityRepository.find(filter, pageable);
        assertThat(result, iterableWithSize(42));
        assertThat(result.getContent().get(0).name(), equalTo("Abano Terme"));
    }

    @Test
    void testFindByExternalId() {
        City expected = cityRepository.findById(1).get();
        Optional<City> optResult = cityRepository.findByExternalId(expected.uuid());
        assertThat(optResult.isPresent(), equalTo(true));
        City result = optResult.get();
        assertThat(result, notNullValue());
        assertThat(result.uuid(), equalTo(expected.uuid()));
        assertThat(result.name(), equalTo("Abano Terme"));
        assertThat(result.localData(), notNullValue());
        assertThat(result.localData().itCodiceIstat(), equalTo("28001"));
        assertThat(result.localData().itCodiceErariale(), equalTo("A001"));
        assertThat(result.province(), notNullValue());
        assertThat(result.province().name(), equalTo("Padova"));
    }

    @Test
    void testFindFirstPageWithOrderByUUIDAsc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("uuid")));
        Page<City> result = cityRepository.find(FilterCriteria.of(), pageable);
        assertThat(result, iterableWithSize(10));
    }

    @Test
    void testFindByExternalIdUsingStartsWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc("name")));
        FilterCriteria filter = FilterCriteria.of(FilterCondition.of("uuid", "startsWith", "0"));
        Page<City> result = cityRepository.find(filter, pageable);
        for (City city : result) {
            assertThat(city.uuid().toString().startsWith("0"), equalTo(true));
        }
    }

    @Test
    void testFindAllCitiesWithOrderByProvinceNameAscAndCityNameAsc() {
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE,
                Sort.by(Order.asc("province.name"), Order.asc("name")));
        Page<City> result = cityRepository.find(FilterCriteria.of(), pageable);
        assertThat(result, iterableWithSize(8020));
        assertThat(result.getContent().get(0).name(), equalTo("Agrigento"));
        assertThat(result.getContent().get(0).province().name(), equalTo("Agrigento"));
    }

    @Test
    void testFindCitiesByProvinceNameWithOrderByCityNameAsc() {
        Searchable filter = FilterCriteria.of(FilterCondition.of("province.name", "eq", "Verona"));
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(filter, pageable);
        assertThat(result, iterableWithSize(96));
        assertThat(result.getContent().get(0).name(), equalTo("Affi"));
        assertThat(result.getContent().get(0).province().name(), equalTo("Verona"));
    }

    @Test
    void testFindCitiesByProvinceUuidWithOrderByCityNameAsc() {
        Searchable filterForVerona = FilterCriteria.of(FilterCondition.of("province.name", "eq", "Verona"));
        Page<City> cityInVeronaProvince = cityRepository.find(filterForVerona, PageRequest.ofSize(1));
        UUID veronaUuid = cityInVeronaProvince.getContent().get(0).province().uuid();

        Searchable filter = FilterCriteria.of(FilterCondition.of("province.uuid", "eq", veronaUuid.toString()));
        Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(filter, pageable);
        assertThat(result, iterableWithSize(96));
        assertThat(result.getContent().get(0).name(), equalTo("Affi"));
        assertThat(result.getContent().get(0).province().name(), equalTo("Verona"));
    }

    @Test
    void testCreateNewCityInVeronaProvince() {
        Searchable filterForVerona = FilterCriteria.of(FilterCondition.of("province.name", "eq", "Verona"));
        Page<City> cityInVeronaProvince = cityRepository.find(filterForVerona, PageRequest.ofSize(1));
        UUID veronaUuid = cityInVeronaProvince.getContent().get(0).province().uuid();

        City newCity = new City(null, "New City", new CityLocalData("123456", "1234"),
                new City.Province(veronaUuid, "Not Important"));
        City result = cityRepository.create(newCity);
        assertThat(result, notNullValue());
        assertThat(result.uuid(), notNullValue());
        assertThat(result.name(), equalTo("New City"));
        assertThat(result.localData().itCodiceIstat(), equalTo("123456"));
        assertThat(result.localData().itCodiceErariale(), equalTo("1234"));
        assertThat(result.province().uuid(), equalTo(veronaUuid));
        assertThat(result.province().name(), equalTo("Verona"));
    }

    @Test
    void testCreateNewCityWithoutProvinceAndLocalData() {
        City newCity = new City(null, "New City", null, null);
        City result = cityRepository.create(newCity);
        assertThat(result, notNullValue());
        assertThat(result.uuid(), notNullValue());
        assertThat(result.name(), equalTo("New City"));
        assertThat(result.localData(), nullValue());
        assertThat(result.province(), nullValue());
    }

    @Test
    void testDeleteAnExistingCity() {
        City city = cityRepository.findById(1).get();
        boolean deleted = cityRepository.delete(city.uuid());
        assertThat(deleted, equalTo(true));
        Optional<City> optResult = cityRepository.findById(1);
        assertThat(optResult.isPresent(), equalTo(false));
    }

    @Test
    void testDeleteANonExistingCity() {
        UUID randomUUID = UUID.randomUUID();
        DataNotFoundException ex = assertThrows(DataNotFoundException.class, () -> cityRepository.delete(randomUUID));
        assertThat(ex.getMessage(), equalTo("City not found: " + randomUUID));
    }
}
