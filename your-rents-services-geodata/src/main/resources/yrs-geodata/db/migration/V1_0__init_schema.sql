---
--- YourRents Geodata: initial scheme creation
---
CREATE TABLE yrs_geodata.city (
    id SERIAL,
    name character varying(256) NOT NULL,
    province_id bigint,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.city_local_data (
    id bigint,
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
    continent_id bigint,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.province (
    id SERIAL,
    name character varying(256) NOT NULL,
    region_id bigint,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.province_local_data (
    id bigint,
    it_codice_istat character varying(3),
    it_sigla character varying(2)
);

CREATE TABLE yrs_geodata.region (
    id SERIAL,
    name character varying(256) NOT NULL,
    country_id bigint,
    external_id UUID NOT NULL UNIQUE DEFAULT gen_random_uuid()
);

CREATE TABLE yrs_geodata.region_local_data (
    id bigint,
    it_codice_istat character varying(2)
);
