package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class MensajeForoRequestDTO {
    private Integer foroId;
    // Se elimina el usuarioId, se obtendrá del token de seguridad
    private String mensaje;
}
