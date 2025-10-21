package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class AsignacionRequestDTO {
    private Integer profesionalId;
    private Integer pacienteId;
    private Boolean esPrincipal;
}
