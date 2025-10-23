package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.EstadoSesionChat;
import com.alma.alma_backend.entity.SesionChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SesionChatRepository extends JpaRepository<SesionChat, Integer> {

    List<SesionChat> findByPaciente_IdOrderByUltimaActividadDesc(Integer idPaciente);

    List<SesionChat> findByProfesional_IdOrderByUltimaActividadDesc(Integer idProfesional);

    List<SesionChat> findByEstadoOrderByUltimaActividadDesc(EstadoSesionChat estado);

    @Query("SELECT s FROM SesionChat s WHERE s.paciente.id = :idPaciente " +
           "AND s.profesional.id = :idProfesional " +
           "AND s.estado = :estado")
    Optional<SesionChat> findByPacienteAndProfesionalAndEstado(
        @Param("idPaciente") Integer idPaciente,
        @Param("idProfesional") Integer idProfesional,
        @Param("estado") EstadoSesionChat estado
    );

    @Query("SELECT s FROM SesionChat s WHERE s.paciente.id = :idPaciente " +
           "AND s.estado = :estado ORDER BY s.ultimaActividad DESC")
    List<SesionChat> findByPacienteAndEstado(
        @Param("idPaciente") Integer idPaciente,
        @Param("estado") EstadoSesionChat estado
    );

    @Query("SELECT s FROM SesionChat s WHERE s.profesional.id = :idProfesional " +
           "AND s.estado = :estado ORDER BY s.ultimaActividad DESC")
    List<SesionChat> findByProfesionalAndEstado(
        @Param("idProfesional") Integer idProfesional,
        @Param("estado") EstadoSesionChat estado
    );
}