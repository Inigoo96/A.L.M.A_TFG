package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class ProfesionalRegistroDTO {
    private String dni;
    private String nombre;
    private String apellidos;
    private String email; // Preferiblemente corporativo
    private String telefono;
    private String numeroColegiado;
    private String especialidad;
    private String centroSalud; // Con código REGCESS
}