package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.ProfesionalDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalEstadisticasDTO;
import com.alma.alma_backend.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Integer> {
    Optional<Profesional> findByNumeroColegiado(String numeroColegiado);

    Optional<Profesional> findByUsuario_Id(Integer idUsuario);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.id, p.numeroColegiado, p.especialidad, p.centroSalud, " +
           "u.id, u.email, u.nombre, u.apellidos, u.tipoUsuario, u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.id, o.nombreOficial, o.cif) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion o WHERE p.id = :id")
    Optional<ProfesionalDetalleDTO> findDetalleById(@Param("id") Integer id);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.id, p.numeroColegiado, p.especialidad, p.centroSalud, " +
           "u.id, u.email, u.nombre, u.apellidos, u.tipoUsuario, u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.id, o.nombreOficial, o.cif) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion o WHERE o.id = :organizacionId")
    List<ProfesionalDetalleDTO> findDetalleByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.id, p.numeroColegiado, p.especialidad, p.centroSalud, " +
           "u.id, u.email, u.nombre, u.apellidos, u.tipoUsuario, u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.id, o.nombreOficial, o.cif) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion o WHERE o.id = :organizacionId AND u.activo = true")
    List<ProfesionalDetalleDTO> findActivosByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalEstadisticasDTO(" +
           "p.id, u.nombre, u.apellidos, u.email, p.numeroColegiado, p.especialidad, " +
           "COUNT(a.id), " +
           "SUM(CASE WHEN a.activo = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.activo = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.esPrincipal = true THEN 1 ELSE 0 END), " +
           "org.id, org.nombreOficial) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion org LEFT JOIN p.asignaciones a " +
           "WHERE org.id = :organizacionId " +
           "GROUP BY p.id, u.nombre, u.apellidos, u.email, p.numeroColegiado, p.especialidad, org.id, org.nombreOficial")
    List<ProfesionalEstadisticasDTO> findEstadisticasByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalEstadisticasDTO(" +
           "p.id, u.nombre, u.apellidos, u.email, p.numeroColegiado, p.especialidad, " +
           "COUNT(a.id), " +
           "SUM(CASE WHEN a.activo = true THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.activo = false THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN a.esPrincipal = true THEN 1 ELSE 0 END), " + // BUG CORREGIDO
           "org.id, org.nombreOficial) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion org LEFT JOIN p.asignaciones a " +
           "WHERE p.id = :id " +
           "GROUP BY p.id, u.nombre, u.apellidos, u.email, p.numeroColegiado, p.especialidad, org.id, org.nombreOficial")
    Optional<ProfesionalEstadisticasDTO> findEstadisticasById(@Param("id") Integer id);

    @Query("SELECT new com.alma.alma_backend.dto.ProfesionalDetalleDTO(" +
           "p.id, p.numeroColegiado, p.especialidad, p.centroSalud, " +
           "u.id, u.email, u.nombre, u.apellidos, u.tipoUsuario, u.activo, u.fechaRegistro, u.ultimoAcceso, " +
           "o.id, o.nombreOficial, o.cif) " +
           "FROM Profesional p JOIN p.usuario u JOIN u.organizacion o " +
           "WHERE p.especialidad = :especialidad AND o.id = :organizacionId")
    List<ProfesionalDetalleDTO> findByEspecialidadAndOrganizacion(
        @Param("especialidad") String especialidad,
        @Param("organizacionId") Integer organizacionId
    );
}
