package com.yourrents.services.geodata.controller;

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Continent;
import com.yourrents.services.geodata.repository.ContinentRepository;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/continents")
class ContinentController {

	private final ContinentRepository continentRepository;

	ContinentController(ContinentRepository continentRepository) {
		this.continentRepository = continentRepository;
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<Continent> getByUuid(@PathVariable UUID uuid) {
		Continent continent = continentRepository.findByExternalId(uuid)
				.orElseThrow(
						() -> new DataNotFoundException(
								"can't find continent having uuid: " + uuid));
		return ResponseEntity.ok(continent);
	}

}