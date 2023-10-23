package com.yourrents.services.geodata.repository;

import static org.hamcrest.MatcherAssert.*; 
import static org.hamcrest.Matchers.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.jooq.tables.records.CityRecord;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
public class CityRepositoryTest {

    @Autowired
    private CityRepository cityRepository;

    @Test
    public void testFindAll() {
        List<CityRecord> result = cityRepository.findAll();
        assertThat(result, hasSize(8020));
    }

    @Test
    public void testFindByExternalId() {
        CityRecord expected = cityRepository.findById(1);
        CityRecord result = cityRepository.findByExternalId(expected.getExternalId());
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(1));
        assertThat(result.getName(), equalTo("Abano Terme"));
    }
}
