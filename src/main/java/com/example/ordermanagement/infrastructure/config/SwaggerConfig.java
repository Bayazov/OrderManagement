package com.example.ordermanagement.infrastructure.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.tags.Tag;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("basicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")))
                .info(new Info()
                        .title("Order Management API")
                        .version("1.0")
                        .description("API for managing orders"))
                .tags(Arrays.asList(
                        new Tag().name("User Operations").description("Endpoints available for regular users"),
                        new Tag().name("Admin Operations").description("Endpoints available for administrators")
                ));
    }
}



