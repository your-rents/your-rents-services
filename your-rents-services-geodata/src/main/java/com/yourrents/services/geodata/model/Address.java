package com.yourrents.services.geodata.model;

/*-
 * #%L
 * YourRents GeoData Service
 * %%
 * Copyright (C) 2023 Your Rents Team
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.UUID;

public record Address(UUID uuid, String addressLine1, String addressLine2, String postalCode,
                      AddressCity city, AddressProvince province, AddressCountry country) {

  public record AddressCity(UUID uuid, String name) {

  }

  public record AddressProvince(UUID uuid, String name) {

  }

  public record AddressCountry(UUID uuid, String localName) {

  }

}
