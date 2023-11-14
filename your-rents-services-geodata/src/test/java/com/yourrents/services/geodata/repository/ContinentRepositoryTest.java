package com.yourrents.services.geodata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Continent;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest()
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
class ContinentRepositoryTest {

	static final int NUM_CONTINENTS = 7;

	@Autowired
	ContinentRepository continentRepository;

	@Test
	void findByExternalId() {
		Continent expected = continentRepository.findById(1).orElseThrow(RuntimeException::new);
		Continent continent = continentRepository.findByExternalId(expected.uuid())
				.orElseThrow(RuntimeException::new);
		assertThat(continent.name()).isEqualTo("Africa");
		assertThat(continent.code()).isEqualTo("AF");
	}

	@Test
	void findAll() {
		List<Continent> list = continentRepository.findAll();
		assertThat(list).isNotNull();
		assertThat(list).hasSize(NUM_CONTINENTS);
		assertThat(list.get(0).name()).isEqualTo("Africa");
		assertThat(list.get(NUM_CONTINENTS - 1).name()).isEqualTo("South America");

	}
}