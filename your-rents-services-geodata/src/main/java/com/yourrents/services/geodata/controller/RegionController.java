package com.yourrents.services.geodata.controller;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Region;
import com.yourrents.services.geodata.repository.RegionRepository;
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
@RequestMapping("${yrs-geodata.api.basepath}/regions")
class RegionController {

	private final RegionRepository regionRepository;

	RegionController(RegionRepository regionRepository) {
		this.regionRepository = regionRepository;
	}

	@GetMapping
	public ResponseEntity<Page<Region>> getRegions(
			Searchable filter,
			@ParameterObject @SortDefault(sort = "name", direction = Direction.ASC) Pageable pagination) {
		Page<Region> page = regionRepository.find(filter, pagination);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<Region> getByUuid(@PathVariable UUID uuid) {
		Region region = regionRepository.findByExternalId(uuid)
				.orElseThrow(
						() -> new DataNotFoundException("can't find region having uuid: " + uuid));
		return ResponseEntity.ok(region);
	}

}