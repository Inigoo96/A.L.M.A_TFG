package com.alma.alma_backend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para las respuestas de autenticación (login y registro).
 * Contiene el token JWT y información básica del usuario.
 */
@Data
@NoArgsConstructor
public class AuthenticationResponse {

    @JsonProperty("access_token")
    private String jwt;

    @JsonProperty("email")
    private String email;

    @JsonProperty("role")
    private String role;

    @JsonProperty("token_type")
    private String tokenType = "Bearer";

    public AuthenticationResponse(String jwt, String email, String role) {
        this.jwt = jwt;
        this.email = email;
        this.role = role;
        this.tokenType = "Bearer";
    }
}
