package com.alma.alma_backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desactivar CSRF (Cross-Site Request Forgery) ya que usaremos una API REST stateless (probablemente con JWT más adelante)
            .csrf(csrf -> csrf.disable())
            // Reglas de autorización de peticiones HTTP
            .authorizeHttpRequests(auth -> auth
                // Permitir todas las peticiones a cualquier URL sin autenticación
                .anyRequest().permitAll()
            );

        return http.build();
    }
}
