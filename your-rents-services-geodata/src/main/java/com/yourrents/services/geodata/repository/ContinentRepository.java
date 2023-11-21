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
import static org.jooq.Records.mapping;

import com.yourrents.services.geodata.model.Continent;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.SelectJoinStep;
import org.springframework.stereotype.Repository;

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

	private SelectJoinStep<Record3<String, String, UUID>> getSelectContinentSpec() {
		return dsl.select(
						CONTINENT.CODE.as("code"),
						CONTINENT.NAME.as("name"),
						CONTINENT.EXTERNAL_ID.as("uuid"))
				.from(CONTINENT);

	}

}
