package com.yourrents.services.geodata;

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

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI configOpenApi(
            @Value("${spring.application.name}") String name,
            @Value("${yrs-geodata.api.version}") String version,
            @Value("${yrs-geodata.description}") String description) {
        return new OpenAPI()
            .info(
                new Info()
                    .title(name).version(version).description(description)
                    .termsOfService("https://github.com/your-rents/your-rents-services")
                    .license(new License().name("Apache License, Version 2.0").identifier("Apache-2.0")
                    .url("https://opensource.org/license/apache-2-0/")))
                ;
    }

}
