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

    Optional<Paciente> findByUsuario_Id(Integer idUsuario);

    /**
     * Alias para compatibilidad con código existente.
     */
    default Optional<Paciente> findByUsuario_IdUsuario(Integer idUsuario) {
        return findByUsuario_Id(idUsuario);
    }

    /**
     * Busca pacientes por organización usando la navegación de relaciones.
     */
    List<Paciente> findByUsuario_Organizacion_Id(Integer organizacionId);

    /**
     * Alias para compatibilidad con código existente.
     */
    default List<Paciente> findByUsuario_Organizacion_IdOrganizacion(Integer organizacionId) {
        return findByUsuario_Organizacion_Id(organizacionId);
    }

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo) " +
           "FROM Paciente pac JOIN pac.usuario u WHERE pac.id = :id")
    Optional<PacienteDetalleDTO> findDetalleById(@Param("id") Integer id);

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo) " +
           "FROM Paciente pac JOIN pac.usuario u WHERE u.organizacion.id = :organizacionId")
    List<PacienteDetalleDTO> findDetalleByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo) " +
           "FROM Paciente pac JOIN pac.usuario u " +
           "WHERE u.organizacion.id = :organizacionId AND u.activo = true")
    List<PacienteDetalleDTO> findActivosByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo, " +
           "u.id, u.tipoUsuario, u.fechaRegistro, u.ultimoAcceso, " +
           "org.id, org.nombreOficial, org.cif) " +
           "FROM Paciente pac JOIN pac.usuario u JOIN u.organizacion org JOIN pac.asignaciones a " +
           "WHERE a.profesional.id = :profesionalId AND a.activo = :activo")
    List<PacienteDetalleDTO> findPacientesByProfesional(
        @Param("profesionalId") Integer profesionalId,
        @Param("activo") boolean activo
    );

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo) " +
           "FROM Paciente pac JOIN pac.usuario u " +
           "WHERE u.organizacion.id = :organizacionId " +
           "AND NOT EXISTS (SELECT 1 FROM AsignacionProfesionalPaciente a WHERE a.paciente.id = pac.id AND a.activo = true)")
    List<PacienteDetalleDTO> findSinAsignarByOrganizacion(@Param("organizacionId") Integer organizacionId);

    @Query("SELECT new com.alma.alma_backend.dto.PacienteDetalleDTO(" +
           "pac.id, u.nombre, u.apellidos, u.email, pac.tarjetaSanitaria, " +
           "pac.fechaNacimiento, pac.genero, u.activo) " +
           "FROM Paciente pac JOIN pac.usuario u " +
           "WHERE u.organizacion.id = :organizacionId " +
           "AND (LOWER(u.nombre) LIKE LOWER(CONCAT('%', :nombre, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :nombre, '%')))")
    List<PacienteDetalleDTO> searchByNombreAndOrganizacion(
        @Param("nombre") String nombre,
        @Param("organizacionId") Integer organizacionId
    );
}