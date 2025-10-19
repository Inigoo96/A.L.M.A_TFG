package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;

import java.util.List;
import java.util.Optional;

public interface AsignacionProfesionalPacienteService {

    AsignacionProfesionalPaciente save(AsignacionProfesionalPaciente asignacion);

    Optional<AsignacionProfesionalPaciente> findById(Integer id);

    List<AsignacionProfesionalPaciente> findByPacienteId(Integer pacienteId);

    List<AsignacionProfesionalPaciente> findByProfesionalId(Integer profesionalId);

    /**
     * Busca las asignaciones de un paciente, asegurando que pertenezca a la organización especificada.
     * @param pacienteId ID del paciente
     * @param organizacionId ID de la organización
     * @return Lista de asignaciones
     */
    List<AsignacionProfesionalPaciente> findByPacienteIdAndOrganizacionId(Integer pacienteId, Integer organizacionId);

    /**
     * Busca las asignaciones de un profesional, asegurando que pertenezca a la organización especificada.
     * @param profesionalId ID del profesional
     * @param organizacionId ID de la organización
     * @return Lista de asignaciones
     */
    List<AsignacionProfesionalPaciente> findByProfesionalIdAndOrganizacionId(Integer profesionalId, Integer organizacionId);


    List<AsignacionProfesionalPaciente> findActivasByPacienteId(Integer pacienteId);

    Optional<AsignacionProfesionalPaciente> findPrincipalByPacienteId(Integer pacienteId);

    void deleteById(Integer id);

    // Método para desactivar una asignación (soft delete)
    AsignacionProfesionalPaciente deactivateAsignacion(Integer id);
}
