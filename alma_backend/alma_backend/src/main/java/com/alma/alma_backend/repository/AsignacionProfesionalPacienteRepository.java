package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
