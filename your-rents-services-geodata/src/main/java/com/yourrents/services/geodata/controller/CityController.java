package com.yourrents.services.geodata.controller;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.SortDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.CityRepository;
import com.yourrents.services.geodata.util.search.Searchable;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/cities")
class CityController {
    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public Page<City> getCities(
            Searchable filter,
            @ParameterObject @SortDefault(sort = "name", direction = Direction.ASC) Pageable pagination) {
        return cityRepository.find(filter, pagination);
    }

    @GetMapping("/{uuid}")
    public City getByUuid(@PathVariable UUID uuid) {
        return cityRepository.findByExternalId(uuid).get();
    }

}