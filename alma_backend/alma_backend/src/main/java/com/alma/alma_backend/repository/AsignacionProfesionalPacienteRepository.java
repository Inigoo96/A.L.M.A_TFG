package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.AsignacionDetalleDTO;
import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AsignacionProfesionalPacienteRepository extends JpaRepository<AsignacionProfesionalPaciente, Integer> {

    // Encontrar todas las asignaciones de un paciente (activas e inactivas)
    List<AsignacionProfesionalPaciente> findByPaciente_IdPaciente(Integer idPaciente);

    // Encontrar todas las asignaciones de un profesional (activas e inactivas)
    List<AsignacionProfesionalPaciente> findByProfesional_IdProfesional(Integer idProfesional);

    // Encontrar asignaciones activas para un paciente
    List<AsignacionProfesionalPaciente> findByPaciente_IdPacienteAndActivoTrue(Integer idPaciente);

    // Encontrar asignaciones activas para un profesional
    List<AsignacionProfesionalPaciente> findByProfesional_IdProfesionalAndActivoTrue(Integer idProfesional);

    // Encontrar la asignación principal de un paciente
    Optional<AsignacionProfesionalPaciente> findByPaciente_IdPacienteAndEsPrincipalTrueAndActivoTrue(Integer idPaciente);

    /**
     * Obtiene los detalles completos de una asignación específica.
     *
     * @param idAsignacion ID de la asignación
     * @return Optional con el DTO detallado de la asignación
     */
    @Query("SELECT new com.alma.alma_backend.dto.AsignacionDetalleDTO(" +
           "a.idAsignacion, a.esPrincipal, a.fechaAsignacion, a.activo, " +
           "prof.idProfesional, uProf.nombre, uProf.apellidos, uProf.email, " +
           "prof.numeroColegiado, prof.especialidad, " +
           "pac.idPaciente, uPac.nombre, uPac.apellidos, uPac.email, pac.fechaNacimiento, " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM AsignacionProfesionalPaciente a " +
           "JOIN a.profesional prof " +
           "JOIN prof.usuario uProf " +
           "JOIN a.paciente pac " +
           "JOIN pac.usuario uPac " +
           "JOIN uProf.organizacion o " +
           "WHERE a.idAsignacion = :idAsignacion")
    Optional<AsignacionDetalleDTO> findDetalleById(@Param("idAsignacion") Integer idAsignacion);

    /**
     * Obtiene todas las asignaciones de un profesional con detalles completos.
     *
     * @param idProfesional ID del profesional
     * @param soloActivas Si es true, solo devuelve asignaciones activas
     * @return Lista de asignaciones con detalles
     */
    @Query("SELECT new com.alma.alma_backend.dto.AsignacionDetalleDTO(" +
           "a.idAsignacion, a.esPrincipal, a.fechaAsignacion, a.activo, " +
           "prof.idProfesional, uProf.nombre, uProf.apellidos, uProf.email, " +
           "prof.numeroColegiado, prof.especialidad, " +
           "pac.idPaciente, uPac.nombre, uPac.apellidos, uPac.email, pac.fechaNacimiento, " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM AsignacionProfesionalPaciente a " +
           "JOIN a.profesional prof " +
           "JOIN prof.usuario uProf " +
           "JOIN a.paciente pac " +
           "JOIN pac.usuario uPac " +
           "JOIN uProf.organizacion o " +
           "WHERE prof.idProfesional = :idProfesional " +
           "AND (:soloActivas = false OR a.activo = true) " +
           "ORDER BY a.esPrincipal DESC, a.fechaAsignacion DESC")
    List<AsignacionDetalleDTO> findDetalleByProfesional(
            @Param("idProfesional") Integer idProfesional,
            @Param("soloActivas") boolean soloActivas);

    /**
     * Obtiene todas las asignaciones de un paciente con detalles completos.
     *
     * @param idPaciente ID del paciente
     * @param soloActivas Si es true, solo devuelve asignaciones activas
     * @return Lista de asignaciones con detalles
     */
    @Query("SELECT new com.alma.alma_backend.dto.AsignacionDetalleDTO(" +
           "a.idAsignacion, a.esPrincipal, a.fechaAsignacion, a.activo, " +
           "prof.idProfesional, uProf.nombre, uProf.apellidos, uProf.email, " +
           "prof.numeroColegiado, prof.especialidad, " +
           "pac.idPaciente, uPac.nombre, uPac.apellidos, uPac.email, pac.fechaNacimiento, " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM AsignacionProfesionalPaciente a " +
           "JOIN a.profesional prof " +
           "JOIN prof.usuario uProf " +
           "JOIN a.paciente pac " +
           "JOIN pac.usuario uPac " +
           "JOIN uProf.organizacion o " +
           "WHERE pac.idPaciente = :idPaciente " +
           "AND (:soloActivas = false OR a.activo = true) " +
           "ORDER BY a.esPrincipal DESC, a.fechaAsignacion DESC")
    List<AsignacionDetalleDTO> findDetalleByPaciente(
            @Param("idPaciente") Integer idPaciente,
            @Param("soloActivas") boolean soloActivas);

    /**
     * Obtiene todas las asignaciones de una organización con detalles completos.
     *
     * @param idOrganizacion ID de la organización
     * @param soloActivas Si es true, solo devuelve asignaciones activas
     * @return Lista de asignaciones con detalles
     */
    @Query("SELECT new com.alma.alma_backend.dto.AsignacionDetalleDTO(" +
           "a.idAsignacion, a.esPrincipal, a.fechaAsignacion, a.activo, " +
           "prof.idProfesional, uProf.nombre, uProf.apellidos, uProf.email, " +
           "prof.numeroColegiado, prof.especialidad, " +
           "pac.idPaciente, uPac.nombre, uPac.apellidos, uPac.email, pac.fechaNacimiento, " +
           "o.idOrganizacion, o.nombreOrganizacion) " +
           "FROM AsignacionProfesionalPaciente a " +
           "JOIN a.profesional prof " +
           "JOIN prof.usuario uProf " +
           "JOIN a.paciente pac " +
           "JOIN pac.usuario uPac " +
           "JOIN uProf.organizacion o " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "AND (:soloActivas = false OR a.activo = true) " +
           "ORDER BY a.fechaAsignacion DESC")
    List<AsignacionDetalleDTO> findDetalleByOrganizacion(
            @Param("idOrganizacion") Integer idOrganizacion,
            @Param("soloActivas") boolean soloActivas);

    /**
     * Verifica si existe una asignación activa entre un profesional y un paciente específicos.
     *
     * @param idProfesional ID del profesional
     * @param idPaciente ID del paciente
     * @return true si existe una asignación activa, false en caso contrario
     */
    @Query("SELECT COUNT(a) > 0 " +
           "FROM AsignacionProfesionalPaciente a " +
           "WHERE a.profesional.idProfesional = :idProfesional " +
           "AND a.paciente.idPaciente = :idPaciente " +
           "AND a.activo = true")
    boolean existeAsignacionActiva(
            @Param("idProfesional") Integer idProfesional,
            @Param("idPaciente") Integer idPaciente);
}
