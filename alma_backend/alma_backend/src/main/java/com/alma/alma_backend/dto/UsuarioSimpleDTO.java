package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO simplificado de Usuario para respuestas que no requieren todos los detalles.
 *
 * Útil para listas, selects y autocomplete donde se necesita información básica
 * sin exponer datos sensibles.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioSimpleDTO {

    private Integer idUsuario;
    private String email;
    private String nombre;
    private String apellidos;
    private TipoUsuario tipoUsuario;
    private Boolean activo;
    private String nombreOrganizacion;

    /**
     * Obtiene el nombre completo del usuario.
     */
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") +
               (apellidos != null ? " " + apellidos : "");
    }
}