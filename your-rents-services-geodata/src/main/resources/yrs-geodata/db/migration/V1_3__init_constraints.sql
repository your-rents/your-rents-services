---
--- YourRent Geodata: initial schema constraints
---

ALTER TABLE ONLY yrs_geodata.city
    ADD CONSTRAINT city_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.city_local_data
    ADD CONSTRAINT city_local_data_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.continent
    ADD CONSTRAINT continent_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.country
    ADD CONSTRAINT country_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.province
    ADD CONSTRAINT provincia_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.province_local_data
    ADD CONSTRAINT province_local_data_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.region
    ADD CONSTRAINT region_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.region_local_data
    ADD CONSTRAINT region_local_data_pkey PRIMARY KEY (id);

ALTER TABLE ONLY yrs_geodata.continent
    ADD CONSTRAINT uk_continent_name UNIQUE (name);

ALTER TABLE ONLY yrs_geodata.city
    ADD CONSTRAINT fk_city_province FOREIGN KEY (province_id) REFERENCES yrs_geodata.province(id);

ALTER TABLE ONLY yrs_geodata.city_local_data
    ADD CONSTRAINT fk_city_local_data_city FOREIGN KEY (id) REFERENCES yrs_geodata.city(id);

ALTER TABLE ONLY yrs_geodata.province
    ADD CONSTRAINT fk_province_region FOREIGN KEY (region_id) REFERENCES yrs_geodata.region(id);

ALTER TABLE ONLY yrs_geodata.province_local_data
    ADD CONSTRAINT fk_province_local_data_province FOREIGN KEY (id) REFERENCES yrs_geodata.province(id);

ALTER TABLE ONLY yrs_geodata.region
    ADD CONSTRAINT fk_region_country FOREIGN KEY (country_id) REFERENCES yrs_geodata.country(id);

ALTER TABLE ONLY yrs_geodata.region_local_data
    ADD CONSTRAINT fk_region_local_data_region FOREIGN KEY (id) REFERENCES yrs_geodata.region(id);

ALTER TABLE ONLY yrs_geodata.country
    ADD CONSTRAINT fk_country_continent FOREIGN KEY (continent_id) REFERENCES yrs_geodata.continent(id);
