package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatPacientesResponseDTO {
    private Integer id;
    private Integer pacienteId1;
    private String nombrePaciente1;
    private Integer pacienteId2;
    private String nombrePaciente2;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaActividad;
    private String estado;
}
