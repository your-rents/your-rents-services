package com.yourrents.services.geodata.repository;

/*-
 * #%L
 * YourRents GeoData Service
 * %%
 * Copyright (C) 2023 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import static com.yourrents.services.geodata.util.search.PaginationUtils.lastPage;
import static com.yourrents.services.geodata.util.search.PaginationUtils.numRecordsInPage;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.iterableWithSize;
import static org.hamcrest.Matchers.notNullValue;

import com.yourrents.services.common.searchable.FilterCondition;
import com.yourrents.services.common.searchable.FilterCriteria;
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

@SpringBootTest()
@Import(TestYourRentsGeoDataServiceApplication.class)
class CountryRepositoryTest {

	static final int NUM_COUNTRIES = 246;
	static final int PAGE_SIZE = 5;

	@Autowired
	CountryRepository countryRepository;

	@Test
	void findAll() {
		Page<Country> result = countryRepository.find(FilterCriteria.of(),
				PageRequest.ofSize(Integer.MAX_VALUE));
		assertThat(result, iterableWithSize(NUM_COUNTRIES));
	}


	@Test
	void findFirstPageWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.asc("localName")));
		Page<Country> page = countryRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(PAGE_SIZE));
		assertThat(page.getContent().get(0).localName(), equalTo("Afghanistan"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).localName(), equalTo("American Samoa"));
	}

	@Test
	void findFirstPageWithOrderByNameDesc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.desc("localName")));
		Page<Country> page = countryRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(PAGE_SIZE));
		assertThat(page.getContent().get(0).localName(), equalTo("Zimbabwe"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).localName(),
				equalTo("Wallis and Futuna Islands"));
	}

	@Test
	void findLastPageWithOrderByNameAsc() {
		final int lastPage = lastPage(NUM_COUNTRIES, PAGE_SIZE);
		final int numRecordsForPage = numRecordsInPage(NUM_COUNTRIES, PAGE_SIZE, lastPage);
		Pageable pageable = PageRequest.of(lastPage, PAGE_SIZE, Sort.by(Order.asc("localName")));
		Page<Country> page = countryRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(numRecordsForPage));
		if (numRecordsForPage > 0) {
			assertThat(page.getContent().get(0).localName(), equalTo("Zimbabwe"));
		}
	}

	@Test
	void findFilteredByNameEqualsWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Order.asc("localName")));
		FilterCriteria filter = FilterCriteria.of(FilterCondition.of("localName", "eq", "Brazil"));
		Page<Country> page = countryRepository.find(filter, pageable);
		assertThat(page, iterableWithSize(1));
		assertThat(page.getContent().get(0).localName(), equalTo("Brazil"));
	}

	@Test
	void findFilteredByNameContainsIgnoreCaseWithOrderByNameDesc() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Order.desc("localName")));
		FilterCriteria filter = FilterCriteria.of(
				FilterCondition.of("localName", "containsIgnoreCase", "AS"));
		Page<Country> page = countryRepository.find(filter, pageable);
		assertThat(page, iterableWithSize(6));
		assertThat(page.getContent().get(0).localName(), equalTo("Madagascar"));
	}

	@Test
	void findByExternalId() {
		Country expected = countryRepository.findById(1).orElseThrow(RuntimeException::new);
		Country country = countryRepository.findByExternalId(expected.uuid())
				.orElseThrow(RuntimeException::new);
		assertThat(country, notNullValue());
		assertThat(country.uuid(), equalTo(expected.uuid()));
		assertThat(country.isoCode(), equalTo("AD"));
		assertThat(country.englishFullName(), equalTo("Principality of Andorra"));
		assertThat(country.iso3(), equalTo("AND"));
		assertThat(country.localName(), equalTo("Andorra"));
		assertThat(country.number(), equalTo(20));
		assertThat(country.continent().name(), equalTo("Europe"));
	}

}
