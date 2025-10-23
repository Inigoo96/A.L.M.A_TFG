package com.alma.alma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EstadisticasMetasDTO {
    private Integer idPaciente;
    private String nombrePaciente;
    private Long totalMetas;
    private Long metasCompletadas;
    private Long metasPendientes;
    private Long metasCanceladas;
    private Double porcentajeCompletado;
}