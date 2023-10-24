package com.yourrents.services.geodata.controller;

import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.repository.CityRepository;

@RestController
@RequestMapping("${yrs-geodata.api.basepath}/cities")
public class CityController {
    @Autowired
    private CityRepository cityRepository;

    @GetMapping
    public Iterable<City> findAll() {
        return cityRepository.findAll();
    }

    @GetMapping("/{uuid}")
    public Optional<City> findByUuid(@PathVariable UUID uuid) {
        return cityRepository.findByExternalId(uuid);
    }
    
}