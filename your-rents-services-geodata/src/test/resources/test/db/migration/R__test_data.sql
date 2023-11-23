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

INSERT INTO province (id, name, region_id, external_id) VALUES (1000000, 'ZZZ No Cities Province', null, '00000000-0000-0000-0000-000000000001');
INSERT INTO region (id, name, country_id, external_id) VALUES (1000000, 'ZZZ No Provinces Region', 109, '00000000-0000-0000-0000-000000000001');