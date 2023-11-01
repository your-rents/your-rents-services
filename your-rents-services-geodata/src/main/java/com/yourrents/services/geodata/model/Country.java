package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Country(String isoCode, String englishFullName, String iso3, String localName, Integer number, UUID uuid) {
}