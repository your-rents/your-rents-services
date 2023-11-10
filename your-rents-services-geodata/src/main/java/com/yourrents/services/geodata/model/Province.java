package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Province(UUID uuid, String name, Province.Region region) {
	public record Region(UUID uuid, String name) {
	}

}