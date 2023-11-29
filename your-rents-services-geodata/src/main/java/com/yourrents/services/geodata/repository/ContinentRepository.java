package com.yourrents.services.geodata.repository;

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

import static com.yourrents.services.geodata.jooq.Tables.CONTINENT;
import static com.yourrents.services.geodata.jooq.Tables.COUNTRY;
import static org.jooq.Records.mapping;

import com.yourrents.services.common.util.exception.DataConflictException;
import com.yourrents.services.common.util.exception.DataNotFoundException;
import com.yourrents.services.geodata.jooq.tables.records.ContinentRecord;
import com.yourrents.services.geodata.model.Continent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ContinentRepository {

	private final DSLContext dsl;

	ContinentRepository(DSLContext dsl) {
		this.dsl = dsl;
	}

	public List<Continent> findAll() {
		return getSelectContinentSpec()
				.orderBy(CONTINENT.ID.asc())
				.fetch()
				.map(mapping(Continent::new));
	}

	public Optional<Continent> findById(Integer id) {
		return getSelectContinentSpec()
				.where(CONTINENT.ID.eq(id))
				.fetchOptional()
				.map(mapping(Continent::new));
	}

	public Optional<Continent> findByExternalId(UUID externalId) {
		return getSelectContinentSpec()
				.where(CONTINENT.EXTERNAL_ID.eq(externalId))
				.fetchOptional()
				.map(mapping(Continent::new));
	}

  /**
   * Create a new Continent
   *
   * @return the new created continent
   */
  @Transactional(readOnly = false)
  public Continent create(Continent continent) {
    ContinentRecord newContinent = dsl.newRecord(CONTINENT);
    newContinent.setCode(continent.code());
    newContinent.setName(continent.name());
    newContinent.insert();
    return findById(newContinent.getId()).orElseThrow();
  }

  /**
   * Delete a continent only if there are no countries associated with it.
   *
   * @return true if the continent has been deleted, false otherwise
   * @throws DataNotFoundException if the continent does not exist
   * @throws DataConflictException if there is at least one country associated to it
   */
  @Transactional(readOnly = false)
  public boolean delete(UUID uuid) {
    Integer continentId = dsl.select(CONTINENT.ID)
        .from(CONTINENT)
        .where(CONTINENT.EXTERNAL_ID.eq(uuid))
        .fetchOptional(CONTINENT.ID).orElseThrow(
            () -> new DataNotFoundException("Continent not found: " + uuid));
    boolean countriesExist = dsl.fetchExists(COUNTRY, COUNTRY.CONTINENT_ID.eq(continentId));
    if (countriesExist) {
      throw new DataConflictException(
          "Unable to delete the continent with UUID: " + uuid
              + " because it is referenced by at least one country");
    }
    return dsl.deleteFrom(CONTINENT)
        .where(CONTINENT.ID.eq(continentId))
        .execute() > 0;
  }

  private SelectJoinStep<Record3<UUID, String, String>> getSelectContinentSpec() {
		return dsl.select(
            CONTINENT.EXTERNAL_ID.as("uuid"),
						CONTINENT.CODE.as("code"),
            CONTINENT.NAME.as("name"))
				.from(CONTINENT);
  }

  /**
   * Update a continent.
   * <p>
   * You can update the name, the code. You can't update the continent uuid.
   * <p>
   * Only not null fields are used to update the continent.
   *
   * @param uuid the uuid of the continent to be updated.
   * @param continent the data of continent to be updated.
   * @return the updated continent
   * @throws DataNotFoundException if the continent does not exist
   */
  @Transactional(readOnly = false)
  public Continent update(UUID uuid, Continent continent) {
    ContinentRecord dbContinent = dsl.selectFrom(CONTINENT)
        .where(CONTINENT.EXTERNAL_ID.eq(uuid))
        .fetchOptional().orElseThrow(
            () -> new DataNotFoundException("Continent not found: " + uuid));
    if (continent.code() != null) {
      dbContinent.setCode(continent.code());
    }
    if (continent.name() != null) {
      dbContinent.setName(continent.name());
    }
    dbContinent.update();
    return findById(dbContinent.getId())
        .orElseThrow(() -> new RuntimeException("failed to update continent: " + uuid));
  }

}
