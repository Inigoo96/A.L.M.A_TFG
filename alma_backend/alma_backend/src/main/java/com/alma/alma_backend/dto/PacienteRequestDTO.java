package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRequestDTO {

    @NotNull(message = "El ID de usuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "La fecha de nacimiento es obligatoria")
    private LocalDate fechaNacimiento;

    private String genero;

    @NotBlank(message = "La tarjeta sanitaria es obligatoria")
    private String tarjetaSanitaria;
}
