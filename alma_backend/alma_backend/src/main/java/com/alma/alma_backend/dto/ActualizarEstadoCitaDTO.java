package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoCita;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ActualizarEstadoCitaDTO {
    @NotNull(message = "El estado de la cita es obligatorio")
    private EstadoCita estado;

    private String notasSesion;
}