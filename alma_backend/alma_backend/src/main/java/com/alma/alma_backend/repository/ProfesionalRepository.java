package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.ProfesionalDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalEstadisticasDTO;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Integer> {

    /**
     * Busca un perfil de Profesional a partir de su entidad Usuario asociada.
     * @param usuario La entidad Usuario.
     * @return Un Optional que contiene el Profesional si se encuentra, o vacío en caso contrario.
     */
    Optional<Profesional> findByUsuario(Usuario usuario);

    /**
     * Busca un perfil de Profesional a partir del ID de su entidad Usuario asociada.
     * @param usuarioId El ID del Usuario.
     * @return Un Optional que contiene el Profesional si se encuentra, o vacío en caso contrario.
     */
    Optional<Profesional> findByUsuario_IdUsuario(Integer usuarioId);

    /**
     * Obtiene el detalle completo de un profesional con información de usuario y organización
     * en una sola query, evitando problemas de lazy loading.
     *
     * @param idProfesional ID del profesional
     * @return Optional con el DTO completo del profesional
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.idProfesional, p.numeroColegiado, p.especialidad, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE p.idProfesional = :idProfesional")
    Optional<ProfesionalDetalleDTO> findDetalleById(@Param("idProfesional") Integer idProfesional);

    /**
     * Obtiene todos los profesionales de una organización con su información completa.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de DTOs con información completa de profesionales
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.idProfesional, p.numeroColegiado, p.especialidad, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "ORDER BY u.apellidos, u.nombre")
    List<ProfesionalDetalleDTO> findDetalleByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Obtiene todos los profesionales activos de una organización.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de DTOs con información completa de profesionales activos
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.idProfesional, p.numeroColegiado, p.especialidad, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND u.activo = true " +
           "ORDER BY u.apellidos, u.nombre")
    List<ProfesionalDetalleDTO> findActivosByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Obtiene las estadísticas de carga de trabajo de todos los profesionales de una organización.
     *
     * Incluye conteo de pacientes asignados (totales, activos, inactivos) y asignaciones principales.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de DTOs con estadísticas de cada profesional
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalEstadisticasDTO(" +
           "p.idProfesional, u.nombre, u.apellidos, u.email, " +
           "p.numeroColegiado, p.especialidad, " +
           "COUNT(a.idAsignacion), " +
           "SUM(CASE WHEN a.activo = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.activo = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.esPrincipal = true AND a.activo = true THEN 1 ELSE 0 END), " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "LEFT JOIN p.asignaciones a " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND u.activo = true " +
           "GROUP BY p.idProfesional, u.nombre, u.apellidos, u.email, " +
           "p.numeroColegiado, p.especialidad, o.idOrganizacion, o.nombreOrganizacion " +
           "ORDER BY u.apellidos, u.nombre")
    List<ProfesionalEstadisticasDTO> findEstadisticasByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Obtiene las estadísticas de un profesional específico.
     *
     * @param idProfesional ID del profesional
     * @return Optional con las estadísticas del profesional
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalEstadisticasDTO(" +
           "p.idProfesional, u.nombre, u.apellidos, u.email, " +
           "p.numeroColegiado, p.especialidad, " +
           "COUNT(a.idAsignacion), " +
           "SUM(CASE WHEN a.activo = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.activo = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.esPrincipal = true AND a.activo = true THEN 1 ELSE 0 END), " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "LEFT JOIN p.asignaciones a " +
           "WHERE p.idProfesional = :idProfesional " +
           "GROUP BY p.idProfesional, u.nombre, u.apellidos, u.email, " +
           "p.numeroColegiado, p.especialidad, o.idOrganizacion, o.nombreOrganizacion")
    Optional<ProfesionalEstadisticasDTO> findEstadisticasById(@Param("idProfesional") Integer idProfesional);

    /**
     * Busca profesionales por especialidad dentro de una organización.
     *
     * @param especialidad Especialidad a buscar (búsqueda parcial, case-insensitive)
     * @param idOrganizacion ID de la organización
     * @return Lista de profesionales que coinciden con la especialidad
     */
    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.idProfesional, p.numeroColegiado, p.especialidad, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Profesional p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND LOWER(p.especialidad) LIKE LOWER(CONCAT('%', :especialidad, '%')) " +
           "AND u.activo = true " +
           "ORDER BY u.apellidos, u.nombre")
    List<ProfesionalDetalleDTO> findByEspecialidadAndOrganizacion(
            @Param("especialidad") String especialidad,
            @Param("idOrganizacion") Integer idOrganizacion);
}
