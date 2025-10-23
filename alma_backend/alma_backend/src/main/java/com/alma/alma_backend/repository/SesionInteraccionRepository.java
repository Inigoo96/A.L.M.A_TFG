package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.EstadoSesion;
import com.alma.alma_backend.entity.SesionInteraccion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SesionInteraccionRepository extends JpaRepository<SesionInteraccion, Integer> {

    List<SesionInteraccion> findByPacienteIdOrderByFechaInicioDesc(Integer idPaciente);

    List<SesionInteraccion> findByProfesionalIdOrderByFechaInicioDesc(Integer idProfesional);

    List<SesionInteraccion> findByEstadoOrderByFechaInicioDesc(EstadoSesion estado);

    @Query("SELECT s FROM SesionInteraccion s WHERE s.paciente.id = :idPaciente " +
           "AND s.estado = :estado ORDER BY s.fechaInicio DESC")
    List<SesionInteraccion> findByPacienteAndEstado(
        @Param("idPaciente") Integer idPaciente,
        @Param("estado") EstadoSesion estado
    );

    @Query("SELECT s FROM SesionInteraccion s WHERE s.paciente.id = :idPaciente " +
           "AND s.fechaInicio BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY s.fechaInicio DESC")
    List<SesionInteraccion> findSesionesPacienteEnRango(
        @Param("idPaciente") Integer idPaciente,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT s FROM SesionInteraccion s WHERE s.profesional.id = :idProfesional " +
           "AND s.alertasGeneradas IS NOT NULL " +
           "ORDER BY s.fechaInicio DESC")
    List<SesionInteraccion> findSesionesConAlertasPorProfesional(
        @Param("idProfesional") Integer idProfesional
    );

    @Query("SELECT COUNT(s) FROM SesionInteraccion s WHERE s.paciente.id = :idPaciente")
    Long countByPacienteId(@Param("idPaciente") Integer idPaciente);
}