package com.alma.alma_backend.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para exponer la información de un Paciente a un Profesional.
 */
@Data
public class PacienteDTO {

    private Integer id;
    private String nombre;
    private String apellidos;
    private String email;
    private LocalDate fechaNacimiento;
}
