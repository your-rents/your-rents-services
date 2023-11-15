package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Country(UUID uuid, String isoCode, String englishFullName, String iso3, String localName,
					  Integer number, Country.Continent continent) {

	public record Continent(UUID uuid, String name) {

	}
}