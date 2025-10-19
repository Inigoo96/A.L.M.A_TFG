package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import com.alma.alma_backend.entity.TipoUsuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * DTO que representa la información completa de un paciente,
 * incluyendo datos de usuario y organización sin problemas de lazy loading.
 *
 * Útil para respuestas de API que necesitan todos los datos del paciente
 * sin exponer información sensible como el password.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteDetalleDTO {

    // Datos del perfil Paciente
    private Integer idPaciente;
    private LocalDate fechaNacimiento;
    private Genero genero;

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
     * Obtiene el nombre completo del paciente.
     *
     * @return Nombre completo (nombre + apellidos)
     */
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") +
               (apellidos != null ? " " + apellidos : "");
    }

    /**
     * Calcula la edad del paciente basándose en su fecha de nacimiento.
     *
     * @return Edad en años, o null si no hay fecha de nacimiento
     */
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}