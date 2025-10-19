package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.PacienteDetalleDTO;
import com.alma.alma_backend.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByUsuario_IdUsuario(Integer idUsuario);

    List<Paciente> findByUsuario_Organizacion_IdOrganizacion(Integer idOrganizacion);

    /**
     * Obtiene el detalle completo de un paciente con información de usuario y organización
     * en una sola query, evitando problemas de lazy loading.
     *
     * @param idPaciente ID del paciente
     * @return Optional con el DTO completo del paciente
     */
    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE p.idPaciente = :idPaciente")
    Optional<PacienteDetalleDTO> findDetalleById(@Param("idPaciente") Integer idPaciente);

    /**
     * Obtiene todos los pacientes de una organización con su información completa.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de DTOs con información completa de pacientes
     */
    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "ORDER BY u.apellidos, u.nombre")
    List<PacienteDetalleDTO> findDetalleByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Obtiene todos los pacientes activos de una organización.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de DTOs con información completa de pacientes activos
     */
    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND u.activo = true " +
           "ORDER BY u.apellidos, u.nombre")
    List<PacienteDetalleDTO> findActivosByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Obtiene los pacientes asignados a un profesional específico.
     *
     * @param idProfesional ID del profesional
     * @param soloActivos Si es true, solo devuelve asignaciones activas
     * @return Lista de pacientes con información completa
     */
    @Query("SELECT DISTINCT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "JOIN p.asignaciones a " +
           "WHERE a.profesional.idProfesional = :idProfesional " +
           "AND (:soloActivos = false OR a.activo = true) " +
           "ORDER BY u.apellidos, u.nombre")
    List<PacienteDetalleDTO> findPacientesByProfesional(
            @Param("idProfesional") Integer idProfesional,
            @Param("soloActivos") boolean soloActivos);

    /**
     * Busca pacientes sin asignar (sin asignaciones activas) en una organización.
     * Útil para mostrar pacientes disponibles para asignación.
     *
     * @param idOrganizacion ID de la organización
     * @return Lista de pacientes sin asignación activa
     */
    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND u.activo = true " +
           "AND NOT EXISTS (" +
           "  SELECT a FROM AsignacionProfesionalPaciente a " +
           "  WHERE a.paciente.idPaciente = p.idPaciente " +
           "  AND a.activo = true" +
           ") " +
           "ORDER BY u.apellidos, u.nombre")
    List<PacienteDetalleDTO> findSinAsignarByOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    /**
     * Busca pacientes por nombre o apellidos (búsqueda parcial) dentro de una organización.
     *
     * @param searchTerm Término de búsqueda
     * @param idOrganizacion ID de la organización
     * @return Lista de pacientes que coinciden con la búsqueda
     */
    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "p.idPaciente, p.fechaNacimiento, p.genero, " +
           "u.idUsuario, u.email, u.nombre, u.apellidos, u.tipoUsuario, " +
           "u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa) " +
           "FROM Paciente p " +
           "JOIN p.usuario u " +
           "JOIN u.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND u.activo = true " +
           "AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "     OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY u.apellidos, u.nombre")
    List<PacienteDetalleDTO> searchByNombreAndOrganizacion(
            @Param("searchTerm") String searchTerm,
            @Param("idOrganizacion") Integer idOrganizacion);
}
