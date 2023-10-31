package com.yourrents.services.geodata.model;

import java.util.UUID;

public record Country(String isoCode, String localName, String englishFullName, UUID uuid) {

	@Override
	public String toString() {
		return "Country{" +
				"isoCode='" + isoCode + '\'' +
				", localName='" + localName + '\'' +
				", englishFullName='" + englishFullName + '\'' +
				'}';
	}
}