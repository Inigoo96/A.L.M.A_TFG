package com.alma.alma_backend.dto;

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
    private OrganizacionSummaryDTO organizacion;
    private boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;
    private boolean passwordTemporal;
    private String cargo;
    private String documentoCargoUrl;
}
