package com.yourrents.services.geodata.repository;

import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.jooq.tables.records.AddressRecord;
import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.model.City;
import com.yourrents.services.geodata.model.Country;
import com.yourrents.services.geodata.model.Province;
import org.springframework.stereotype.Component;

@Component
class AddressValueSetter {

  private final CityRepository cityRepository;
  private final ProvinceRepository provinceRepository;
  private final CountryRepository countryRepository;

  AddressValueSetter(CityRepository cityRepository, ProvinceRepository provinceRepository,
      CountryRepository countryRepository) {
    this.cityRepository = cityRepository;
    this.provinceRepository = provinceRepository;
    this.countryRepository = countryRepository;
  }

  /**
   * Set the values of the addressRecord based on the values of address. If the uuid of the city,
   * province, region is set then the application lookup for the corresponding data in the db.
   *
   * @throws DataNotFoundException if one of the linked entity cannot be found.
   */
  void updateAddressRecord(AddressRecord addressRecord, Address address) {
    if (address.addressLine1() != null) {
      addressRecord.setAddressLine_1(address.addressLine1());
    }
    if (address.addressLine2() != null) {
      addressRecord.setAddressLine_2(address.addressLine2());
    }
    if (address.postalCode() != null) {
      addressRecord.setPostalCode(address.postalCode());
    }
    if (address.city() != null) {
      if (address.city().uuid() != null) {
        City city = cityRepository.findByExternalId(address.city().uuid()).orElseThrow(
            () -> new DataNotFoundException("City not found: "
                + address.city().uuid()));
        addressRecord.setCityExternalId(city.uuid());
        addressRecord.setCity(city.name());
      } else {
        addressRecord.setCity(address.city().name());
        addressRecord.setCityExternalId(null);
      }
    }
    if (address.province() != null) {
      if (address.province().uuid() != null) {
        Province province = provinceRepository.findByExternalId(address.province().uuid())
            .orElseThrow(
                () -> new DataNotFoundException("Province not found: "
                    + address.province().uuid()));
        addressRecord.setProvinceExternalId(province.uuid());
        addressRecord.setProvince(province.name());
      } else {
        addressRecord.setProvince(address.province().name());
        addressRecord.setProvinceExternalId(null);
      }
    }
    if (address.country() != null) {
      if (address.country().uuid() != null) {
        Country country = countryRepository.findByExternalId(address.country().uuid()).orElseThrow(
            () -> new DataNotFoundException("Country not found: "
                + address.country().uuid()));
        addressRecord.setCountryExternalId(country.uuid());
        addressRecord.setCountry(country.localName());
      } else {
        addressRecord.setCountry(address.country().localName());
        addressRecord.setCountryExternalId(null);
      }
    }
  }
}
