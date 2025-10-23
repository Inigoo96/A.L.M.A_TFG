package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgresoDueloResponseDTO {
    private Integer id;
    private Integer idPaciente;
    private Integer idProfesional;
    private String nombreFaseDuelo;
    private String estadoEmocional;
    private String notas;
    private LocalDateTime fechaRegistro;
}
