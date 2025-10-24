package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class InformeEmocionalRequestDTO {
    private Integer pacienteId;
    private Integer profesionalId;
    private LocalDate periodoInicio;
    private LocalDate periodoFin;
}
