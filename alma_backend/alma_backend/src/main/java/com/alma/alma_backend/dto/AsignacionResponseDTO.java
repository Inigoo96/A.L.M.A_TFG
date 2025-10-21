package com.alma.alma_backend.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class AsignacionResponseDTO {
    private Integer id;
    private ProfesionalResponseDTO profesional;
    private PacienteResponseDTO paciente;
    private LocalDateTime fechaAsignacion;
    private boolean activa;
    private Boolean esPrincipal;
}
