package com.alma.alma_backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO used to create or update organization basic data without exposing the entity directly.
 */
@Data
public class OrganizacionRequestDTO {

    @NotBlank(message = "El CIF es obligatorio")
    @Size(max = 9, message = "El CIF no puede superar 9 caracteres")
    private String cif;

    @NotBlank(message = "El número de la Seguridad Social es obligatorio")
    @Size(max = 20, message = "El número de la Seguridad Social no puede superar 20 caracteres")
    private String numeroSeguridadSocial;

    @NotBlank(message = "El nombre oficial es obligatorio")
    @Size(max = 255, message = "El nombre oficial no puede superar 255 caracteres")
    private String nombreOficial;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede superar 255 caracteres")
    private String direccion;

    @NotBlank(message = "El código REGCESS es obligatorio")
    @Size(max = 50, message = "El código REGCESS no puede superar 50 caracteres")
    private String codigoRegcess;

    @NotBlank(message = "El email corporativo es obligatorio")
    @Email(message = "El email corporativo debe ser válido")
    private String emailCorporativo;

    @NotBlank(message = "El teléfono de contacto es obligatorio")
    @Size(max = 20, message = "El teléfono de contacto no puede superar 20 caracteres")
    private String telefonoContacto;

    @Size(max = 255, message = "La URL del documento CIF no puede superar 255 caracteres")
    private String documentoCifUrl;

    @Size(max = 255, message = "La URL del documento de Seguridad Social no puede superar 255 caracteres")
    private String documentoSeguridadSocialUrl;
}
