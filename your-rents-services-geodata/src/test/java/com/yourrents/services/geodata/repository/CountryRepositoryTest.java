package com.yourrents.services.geodata.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Country;
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

@SpringBootTest()
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
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

	@Test
	void testFindFirstPageWithOrderByNumberDesc() {
		Pageable pageable = PageRequest.of(0, 10, Sort.by(Order.desc("number")));
		Page<Country> result = countryRepository.find(pageable);
		assertThat(result.getContent().get(0).localName()).isEqualTo("Zambia");
		assertThat(result.getContent().get(0).number()).isEqualTo(894);
	}
}