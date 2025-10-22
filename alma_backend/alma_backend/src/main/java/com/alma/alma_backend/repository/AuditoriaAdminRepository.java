package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.AuditoriaAdmin;
import com.alma.alma_backend.entity.TipoAccionAuditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para gestionar registros de auditoría administrativa.
 *
 * Proporciona métodos para consultar el historial de acciones críticas
 * realizadas por SUPER_ADMIN.
 */
@Repository
public interface AuditoriaAdminRepository extends JpaRepository<AuditoriaAdmin, Integer> {

    /**
     * Busca todos los registros de auditoría de un usuario administrador específico.
     *
     * @param idUsuarioAdmin ID del usuario administrador
     * @return Lista de registros de auditoría ordenados por fecha descendente
     */
    List<AuditoriaAdmin> findByUsuarioAdmin_IdOrderByFechaAccionDesc(Integer idUsuarioAdmin);

    /**
     * Busca registros de auditoría por tipo de acción.
     *
     * @param tipoAccion Tipo de acción a buscar
     * @return Lista de registros de auditoría ordenados por fecha descendente
     */
    List<AuditoriaAdmin> findByTipoAccionOrderByFechaAccionDesc(TipoAccionAuditoria tipoAccion);

    /**
     * Busca registros de auditoría para una tabla y registro específico.
     *
     * @param tablaAfectada Nombre de la tabla
     * @param idRegistro ID del registro en esa tabla
     * @return Lista de registros de auditoría ordenados por fecha descendente
     */
    List<AuditoriaAdmin> findByTablaAfectadaAndIdRegistroAfectadoOrderByFechaAccionDesc(
            String tablaAfectada, Integer idRegistro);

    /**
     * Busca registros de auditoría en un rango de fechas.
     *
     * @param fechaInicio Fecha de inicio del rango
     * @param fechaFin Fecha de fin del rango
     * @return Lista de registros de auditoría en el rango especificado
     */
    List<AuditoriaAdmin> findByFechaAccionBetweenOrderByFechaAccionDesc(
            LocalDateTime fechaInicio, LocalDateTime fechaFin);

    /**
     * Busca registros de auditoría de una IP específica.
     * Útil para detectar patrones de acceso sospechosos.
     *
     * @param ipOrigen Dirección IP de origen
     * @return Lista de registros de auditoría desde esa IP
     */
    List<AuditoriaAdmin> findByIpOrigenOrderByFechaAccionDesc(String ipOrigen);

    /**
     * Obtiene las últimas N acciones de auditoría.
     *
     * @param limit Número máximo de registros a retornar
     * @return Lista de los últimos registros de auditoría
     */
    @Query("SELECT a FROM AuditoriaAdmin a ORDER BY a.fechaAccion DESC LIMIT :limit")
    List<AuditoriaAdmin> findTopNByOrderByFechaAccionDesc(@Param("limit") int limit);

    /**
     * Cuenta el número de acciones de un tipo específico realizadas por un administrador.
     *
     * @param idUsuarioAdmin ID del usuario administrador
     * @param tipoAccion Tipo de acción
     * @return Número de acciones de ese tipo
     */
    @Query("SELECT COUNT(a) FROM AuditoriaAdmin a " +
           "WHERE a.usuarioAdmin.id = :idUsuarioAdmin AND a.tipoAccion = :tipoAccion")
    Long countByUsuarioAndTipoAccion(@Param("idUsuarioAdmin") Integer idUsuarioAdmin,
                                      @Param("tipoAccion") TipoAccionAuditoria tipoAccion);

    /**
     * Busca todas las acciones realizadas sobre una organización específica.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de registros de auditoría relacionados con esa organización
     */
    @Query("SELECT a FROM AuditoriaAdmin a " +
           "WHERE a.tablaAfectada = 'ORGANIZACION' AND a.idRegistroAfectado = :idOrganizacion " +
           "ORDER BY a.fechaAccion DESC")
    List<AuditoriaAdmin> findByOrganizacionId(@Param("idOrganizacion") Integer idOrganizacion);
}