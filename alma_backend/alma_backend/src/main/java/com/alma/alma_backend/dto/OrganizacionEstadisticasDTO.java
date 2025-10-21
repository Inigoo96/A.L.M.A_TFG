package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoVerificacion;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para representar las estadísticas agregadas de usuarios por organización.
 *
 * Este DTO proporciona una vista consolidada del número de usuarios de cada tipo
 * que pertenecen a una organización específica, útil para dashboards administrativos
 * y reportes de gestión.
 */
@Data
@NoArgsConstructor
public class OrganizacionEstadisticasDTO {

    /**
     * Identificador único de la organización
     */
    private Integer idOrganizacion;

    /**
     * Nombre comercial de la organización
     */
    private String nombreOrganizacion;

    /**
     * Código de Identificación Fiscal (CIF) de la organización
     */
    private String cif;

    /**
     * Estado de verificación de la organización
     */
    private EstadoVerificacion estadoVerificacion;

    /**
     * Indica si la organización está actualmente activa (derivado de estadoVerificacion)
     */
    private Boolean activa;

    /**
     * Número total de usuarios (de todos los tipos) asociados a la organización
     */
    private Long totalUsuarios;

    /**
     * Número de usuarios con rol ADMIN_ORGANIZACION
     */
    private Long admins;

    /**
     * Número de usuarios con rol PROFESIONAL
     */
    private Long profesionales;

    /**
     * Número de usuarios con rol PACIENTE
     */
    private Long pacientes;

    /**
     * Número de usuarios con rol SUPER_ADMIN (normalmente debería ser 0 para organizaciones específicas)
     */
    private Long superAdmins;

    /**
     * Constructor simplificado para queries JPQL en OrganizacionRepository.
     * Este constructor recibe el estadoVerificacion directamente y calcula los totales.
     *
     * @param idOrganizacion ID de la organización
     * @param nombreOrganizacion Nombre de la organización
     * @param cif CIF de la organización
     * @param estadoVerificacion Estado de verificación de la organización
     * @param totalUsuarios Total de usuarios
     * @param profesionales Cantidad de profesionales
     * @param pacientes Cantidad de pacientes
     */
    public OrganizacionEstadisticasDTO(Integer idOrganizacion, String nombreOrganizacion,
                                       String cif, EstadoVerificacion estadoVerificacion,
                                       Long totalUsuarios, Long profesionales, Long pacientes) {
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
        this.cif = cif;
        this.estadoVerificacion = estadoVerificacion;
        this.activa = estadoVerificacion == EstadoVerificacion.VERIFICADA;
        this.totalUsuarios = totalUsuarios != null ? totalUsuarios : 0L;
        this.profesionales = profesionales != null ? profesionales : 0L;
        this.pacientes = pacientes != null ? pacientes : 0L;
        this.admins = 0L;
        this.superAdmins = 0L;
    }

    /**
     * Constructor para proyecciones de JPA que calcula el total automáticamente.
     * Este constructor es utilizado por las queries del repository.
     *
     * @param idOrganizacion ID de la organización
     * @param nombreOrganizacion Nombre de la organización
     * @param cif CIF de la organización
     * @param activa Estado de la organización
     * @param admins Cantidad de administradores
     * @param profesionales Cantidad de profesionales
     * @param pacientes Cantidad de pacientes
     * @param superAdmins Cantidad de super administradores
     */
    public OrganizacionEstadisticasDTO(Integer idOrganizacion, String nombreOrganizacion,
                                       String cif, Boolean activa, Long admins,
                                       Long profesionales, Long pacientes, Long superAdmins) {
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
        this.cif = cif;
        this.activa = activa;
        this.admins = admins != null ? admins : 0L;
        this.profesionales = profesionales != null ? profesionales : 0L;
        this.pacientes = pacientes != null ? pacientes : 0L;
        this.superAdmins = superAdmins != null ? superAdmins : 0L;
        this.totalUsuarios = this.admins + this.profesionales + this.pacientes + this.superAdmins;
    }

    /**
     * Calcula y actualiza el total de usuarios basándose en los conteos individuales.
     * Útil cuando se modifican los valores individuales después de la construcción.
     */
    public void calcularTotal() {
        this.totalUsuarios = (this.admins != null ? this.admins : 0L) +
                            (this.profesionales != null ? this.profesionales : 0L) +
                            (this.pacientes != null ? this.pacientes : 0L) +
                            (this.superAdmins != null ? this.superAdmins : 0L);
    }
}