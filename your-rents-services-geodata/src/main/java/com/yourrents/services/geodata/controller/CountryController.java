package com.yourrents.services.geodata.controller;

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

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.repository.CountryRepository;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/countries")
class CountryController {

	private final CountryRepository countryRepository;

	CountryController(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	@GetMapping
	public ResponseEntity<Page<Country>> getCountries(
			@ParameterObject @SearchableDefault({ @SearchableField(name = "uuid"), @SearchableField("isoCode"),
					@SearchableField("englishFullName"), @SearchableField("iso3"), @SearchableField("localName"),
					@SearchableField("number") }) Searchable filter,
			@ParameterObject @SortDefault(sort = "localName", direction = Direction.ASC) Pageable pagination) {
		Page<Country> page = countryRepository.find(filter, pagination);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<Country> getByUuid(@PathVariable UUID uuid) {
		Country country = countryRepository.findByExternalId(uuid)
				.orElseThrow(
						() -> new DataNotFoundException("can't find country having uuid: " + uuid));
		return ResponseEntity.ok(country);
	}

}
