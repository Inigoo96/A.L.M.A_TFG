package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;

import java.util.List;
import java.util.Optional;

public interface AsignacionProfesionalPacienteService {

    AsignacionProfesionalPaciente save(AsignacionProfesionalPaciente asignacion);

    Optional<AsignacionProfesionalPaciente> findById(Integer id);

    List<AsignacionProfesionalPaciente> findByPacienteId(Integer pacienteId);

    List<AsignacionProfesionalPaciente> findByProfesionalId(Integer profesionalId);

    List<AsignacionProfesionalPaciente> findActivasByPacienteId(Integer pacienteId);

    Optional<AsignacionProfesionalPaciente> findPrincipalByPacienteId(Integer pacienteId);

    void deleteById(Integer id);

    // Método para desactivar una asignación (soft delete)
    AsignacionProfesionalPaciente deactivateAsignacion(Integer id);
}
