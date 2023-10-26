package com.yourrents.services.geodata.repository;

import static org.hamcrest.MatcherAssert.*; 
import static org.hamcrest.Matchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.City;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    void testFindAll() {
        Page<City> result = cityRepository.find(PageRequest.ofSize(Integer.MAX_VALUE));
        assertThat(result, iterableWithSize(8020));
    }

    @Test
    void testFindFirstPageWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Abano Terme"));
        assertThat(result.getContent().get(9).name(), equalTo("Acate"));
    }

    @Test
    void testFindFirstPageWithOrderByNameDesc() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.desc("name")));
        Page<City> result = cityRepository.find(pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Vizzolo Predabissi"));
        assertThat(result.getContent().get(9).name(), equalTo("Vittuone"));
    }

    @Test
    void testFindLastPageWithOrderByNameAsc() {
        Pageable pageable = PageRequest.of(801, 10, Sort.by(Order.asc("name")));
        Page<City> result = cityRepository.find(pageable);
        assertThat(result, iterableWithSize(10));
        assertThat(result.getContent().get(0).name(), equalTo("Vittuone"));
        assertThat(result.getContent().get(9).name(), equalTo("Vizzolo Predabissi"));
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
}
