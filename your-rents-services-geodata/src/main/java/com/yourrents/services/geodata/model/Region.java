package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Region(UUID uuid, String name, RegionLocalData localData,
					 Region.Country country) {

	public record Country(UUID uuid, String localName) {

	}

}