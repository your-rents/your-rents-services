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

import static org.assertj.core.api.Assertions.assertThat;

import com.yourrents.services.geodata.TestYourRentsGeoDataServiceApplication;
import com.yourrents.services.geodata.model.Continent;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest()
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
