package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoEmocional;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProgresoDueloRequestDTO {

    @NotNull(message = "El ID del paciente no puede ser nulo")
    private Integer idPaciente;

    private Integer idProfesional; // Puede ser nulo si el paciente lo registra

    @NotNull(message = "El ID de la fase de duelo no puede ser nulo")
    private Integer idFaseDuelo;

    @NotNull(message = "El estado emocional no puede ser nulo")
    private EstadoEmocional estadoEmocional;

    private String notas;
}
