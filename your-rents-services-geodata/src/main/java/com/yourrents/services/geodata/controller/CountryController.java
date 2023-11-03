package com.yourrents.services.geodata.controller;

import com.yourrents.services.geodata.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.repository.CountryRepository;
import java.util.UUID;
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

}