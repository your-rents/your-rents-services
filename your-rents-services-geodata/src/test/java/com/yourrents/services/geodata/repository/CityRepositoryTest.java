package com.yourrents.services.geodata.repository;

import static org.hamcrest.MatcherAssert.*; 
import static org.hamcrest.Matchers.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.City;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    public void testFindAll() {
        Iterable<City> result = cityRepository.findAll();
        assertThat(result, iterableWithSize(8020));
    }

    @Test
    public void testFindByExternalId() {
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
