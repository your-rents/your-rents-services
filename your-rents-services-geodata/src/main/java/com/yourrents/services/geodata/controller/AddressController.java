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

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.repository.AddressRepository;
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
@RequestMapping("${yrs-geodata.api.basepath}/addresses")
class AddressController {

  private final AddressRepository addressRepository;

  AddressController(AddressRepository addressRepository) {
    this.addressRepository = addressRepository;
  }

  @GetMapping
  ResponseEntity<Page<Address>> getAddresses(
      @ParameterObject Searchable filter,
      @ParameterObject @SortDefault(sort = "city.name", direction = Direction.ASC) Pageable pagination) {
    Page<Address> page = addressRepository.find(filter, pagination);
    return ResponseEntity.ok(page);
  }

  @GetMapping("/{uuid}")
  ResponseEntity<Address> getByUuid(@PathVariable UUID uuid) {
    Address address = addressRepository.findByExternalId(uuid)
        .orElseThrow(
            () -> new DataNotFoundException("can't find address having uuid: " + uuid));
    return ResponseEntity.ok(address);
  }

  @PostMapping
  ResponseEntity<Address> create(@RequestBody Address address) {
    Address result = addressRepository.create(address);
    return new ResponseEntity<>(result, HttpStatus.CREATED);
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  void delete(@PathVariable UUID uuid) {
    addressRepository.delete(uuid);
  }

  @PatchMapping("/{uuid}")
  ResponseEntity<Address> update(@PathVariable UUID uuid,
      @RequestBody Address address) {
    Address result = addressRepository.update(uuid, address);
    return ResponseEntity.ok(result);
  }
}

