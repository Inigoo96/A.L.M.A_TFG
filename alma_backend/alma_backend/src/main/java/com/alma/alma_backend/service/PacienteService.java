package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.PacienteDetalleDTO;
import com.alma.alma_backend.entity.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {

    // Paciente save(Paciente paciente); // Eliminado para evitar duplicidad con updatePaciente y conflictos de compilación

    Optional<Paciente> findById(Integer id);

    Optional<Paciente> findByUsuarioId(Integer usuarioId);

    List<Paciente> findAll();

    /**
     * Busca todos los pacientes que pertenecen a una organización específica.
     * @param organizacionId El ID de la organización
     * @return Lista de pacientes de esa organización
     */
    List<Paciente> findByOrganizacionId(Integer organizacionId);

    void deleteById(Integer id);

    Paciente updatePaciente(Integer id, Paciente pacienteDetails);

    // Métodos con DTOs optimizados
    Optional<PacienteDetalleDTO> findDetalleById(Integer idPaciente);

    List<PacienteDetalleDTO> findDetalleByOrganizacion(Integer idOrganizacion);

    List<PacienteDetalleDTO> findActivosByOrganizacion(Integer idOrganizacion);

    List<PacienteDetalleDTO> findPacientesByProfesional(Integer idProfesional, boolean soloActivos);

    List<PacienteDetalleDTO> findSinAsignarByOrganizacion(Integer idOrganizacion);

    List<PacienteDetalleDTO> searchByNombreAndOrganizacion(String searchTerm, Integer idOrganizacion);
}
