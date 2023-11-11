package com.yourrents.services.geodata.repository;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Province;
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

import static com.yourrents.services.geodata.util.search.PaginationUtils.lastPage;
import static com.yourrents.services.geodata.util.search.PaginationUtils.numRecordsInPage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestYourRentsGeoDataServiceApplication.class)
class ProvinceRepositoryTest {

	static final int NUM_PROVINCES = 107;
	static final int PAGE_SIZE = 5;

	@Autowired
	ProvinceRepository provinceRepository;

	@Test
	void testFindAll() {
		Page<Province> result = provinceRepository.find(FilterCriteria.of(),
				PageRequest.ofSize(Integer.MAX_VALUE));
		assertThat(result, iterableWithSize(NUM_PROVINCES));
	}

	@Test
	void testFindFirstPageWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.asc("name")));
		Page<Province> page = provinceRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(5));
		assertThat(page.getContent().get(0).name(), equalTo("Agrigento"));
		assertThat(page.getContent().get(0).region().name(), equalTo("Sicilia"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).name(), equalTo("Ascoli Piceno"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).region().name(), equalTo("Marche"));
	}

	@Test
	void testFindFirstPageWithOrderByNameDesc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.desc("name")));
		Page<Province> result = provinceRepository.find(FilterCriteria.of(), pageable);
		assertThat(result, iterableWithSize(PAGE_SIZE));
		assertThat(result.getContent().get(0).name(), equalTo("Viterbo"));
		assertThat(result.getContent().get(PAGE_SIZE - 1).name(), equalTo("Vercelli"));
	}

	@Test
	void testFindLastPageWithOrderByNameAsc() {
		final int lastPage = lastPage(NUM_PROVINCES, PAGE_SIZE);
		final int numRecordsForPage = numRecordsInPage(NUM_PROVINCES, PAGE_SIZE, lastPage);
		Pageable pageable = PageRequest.of(lastPage, PAGE_SIZE, Sort.by(Order.asc("name")));
		Page<Province> result = provinceRepository.find(FilterCriteria.of(), pageable);
		assertThat(result, iterableWithSize(numRecordsForPage));
		if (numRecordsForPage > 0) {
			assertThat(result.getContent().get(0).name(), equalTo("Vicenza"));
		}
	}

	@Test
	void testFindFilteredByNameEqualsWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.asc("name")));
		FilterCriteria filter = FilterCriteria.of(FilterCondition.of("name", "eq", "Venezia"));
		Page<Province> result = provinceRepository.find(filter, pageable);
		assertThat(result, iterableWithSize(1));
		assertThat(result.getContent().get(0).name(), equalTo("Venezia"));
	}

	@Test
	void testFindFilteredByNameContainsIgnoreCaseWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.desc("name")));
		FilterCriteria filter = FilterCriteria.of(
				FilterCondition.of("name", "containsIgnoreCase", "AV"));
		Page<Province> result = provinceRepository.find(filter, pageable);
		assertThat(result, iterableWithSize(4));
		assertThat(result.getContent().get(0).name(), equalTo("Savona"));
	}

	@Test
	void testFindByExternalId() {
		Province expected = provinceRepository.findById(1).orElseThrow(RuntimeException::new);
		Province province = provinceRepository.findByExternalId(expected.uuid())
				.orElseThrow(RuntimeException::new);

		assertThat(province, notNullValue());
		assertThat(province.uuid(), equalTo(expected.uuid()));
		assertThat(province.name(), equalTo("Torino"));
		assertThat(province.region().name(), equalTo("Piemonte"));
	}

	@Test
	void testFindAllWithOrderByRegionAscAndNameDesc() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE,
				Sort.by(Order.asc("region.name"), Order.desc("name")));

		Page<Province> result = provinceRepository.find(FilterCriteria.of(), pageable);
		assertThat(result, iterableWithSize(NUM_PROVINCES));
		assertThat(result.getContent().get(0).name(), equalTo("Teramo"));
		assertThat(result.getContent().get(0).region().name(), equalTo("Abruzzo"));
		assertThat(result.getContent().get(1).name(), equalTo("Pescara"));
	}


	@Test
	void findById() {
		Province expected = provinceRepository.findById(1).orElseThrow(RuntimeException::new);
		assertThat(expected, notNullValue());
	}

}