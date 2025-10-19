package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoUsuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserCreationRequest {

    @NotBlank(message = "El nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "Los apellidos no pueden estar vacíos")
    private String apellidos;

    @NotBlank(message = "El email не может быть пустым")
    @Email(message = "El formato del email es inválido")
    private String email;

    @NotBlank(message = "La contraseña no puede estar vacía")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @NotNull(message = "El tipo de usuario es obligatorio")
    private TipoUsuario tipoUsuario; // Será PACIENTE o PROFESIONAL

    // Campos opcionales específicos para cada rol
    private String numeroColegiado;
    private String especialidad;
}
