package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.TipoUsuario;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UsuarioResponseDTO {
    private Integer id; // <-- CORREGIDO de Long a Integer
    private String dni;
    private String email;
    private String nombre;
    private String apellidos;
    private String telefono;
    private TipoUsuario tipoUsuario;
    private Organizacion organizacion; // Idealmente, esto también sería un DTO. Lo veremos después.
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    private boolean passwordTemporal;
    private String cargo;
    private String documentoCargoUrl;
}
