package com.yourrents.services.geodata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Country;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
@TestPropertySource(properties = "spring.config.additional-location=classpath:application-test.properties")
class CountryRepositoryTest {

	@Autowired
	CountryRepository countryRepository;

	@Test
	void findById() {
		Country country = countryRepository.findById(5).orElseThrow(RuntimeException::new);
		assertThat(country.englishFullName()).isEqualTo("Anguilla");
	}

	@Test
	void testFindByExternalId() {
		Country expected = countryRepository.findById(43)
				.orElseThrow(RuntimeException::new);
		Country switzerland = countryRepository.findByExternalId(expected.uuid())
				.orElseThrow(RuntimeException::new);
		assertThat(switzerland.englishFullName()).isEqualTo("Swiss Confederation");
		assertThat(switzerland.isoCode()).isEqualTo("CH");
		assertThat(switzerland.localName()).isEqualTo("Switzerland");
	}
}