package com.yourrents.services.geodata.controller;

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.repository.CountryRepository;
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

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/countries")
class CountryController {

    private final CountryRepository countryRepository;

	CountryController(CountryRepository countryRepository) {
		this.countryRepository = countryRepository;
	}

	@GetMapping("/{uuid}")
    public ResponseEntity<Country> getByUuid(@PathVariable UUID uuid) {
		Country country = countryRepository.findByExternalId(uuid)
				.orElseThrow(
						() -> new DataNotFoundException("can't find country having uuid: " + uuid));
		return ResponseEntity.ok(country);
	}

	@GetMapping
	public Page<Country> getCountries(
			@ParameterObject @SortDefault(sort = "number", direction = Direction.ASC) Pageable pagination) {
		return countryRepository.find(pagination);
	}


}