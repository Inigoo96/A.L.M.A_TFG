package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MensajeChatPacientesResponseDTO {
    private Integer id;
    private Integer chatPacientesId;
    private Integer remitenteId;
    private String nombreRemitente;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private boolean leido;
}
