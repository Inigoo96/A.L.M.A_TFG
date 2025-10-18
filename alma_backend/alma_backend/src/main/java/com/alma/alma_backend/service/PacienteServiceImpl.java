package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public Paciente save(Paciente paciente) {
        return pacienteRepository.save(paciente);
    }

    @Override
    public Optional<Paciente> findById(Integer id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public Optional<Paciente> findByUsuarioId(Integer usuarioId) {
        return pacienteRepository.findByUsuario_IdUsuario(usuarioId);
    }

    @Override
    public List<Paciente> findByOrganizacionId(Integer organizacionId) {
        return pacienteRepository.findByOrganizacion_IdOrganizacion(organizacionId);
    }

    @Override
    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public Paciente updatePaciente(Integer id, Paciente pacienteDetails) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));

        paciente.setFechaNacimiento(pacienteDetails.getFechaNacimiento());
        paciente.setGenero(pacienteDetails.getGenero());

        return pacienteRepository.save(paciente);
    }
}
