package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO for partial updates of patient details while keeping the entity isolated from controllers.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PacienteUpdateRequestDTO {

    @Past(message = "La fecha de nacimiento debe ser en el pasado")
    private LocalDate fechaNacimiento;

    private Genero genero;

    @Size(max = 50, message = "La tarjeta sanitaria no puede superar 50 caracteres")
    private String tarjetaSanitaria;
}
