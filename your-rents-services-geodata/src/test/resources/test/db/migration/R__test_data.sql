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
--test data for province
INSERT INTO province (id, name, region_id, external_id) VALUES (1000000, 'ZZZ No Cities Province', null, '00000000-0000-0000-0000-000000000001');

--test data for region
INSERT INTO region (id, name, country_id, external_id) VALUES (1000000, 'ZZZ No Provinces Region', 109, '00000000-0000-0000-0000-000000000001');

--test data for continent
INSERT INTO continent(id, code, name, external_id) VALUES (1000000, 'ZZ', 'ZZZ No Countries continent', '00000000-0000-0000-0000-000000000001');

--test data for address
INSERT INTO yrs_geodata.address
(id, address_line_1, address_line_2, postal_code, city_external_id, city, province_external_id, province, country_external_id, country, external_id)
VALUES
  (1000000000, '42 Rose Street', 'Apt 4B', 'W1G', null, 'Paddington', null, 'London', 'ecda2cd6-5353-4fa0-8456-b92959ff504c', 'United Kingdom', '00000000-0000-0000-0000-000000000001'),
  (1000000001, 'Corso Vittorio Emanuele 45', 'Interno 5', '00186', null, 'Fiumicino', '9d0723cb-8974-4b16-b30f-448346492da8', 'Roma', '27d46902-f762-4428-9a34-65eccd28ab51', 'Italy', '00000000-0000-0000-0000-000000000002'),
  (1000000002, 'Piazza Venezia 12', 'scala 44', '00187', null, 'Roma', '9d0723cb-8974-4b16-b30f-448346492da8', 'Roma', '27d46902-f762-4428-9a34-65eccd28ab51', 'Italy', '00000000-0000-0000-0000-000000000003'),
  (1000000003, 'Via delle Magnolie 12', 'Scala A', '20121', null, 'Bresso', '16541241-c451-49f0-bae8-937dbf4c5c59', 'Milano', '27d46902-f762-4428-9a34-65eccd28ab51', 'Italy', '00000000-0000-0000-0000-000000000004')
 ;
