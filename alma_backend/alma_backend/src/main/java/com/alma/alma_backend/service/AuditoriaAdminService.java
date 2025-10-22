package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AuditoriaAdmin;
import com.alma.alma_backend.entity.TipoAccionAuditoria;
import com.alma.alma_backend.entity.Usuario;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Servicio para gestionar la auditoría de acciones administrativas.
 *
 * Este servicio es responsable de registrar todas las acciones críticas
 * realizadas por SUPER_ADMIN en el sistema.
 */
public interface AuditoriaAdminService {

    /**
     * Registra una acción de auditoría en el sistema.
     *
     * @param usuarioAdmin Usuario administrador que realiza la acción
     * @param tipoAccion Tipo de acción realizada
     * @param tablaAfectada Nombre de la tabla afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param datosAnteriores Estado anterior del registro (JSON)
     * @param datosNuevos Nuevo estado del registro (JSON)
     * @param motivo Justificación de la acción
     * @param ipOrigen IP desde donde se realizó la acción
     * @return Registro de auditoría creado
     */
    AuditoriaAdmin registrarAccion(
            Usuario usuarioAdmin,
            TipoAccionAuditoria tipoAccion,
            String tablaAfectada,
            Integer idRegistroAfectado,
            Map<String, Object> datosAnteriores,
            Map<String, Object> datosNuevos,
            String motivo,
            String ipOrigen
    );

    /**
     * Obtiene todos los registros de auditoría de un administrador.
     *
     * @param idUsuarioAdmin ID del usuario administrador
     * @return Lista de registros de auditoría
     */
    List<AuditoriaAdmin> obtenerAuditoriaPorAdmin(Integer idUsuarioAdmin);

    /**
     * Obtiene todos los registros de auditoría de un tipo específico.
     *
     * @param tipoAccion Tipo de acción a buscar
     * @return Lista de registros de auditoría
     */
    List<AuditoriaAdmin> obtenerAuditoriaPorTipo(TipoAccionAuditoria tipoAccion);

    /**
     * Obtiene el historial completo de auditoría de una organización.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de registros de auditoría relacionados con la organización
     */
    List<AuditoriaAdmin> obtenerAuditoriaOrganizacion(Integer idOrganizacion);

    /**
     * Obtiene registros de auditoría en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio
     * @param fechaFin Fecha de fin
     * @return Lista de registros de auditoría en el rango
     */
    List<AuditoriaAdmin> obtenerAuditoriaPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Obtiene las últimas N acciones de auditoría.
     *
     * @param limit Número máximo de registros
     * @return Lista de los últimos registros de auditoría
     */
    List<AuditoriaAdmin> obtenerUltimasAcciones(int limit);

    /**
     * Obtiene todos los registros de auditoría de una tabla y registro específico.
     *
     * @param tablaAfectada Nombre de la tabla
     * @param idRegistro ID del registro
     * @return Lista de registros de auditoría
     */
    List<AuditoriaAdmin> obtenerAuditoriaPorRegistro(String tablaAfectada, Integer idRegistro);
}