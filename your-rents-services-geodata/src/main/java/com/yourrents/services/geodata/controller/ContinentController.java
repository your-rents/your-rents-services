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

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Continent;
import com.yourrents.services.geodata.repository.ContinentRepository;
import java.util.List;
import java.util.UUID;
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

  @GetMapping()
  ResponseEntity<List<Continent>> getAll() {
    List<Continent> list = continentRepository.findAll();
    return ResponseEntity.ok(list);
  }

  @PostMapping
  ResponseEntity<Continent> create(@RequestBody Continent continent) {
    Continent result = continentRepository.create(continent);
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @PatchMapping("/{uuid}")
  ResponseEntity<Continent> update(@PathVariable UUID uuid,
      @RequestBody Continent continent) {
    Continent result = continentRepository.update(uuid, continent);
    return ResponseEntity.ok(result);
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID uuid) {
    continentRepository.delete(uuid);
  }

}
