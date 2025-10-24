package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class ForoDTO {
    private Integer id;
    private String nombre;
    private String descripcion;
    private Integer faseDueloId;
    private String faseDueloNombre;
}
