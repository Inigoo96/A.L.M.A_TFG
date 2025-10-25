package com.alma.alma_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"inigoDev", "lauraDev"})
public class OpenApiConfig {

    @Bean
    public OpenAPI almaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("ALMA Backend API")
                        .description("Documentación interactiva del backend de ALMA (solo perfiles de desarrollo)")
                        .version("3.0.0"));
    }
}