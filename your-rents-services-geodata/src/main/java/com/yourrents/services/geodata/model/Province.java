package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Province(UUID uuid, String name, ProvinceLocalData localData,
					   Province.Region region) {
	public record Region(UUID uuid, String name) {
	}

}