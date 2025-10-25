package com.alma.alma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO for partial updates of user profile data.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UsuarioUpdateRequestDTO {

    @Size(max = 9, message = "El DNI no puede superar 9 caracteres")
    private String dni;

    @Email(message = "El email debe ser válido")
    private String email;

    @Size(max = 255, message = "El nombre no puede superar 255 caracteres")
    private String nombre;

    @Size(max = 255, message = "Los apellidos no pueden superar 255 caracteres")
    private String apellidos;

    @Size(max = 20, message = "El teléfono no puede superar 20 caracteres")
    private String telefono;

    @Size(max = 255, message = "El cargo no puede superar 255 caracteres")
    private String cargo;

    @Size(max = 255, message = "La URL del documento del cargo no puede superar 255 caracteres")
    private String documentoCargoUrl;

    private Boolean activo;
}
