package com.alma.alma_backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO que representa las estadísticas de carga de trabajo de un profesional.
 *
 * Incluye información sobre el número de pacientes asignados,
 * útil para dashboards de gestión y equilibrio de cargas.
 */
@Data
@NoArgsConstructor
public class ProfesionalEstadisticasDTO {

    // Datos del Profesional
    private Integer idProfesional;
    private String nombreCompleto;
    private String email;
    private String numeroColegiado;
    private String especialidad;

    // Estadísticas de Asignaciones
    private Long totalPacientesAsignados;
    private Long pacientesActivos;
    private Long pacientesInactivos;
    private Long asignacionesPrincipales;

    // Datos de Organización
    private Integer idOrganizacion;
    private String nombreOrganizacion;

    /**
     * Constructor simplificado para queries JPQL en ProfesionalRepository.
     * Usado por las queries de estadísticas que solo incluyen datos básicos.
     *
     * @param idProfesional ID del profesional
     * @param nombre Nombre del profesional
     * @param apellidos Apellidos del profesional
     * @param especialidad Especialidad del profesional
     * @param totalPacientesAsignados Total de pacientes asignados
     * @param pacientesActivos Número de pacientes activos
     */
    public ProfesionalEstadisticasDTO(Integer idProfesional, String nombre, String apellidos,
                                      String especialidad, Long totalPacientesAsignados, Long pacientesActivos) {
        this.idProfesional = idProfesional;
        this.nombreCompleto = (nombre != null ? nombre : "") +
                             (apellidos != null ? " " + apellidos : "");
        this.especialidad = especialidad;
        this.totalPacientesAsignados = totalPacientesAsignados != null ? totalPacientesAsignados : 0L;
        this.pacientesActivos = pacientesActivos != null ? pacientesActivos : 0L;
        this.pacientesInactivos = 0L;
        this.asignacionesPrincipales = 0L;
    }

    /**
     * Constructor completo para proyecciones JPA con todos los campos.
     */
    public ProfesionalEstadisticasDTO(Integer idProfesional, String nombreCompleto,
                                      String email, String numeroColegiado, String especialidad,
                                      Long totalPacientesAsignados, Long pacientesActivos,
                                      Long pacientesInactivos, Long asignacionesPrincipales,
                                      Integer idOrganizacion, String nombreOrganizacion) {
        this.idProfesional = idProfesional;
        this.nombreCompleto = nombreCompleto;
        this.email = email;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
        this.totalPacientesAsignados = totalPacientesAsignados != null ? totalPacientesAsignados : 0L;
        this.pacientesActivos = pacientesActivos != null ? pacientesActivos : 0L;
        this.pacientesInactivos = pacientesInactivos != null ? pacientesInactivos : 0L;
        this.asignacionesPrincipales = asignacionesPrincipales != null ? asignacionesPrincipales : 0L;
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
    }

    /**
     * Constructor alternativo para proyecciones JPA (separa nombre y apellidos).
     */
    public ProfesionalEstadisticasDTO(Integer idProfesional, String nombre, String apellidos,
                                      String email, String numeroColegiado, String especialidad,
                                      Long totalPacientesAsignados, Long pacientesActivos,
                                      Long pacientesInactivos, Long asignacionesPrincipales,
                                      Integer idOrganizacion, String nombreOrganizacion) {
        this.idProfesional = idProfesional;
        this.nombreCompleto = (nombre != null ? nombre : "") +
                             (apellidos != null ? " " + apellidos : "");
        this.email = email;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
        this.totalPacientesAsignados = totalPacientesAsignados != null ? totalPacientesAsignados : 0L;
        this.pacientesActivos = pacientesActivos != null ? pacientesActivos : 0L;
        this.pacientesInactivos = pacientesInactivos != null ? pacientesInactivos : 0L;
        this.asignacionesPrincipales = asignacionesPrincipales != null ? asignacionesPrincipales : 0L;
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
    }

    /**
     * Calcula el porcentaje de carga basándose en un máximo recomendado.
     *
     * @param maximoRecomendado Número máximo recomendado de pacientes
     * @return Porcentaje de carga (0-100+)
     */
    public Double calcularPorcentajeCarga(int maximoRecomendado) {
        if (maximoRecomendado <= 0) {
            return 0.0;
        }
        return (pacientesActivos * 100.0) / maximoRecomendado;
    }
}