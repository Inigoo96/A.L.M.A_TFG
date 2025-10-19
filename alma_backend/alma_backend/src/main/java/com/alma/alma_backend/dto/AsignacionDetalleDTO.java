package com.alma.alma_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * DTO que representa una asignación profesional-paciente con todos los detalles
 * de ambas partes, sin necesidad de cargar entidades completas (evita N+1 queries).
 *
 * Útil para listar asignaciones con información completa en una sola query.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionDetalleDTO {

    // Datos de la Asignación
    private Integer idAsignacion;
    private Boolean esPrincipal;
    private LocalDateTime fechaAsignacion;
    private Boolean activo;

    // Datos del Profesional
    private Integer idProfesional;
    private String nombreProfesional;
    private String apellidosProfesional;
    private String emailProfesional;
    private String numeroColegiado;
    private String especialidad;

    // Datos del Paciente
    private Integer idPaciente;
    private String nombrePaciente;
    private String apellidosPaciente;
    private String emailPaciente;
    private LocalDate fechaNacimientoPaciente;

    // Datos de Organización
    private Integer idOrganizacion;
    private String nombreOrganizacion;

    /**
     * Obtiene el nombre completo del profesional.
     */
    public String getNombreCompletoProfesional() {
        return (nombreProfesional != null ? nombreProfesional : "") +
               (apellidosProfesional != null ? " " + apellidosProfesional : "");
    }

    /**
     * Obtiene el nombre completo del paciente.
     */
    public String getNombreCompletoPaciente() {
        return (nombrePaciente != null ? nombrePaciente : "") +
               (apellidosPaciente != null ? " " + apellidosPaciente : "");
    }
}