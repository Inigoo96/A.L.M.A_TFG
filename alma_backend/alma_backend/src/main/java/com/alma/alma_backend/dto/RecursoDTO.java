package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class RecursoDTO {
    private Integer id;
    private String titulo;
    private String descripcion;
    private String url;
    private Integer duracionMinutos;
    private String categoria;
    private String tipoRecurso;
}
