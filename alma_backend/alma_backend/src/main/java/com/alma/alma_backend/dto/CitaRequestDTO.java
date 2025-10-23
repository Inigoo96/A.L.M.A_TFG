package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaRequestDTO {
    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

    @NotNull(message = "El ID del profesional es obligatorio")
    private Integer idProfesional;

    @NotNull(message = "La fecha y hora de la cita son obligatorias")
    private LocalDateTime fechaHora;

    private Integer duracionMinutos;

    private TipoCita tipoCita;

    private String motivo;
}