package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PacienteRegistroDTO {
    private String dni;
    private String nombre;
    private String apellidos;
    private String email;
    private String telefono;
    private String tarjetaSanitaria;
    private LocalDate fechaNacimiento;
    private Genero genero;
}