package com.yourrents.services.geodata;

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