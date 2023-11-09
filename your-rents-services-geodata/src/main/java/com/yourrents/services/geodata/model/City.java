package com.yourrents.services.geodata.model;

import java.util.UUID;

public record City(UUID uuid, String name, CityLocalData localData, City.Province province) {

    public record Province(UUID uuid, String name) {       
    }
}