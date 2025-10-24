package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoRecurso;
import lombok.Data;

@Data
public class UsoRecursoRequestDTO {
    private Integer pacienteId;
    private TipoRecurso tipoRecurso;
    private Integer idRecurso;
    private Integer tiempoConsumidoMinutos;
    private Integer valoracion;
}
