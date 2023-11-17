package com.yourrents.services.geodata.controller;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Province;
import com.yourrents.services.geodata.repository.ProvinceRepository;
import java.util.UUID;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/provinces")
class ProvinceController {

	private final ProvinceRepository provinceRepository;

	public ProvinceController(ProvinceRepository provinceRepository) {
		this.provinceRepository = provinceRepository;
	}

	@GetMapping
	public ResponseEntity<Page<Province>> getCities(
			Searchable filter,
			@ParameterObject @SortDefault(sort = "name", direction = Direction.ASC) Pageable pagination) {
		Page<Province> page = provinceRepository.find(filter, pagination);
		return ResponseEntity.ok(page);
	}

	@GetMapping("/{uuid}")
	public ResponseEntity<Province> getByUuid(@PathVariable UUID uuid) {
		Province province = provinceRepository.findByExternalId(uuid)
				.orElseThrow(
						() -> new DataNotFoundException("can't find province having uuid: " + uuid));
		return ResponseEntity.ok(province);
	}

	@PostMapping
	public ResponseEntity<Province> create(@RequestBody Province province) {
		Province result = provinceRepository.create(province);
		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@PatchMapping("/{uuid}")
	public ResponseEntity<Province> update(@PathVariable UUID uuid,
			@RequestBody Province province) {
		Province result = provinceRepository.update(uuid, province);
		return ResponseEntity.ok(result);
	}

	@DeleteMapping("/{uuid}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable UUID uuid) {
		provinceRepository.delete(uuid);
	}

}