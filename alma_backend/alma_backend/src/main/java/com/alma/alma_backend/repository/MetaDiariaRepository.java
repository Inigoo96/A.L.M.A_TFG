package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.EstadoMeta;
import com.alma.alma_backend.entity.MetaDiaria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface MetaDiariaRepository extends JpaRepository<MetaDiaria, Integer> {

    List<MetaDiaria> findByPacienteIdOrderByFechaAsignadaDesc(Integer idPaciente);

    List<MetaDiaria> findByPacienteIdAndFechaAsignadaOrderByFechaCreacionDesc(
        Integer idPaciente,
        LocalDate fechaAsignada
    );

    List<MetaDiaria> findByPacienteIdAndEstadoOrderByFechaAsignadaDesc(
        Integer idPaciente,
        EstadoMeta estado
    );

    @Query("SELECT m FROM MetaDiaria m WHERE m.paciente.id = :idPaciente " +
           "AND m.fechaAsignada = CURRENT_DATE ORDER BY m.fechaCreacion DESC")
    List<MetaDiaria> findMetasHoyPorPaciente(@Param("idPaciente") Integer idPaciente);

    @Query("SELECT m FROM MetaDiaria m WHERE m.paciente.id = :idPaciente " +
           "AND m.fechaAsignada BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fechaAsignada DESC, m.fechaCreacion DESC")
    List<MetaDiaria> findMetasEnRango(
        @Param("idPaciente") Integer idPaciente,
        @Param("fechaInicio") LocalDate fechaInicio,
        @Param("fechaFin") LocalDate fechaFin
    );

    @Query("SELECT COUNT(m) FROM MetaDiaria m WHERE m.paciente.id = :idPaciente " +
           "AND m.estado = :estado")
    Long countByPacienteIdAndEstado(
        @Param("idPaciente") Integer idPaciente,
        @Param("estado") EstadoMeta estado
    );

    @Query("SELECT m FROM MetaDiaria m WHERE m.paciente.id = :idPaciente " +
           "AND m.fechaAsignada = :fecha " +
           "AND UPPER(TRIM(m.textoMeta)) = UPPER(TRIM(:textoMeta)) " +
           "AND m.estado != 'CANCELADA'")
    List<MetaDiaria> findMetaDuplicada(
        @Param("idPaciente") Integer idPaciente,
        @Param("fecha") LocalDate fecha,
        @Param("textoMeta") String textoMeta
    );
}