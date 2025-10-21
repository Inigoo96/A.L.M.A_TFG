package com.alma.alma_backend.dto;

import lombok.Data;

@Data
public class OrganizacionRegistroDTO {

    // Datos de la organización
    private String cif;
    private String numeroSeguridadSocial;
    private String nombreOficial;
    private String direccion;
    private String codigoRegcess;
    private String emailCorporativo;
    private String telefonoContacto;

    // Datos del administrador que registra
    private AdminDTO administrador;

    @Data
    public static class AdminDTO {
        private String dni;
        private String nombre;
        private String apellidos;
        private String email; // Debe ser corporativo
        private String telefono;
        private String cargo;
        private String password;
    }
}