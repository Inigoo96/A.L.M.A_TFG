package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoMeta;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MetaDiariaResponseDTO {
    private Integer id;
    private Integer idPaciente;
    private String nombrePaciente;
    private String textoMeta;
    private LocalDate fechaAsignada;
    private EstadoMeta estado;
    private String notas;
    private LocalDateTime fechaCompletada;
    private LocalDateTime fechaCreacion;
}