package com.alma.alma_backend.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FinalizarSesionIARequestDTO {
    @NotNull(message = "El ID de la sesión es obligatorio")
    private Integer idSesion;

    @Min(value = 1, message = "La satisfacción debe ser mínimo 1")
    @Max(value = 5, message = "La satisfacción debe ser máximo 5")
    private Integer satisfaccion;

    private String estadoEmocionalDetectado;

    private String notasProfesional; // Solo si el profesional finaliza la sesión
}