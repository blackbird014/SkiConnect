package com.skiconnect.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("SkiConnect API")
                        .description("API for connecting ski teachers with students, with role-based access and place-based search.")
                        .version("1.0.0"))
                .addServersItem(new Server().url("/api/v1"));
    }
} 