package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO que representa la información completa de un profesional,
 * incluyendo datos de usuario y organización sin problemas de lazy loading.
 *
 * Útil para respuestas de API que necesitan todos los datos del profesional
 * sin exponer información sensible como el password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfesionalDetalleDTO {

    // Datos del perfil Profesional
    private Integer idProfesional;
    private String numeroColegiado;
    private String especialidad;

    // Datos del Usuario
    private Integer idUsuario;
    private String email;
    private String nombre;
    private String apellidos;
    private TipoUsuario tipoUsuario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    // Datos de la Organización
    private Integer idOrganizacion;
    private String nombreOrganizacion;
    private String cifOrganizacion;
    private Boolean organizacionActiva;

    /**
     * Obtiene el nombre completo del profesional.
     *
     * @return Nombre completo (nombre + apellidos)
     */
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") +
               (apellidos != null ? " " + apellidos : "");
    }
}