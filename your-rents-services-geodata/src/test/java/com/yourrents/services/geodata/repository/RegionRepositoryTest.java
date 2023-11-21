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
import com.yourrents.services.geodata.model.Region;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@SpringBootTest
@Import(TestYourRentsGeoDataServiceApplication.class)
class RegionRepositoryTest {

	static final int NUM_REGIONS = 20;
	static final int PAGE_SIZE = 5;

	@Autowired
	RegionRepository regionRepository;

	@Test
	void findAll() {
		Page<Region> result = regionRepository.find(FilterCriteria.of(),
				PageRequest.ofSize(Integer.MAX_VALUE));
		assertThat(result, iterableWithSize(NUM_REGIONS));
	}

	@Test
	void findFirstPageWithOrderByNameAsc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Order.asc("name")));
		Page<Region> page = regionRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(PAGE_SIZE));
		assertThat(page.getContent().get(0).name(), equalTo("Abruzzo"));
		assertThat(page.getContent().get(0).country().localName(), equalTo("Italy"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).name(), equalTo("Emilia Romagna"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).country().localName(), equalTo("Italy"));
	}

	@Test
	void findFirstPageWithOrderByNameDesc() {
		Pageable pageable = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Order.desc("name")));
		Page<Region> page = regionRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(PAGE_SIZE));
		assertThat(page.getContent().get(0).name(), equalTo("Veneto"));
		assertThat(page.getContent().get(PAGE_SIZE - 1).name(), equalTo("Toscana"));
	}

	@Test
	void findLastPageWithOrderByNameAsc() {
		final int lastPage = lastPage(NUM_REGIONS, PAGE_SIZE);
		final int numRecordsForPage = numRecordsInPage(NUM_REGIONS, PAGE_SIZE, lastPage);
		Pageable pageable = PageRequest.of(lastPage, PAGE_SIZE, Sort.by(Sort.Order.asc("name")));
		Page<Region> page = regionRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(numRecordsForPage));
		if (numRecordsForPage > 0) {
			assertThat(page.getContent().get(0).name(), equalTo("Toscana"));
		}
	}

	@Test
	void findFilteredByNameEquals() {
		FilterCriteria filter = FilterCriteria.of(FilterCondition.of("name", "eq", "Veneto"));
		Page<Region> result = regionRepository.find(filter, PageRequest.ofSize(Integer.MAX_VALUE));
		assertThat(result, iterableWithSize(1));
		assertThat(result.getContent().get(0).name(), equalTo("Veneto"));
	}

	@Test
	void findFilteredByNameContainsIgnoreCaseWithOrderByNameDesc() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE, Sort.by(Sort.Order.desc("name")));
		FilterCriteria filter = FilterCriteria.of(
				FilterCondition.of("name", "containsIgnoreCase", "IA"));
		Page<Region> page = regionRepository.find(filter, pageable);
		assertThat(page, iterableWithSize(9));
		assertThat(page.getContent().get(0).name(), equalTo("Umbria"));
	}

	@Test
	void findAllWithOrderByCountryLocalName() {
		Pageable pageable = PageRequest.of(0, Integer.MAX_VALUE,
				Sort.by(Sort.Order.asc("country.localName"), Sort.Order.desc("name")));
		Page<Region> page = regionRepository.find(FilterCriteria.of(), pageable);
		assertThat(page, iterableWithSize(NUM_REGIONS));
		assertThat(page.getContent().get(0).country().localName(), equalTo("Italy"));
	}

	@Test
	void findByExternalId() {
		Region expected = regionRepository.findById(1).orElseThrow(RuntimeException::new);
		Region region = regionRepository.findByExternalId(expected.uuid())
				.orElseThrow(RuntimeException::new);

		assertThat(region, notNullValue());
		assertThat(region.uuid(), equalTo(expected.uuid()));
		assertThat(region.name(), equalTo("Piemonte"));
		assertThat(region.localData().itCodiceIstat(), equalTo("1"));
		assertThat(region.country().localName(), equalTo("Italy"));
	}
}
