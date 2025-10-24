package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoRecurso;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsoRecursoResponseDTO {
    private Integer id;
    private Integer pacienteId;
    private TipoRecurso tipoRecurso;
    private Integer idRecurso;
    private LocalDateTime fechaUso;
    private Integer tiempoConsumidoMinutos;
    private Integer valoracion;
}
