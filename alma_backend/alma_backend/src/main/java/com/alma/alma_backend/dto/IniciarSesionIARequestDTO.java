package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoSesion;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IniciarSesionIARequestDTO {
    @NotNull(message = "El ID del paciente es obligatorio")
    private Integer idPaciente;

    private Integer idProfesional; // Opcional, para asignar profesional a la sesión

    private TipoSesion tipoSesion; // Por defecto: CONVERSACION
}