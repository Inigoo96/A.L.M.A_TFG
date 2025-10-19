package com.alma.alma_backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdatePasswordRequest {

    @NotBlank(message = "La contraseña actual no puede estar vacía")
    private String oldPassword;

    @NotBlank(message = "La nueva contraseña no puede estar vacía")
    private String newPassword;
}
