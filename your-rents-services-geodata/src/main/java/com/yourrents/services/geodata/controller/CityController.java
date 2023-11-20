package com.yourrents.services.geodata.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.yourrents.services.common.searchable.Searchable;
import com.yourrents.services.common.searchable.annotation.SearchableDefault;
import com.yourrents.services.common.searchable.annotation.SearchableField;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.CityRepository;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/cities")
class CityController {
    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public Page<City> getCities(
            @ParameterObject @SearchableDefault({ @SearchableField(name = "uuid"), @SearchableField("name"),
                    @SearchableField("province.name") }) Searchable filter,
            @ParameterObject @SortDefault(sort = "name", direction = Direction.ASC) Pageable pagination) {
        return cityRepository.find(filter, pagination);
    }

    @GetMapping("/{uuid}")
    public City getByUuid(@PathVariable UUID uuid) {
        return cityRepository.findByExternalId(uuid).get();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public City create(@RequestBody City city) {
        return cityRepository.create(city);
    }

    @PatchMapping("/{uuid}")
    public City update(@PathVariable UUID uuid, @RequestBody City city) {
        return cityRepository.update(uuid, city);
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID uuid) {
        cityRepository.delete(uuid);
    }
}