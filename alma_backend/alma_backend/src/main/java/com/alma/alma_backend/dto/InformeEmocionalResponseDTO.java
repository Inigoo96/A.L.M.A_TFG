package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.GeneradoPor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
public class InformeEmocionalResponseDTO {
    private Integer id;
    private Integer pacienteId;
    private String nombrePaciente;
    private Integer profesionalId;
    private String nombreProfesional;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
    private Map<String, Object> contenidoInforme;
    private GeneradoPor generadoPor;
    private LocalDateTime fechaGeneracion;
}
