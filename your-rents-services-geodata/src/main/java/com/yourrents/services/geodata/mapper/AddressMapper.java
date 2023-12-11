package com.yourrents.services.geodata.mapper;

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

import com.yourrents.services.geodata.model.Address;
import com.yourrents.services.geodata.model.Address.AddressCity;
import com.yourrents.services.geodata.model.Address.AddressCountry;
import com.yourrents.services.geodata.model.Address.AddressProvince;
import java.util.UUID;
import org.jooq.Record;
import org.springframework.stereotype.Component;

@Component
public class AddressMapper {

  public Address map(Record record) {

    return new Address(
        record.get("uuid", UUID.class),
        record.get("addressLine1", String.class),
        record.get("addressLine2", String.class),
        record.get("postalCode", String.class),
        record.get("city", AddressCity.class),
        record.get("province", AddressProvince.class),
        record.get("country", AddressCountry.class));
  }
}
