---
-- #%L
-- YourRents GeoData Service
-- %%
-- Copyright (C) 2023 Your Rents Team
-- %%
-- Licensed under the Apache License, Version 2.0 (the "License");
-- you may not use this file except in compliance with the License.
-- You may obtain a copy of the License at
-- 
--      http://www.apache.org/licenses/LICENSE-2.0
-- 
-- Unless required by applicable law or agreed to in writing, software
-- distributed under the License is distributed on an "AS IS" BASIS,
-- WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
-- See the License for the specific language governing permissions and
-- limitations under the License.
-- #L%
---
---
--- YourRents Geodata: initial scheme creation
---
CREATE TABLE yrs_geodata.city (
    id SERIAL,
    name character varying(256) NOT NULL,
    province_id integer,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.city_local_data (
    id integer,
    it_codice_istat character varying(6),
    it_codice_erariale character varying(4)
);

CREATE TABLE yrs_geodata.continent (
    id SERIAL,
    code character varying(2) NOT NULL,
    name character varying(32),
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.country (
    id SERIAL,
    iso_code character varying(2),
    english_full_name character varying(256),
    iso_3 character varying(3),
    local_name character varying(256),
    number integer,
    continent_id integer,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.province (
    id SERIAL,
    name character varying(256) NOT NULL,
    region_id integer,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.province_local_data (
    id integer,
    it_codice_istat character varying(3),
    it_sigla character varying(2)
);

CREATE TABLE yrs_geodata.region (
    id SERIAL,
    name character varying(256) NOT NULL,
    country_id integer,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.region_local_data (
    id integer,
    it_codice_istat character varying(2)
);

CREATE TABLE yrs_geodata.address
(
  id             SERIAL,
  address_line_1 TEXT NOT NULL,
  address_line_2 TEXT,
  postal_code    character varying(20),
  city_external_id         UUID,
  city           character varying(256),
  province_external_id   UUID,
  province       character varying(256),
  country_external_id   UUID,
  country        character varying(256),
  external_id    UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);