package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class ProfesionalResponseDTO {
    private Integer id;
    private UsuarioResponseDTO usuario; // DTO seguro, sin hash
    private String numeroColegiado;
    private String especialidad;
    private String centroSalud;
}
