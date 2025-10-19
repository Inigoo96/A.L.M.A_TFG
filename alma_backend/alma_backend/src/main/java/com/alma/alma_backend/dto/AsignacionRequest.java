package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AsignacionRequest {

    @NotNull(message = "El ID del profesional es obligatorio")
    private Integer profesionalId;

    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer pacienteId;
}
