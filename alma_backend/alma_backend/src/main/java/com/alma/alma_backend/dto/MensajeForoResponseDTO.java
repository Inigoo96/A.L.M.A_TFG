package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MensajeForoResponseDTO {
    private Integer id;
    private Integer foroId;
    private Integer usuarioId;
    private String nombreUsuario;
    private String mensaje;
    private LocalDateTime fechaPublicacion;
    private boolean editado;
    private LocalDateTime fechaEdicion;
}
