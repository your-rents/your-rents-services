package com.yourrents.services.geodata.model;

import java.util.UUID;

public record City(UUID uuid, String name, CityLocalData localData, Province province) {}