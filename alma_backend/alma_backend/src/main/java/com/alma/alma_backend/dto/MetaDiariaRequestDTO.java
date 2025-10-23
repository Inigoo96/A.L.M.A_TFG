package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MetaDiariaRequestDTO {
    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

    @NotBlank(message = "El texto de la meta es obligatorio")
    @Size(max = 255, message = "El texto de la meta no puede exceder 255 caracteres")
    private String textoMeta;

    private LocalDate fechaAsignada; // Si no se proporciona, se asigna la fecha actual

    private String notas;
}