package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SesionChatRequestDTO {
    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Integer idProfesional;
}