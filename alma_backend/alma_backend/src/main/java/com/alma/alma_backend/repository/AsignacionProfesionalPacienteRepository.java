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

    // Métodos de búsqueda simples (potencialmente inseguros si no se usan con cuidado)
    List<AsignacionProfesionalPaciente> findByPaciente_IdPaciente(Integer idPaciente);
    List<AsignacionProfesionalPaciente> findByProfesional_IdProfesional(Integer idProfesional);

    // --- MÉTODOS OPTIMIZADOS Y SEGUROS ---

    /**
     * Busca las asignaciones de un paciente, asegurando que pertenezca a la organización especificada.
     * @param idPaciente ID del paciente
     * @param idOrganizacion ID de la organización
     * @return Lista de asignaciones
     */
    List<AsignacionProfesionalPaciente> findByPaciente_IdPacienteAndPaciente_Usuario_Organizacion_IdOrganizacion(Integer idPaciente, Integer idOrganizacion);

    /**
     * Busca las asignaciones de un profesional, asegurando que pertenezca a la organización especificada.
     * @param idProfesional ID del profesional
     * @param idOrganizacion ID de la organización
     * @return Lista de asignaciones
     */
    List<AsignacionProfesionalPaciente> findByProfesional_IdProfesionalAndProfesional_Usuario_Organizacion_IdOrganizacion(Integer idProfesional, Integer idOrganizacion);


    // --- Otros métodos existentes ---

    List<AsignacionProfesionalPaciente> findByPaciente_IdPacienteAndActivoTrue(Integer idPaciente);
    List<AsignacionProfesionalPaciente> findByProfesional_IdProfesionalAndActivoTrue(Integer idProfesional);
    Optional<AsignacionProfesionalPaciente> findByPaciente_IdPacienteAndEsPrincipalTrueAndActivoTrue(Integer idPaciente);

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

    @Query("SELECT COUNT(a) > 0 " +
           "FROM AsignacionProfesionalPaciente a " +
           "WHERE a.profesional.idProfesional = :idProfesional " +
           "AND a.paciente.idPaciente = :idPaciente " +
           "AND a.activo = true")
    boolean existeAsignacionActiva(
            @Param("idProfesional") Integer idProfesional,
            @Param("idPaciente") Integer idPaciente);
}
