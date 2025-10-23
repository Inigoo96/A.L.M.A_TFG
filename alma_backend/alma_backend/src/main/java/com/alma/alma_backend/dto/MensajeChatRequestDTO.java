package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MensajeChatRequestDTO {
    @NotNull(message = "El ID de la sesión de chat es obligatorio")
    private Integer idSesionChat;

    @NotBlank(message = "El mensaje no puede estar vacío")
    private String mensaje;
}