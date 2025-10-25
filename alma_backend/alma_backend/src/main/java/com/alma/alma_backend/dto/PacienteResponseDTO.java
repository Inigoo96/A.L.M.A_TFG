package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta seguro para Paciente.
 * No expone información sensible como el passwordHash del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {

    private Integer id;
    private UsuarioResponseDTO usuario;
    private String tarjetaSanitaria;
    private LocalDate fechaNacimiento;
    private Genero genero;

}