package com.alma.alma_backend.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// Solo se carga si el perfil activo es inigoDev o lauraDev
@ConditionalOnExpression(
        "'${spring.profiles.active}'=='inigoDev' or '${spring.profiles.active}'=='lauraDev'"
)
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