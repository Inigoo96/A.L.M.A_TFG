package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class MensajeChatPacientesRequestDTO {
    private Integer chatPacientesId;
    private Integer remitenteId;
    private String mensaje;
}
