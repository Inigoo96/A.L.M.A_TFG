package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Paciente;

import java.util.List;
import java.util.Optional;

public interface PacienteService {

    Paciente save(Paciente paciente);

    Optional<Paciente> findById(Integer id);

    Optional<Paciente> findByUsuarioId(Integer usuarioId);

    List<Paciente> findByOrganizacionId(Integer organizacionId);

    List<Paciente> findAll();

    void deleteById(Integer id);

    Paciente updatePaciente(Integer id, Paciente pacienteDetails);
}
