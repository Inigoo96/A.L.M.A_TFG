package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MensajeChatResponseDTO {
    private Integer id;
    private Integer idSesionChat;
    private Integer idRemitente;
    private String nombreRemitente;
    private String tipoRemitente;
    private String mensaje;
    private LocalDateTime fechaEnvio;
    private Boolean leido;
    private LocalDateTime fechaLectura;
}