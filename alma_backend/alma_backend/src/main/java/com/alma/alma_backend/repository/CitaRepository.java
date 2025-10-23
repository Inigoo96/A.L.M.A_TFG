package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Cita;
import com.alma.alma_backend.entity.EstadoCita;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface CitaRepository extends JpaRepository<Cita, Integer> {

    List<Cita> findByPaciente_IdOrderByFechaHoraAsc(Integer idPaciente);

    List<Cita> findByProfesional_IdOrderByFechaHoraAsc(Integer idProfesional);

    List<Cita> findByEstadoOrderByFechaHoraAsc(EstadoCita estado);

    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :idPaciente " +
           "AND c.estado = :estado ORDER BY c.fechaHora ASC")
    List<Cita> findByPacienteAndEstado(
        @Param("idPaciente") Integer idPaciente,
        @Param("estado") EstadoCita estado
    );

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :idProfesional " +
           "AND c.estado = :estado ORDER BY c.fechaHora ASC")
    List<Cita> findByProfesionalAndEstado(
        @Param("idProfesional") Integer idProfesional,
        @Param("estado") EstadoCita estado
    );

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :idProfesional " +
           "AND c.fechaHora BETWEEN :fechaInicio AND :fechaFin " +
           "AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findCitasProfesionalEnRango(
        @Param("idProfesional") Integer idProfesional,
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    @Query("SELECT c FROM Cita c WHERE c.paciente.id = :idPaciente " +
           "AND c.fechaHora >= :fechaActual " +
           "AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findCitasProximasPaciente(
        @Param("idPaciente") Integer idPaciente,
        @Param("fechaActual") LocalDateTime fechaActual
    );

    @Query("SELECT c FROM Cita c WHERE c.profesional.id = :idProfesional " +
           "AND c.fechaHora >= :fechaActual " +
           "AND c.estado IN ('PROGRAMADA', 'CONFIRMADA') " +
           "ORDER BY c.fechaHora ASC")
    List<Cita> findCitasProximasProfesional(
        @Param("idProfesional") Integer idProfesional,
        @Param("fechaActual") LocalDateTime fechaActual
    );
}