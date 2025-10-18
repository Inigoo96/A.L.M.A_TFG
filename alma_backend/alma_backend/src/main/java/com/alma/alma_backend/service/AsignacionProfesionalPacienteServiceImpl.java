package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.repository.AsignacionProfesionalPacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionProfesionalPacienteServiceImpl implements AsignacionProfesionalPacienteService {

    @Autowired
    private AsignacionProfesionalPacienteRepository asignacionRepository;

    @Override
    public AsignacionProfesionalPaciente save(AsignacionProfesionalPaciente asignacion) {
        return asignacionRepository.save(asignacion);
    }

    @Override
    public Optional<AsignacionProfesionalPaciente> findById(Integer id) {
        return asignacionRepository.findById(id);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_IdPaciente(pacienteId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByProfesionalId(Integer profesionalId) {
        return asignacionRepository.findByProfesional_IdProfesional(profesionalId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findActivasByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_IdPacienteAndActivoTrue(pacienteId);
    }

    @Override
    public Optional<AsignacionProfesionalPaciente> findPrincipalByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_IdPacienteAndEsPrincipalTrueAndActivoTrue(pacienteId);
    }

    @Override
    public void deleteById(Integer id) {
        asignacionRepository.deleteById(id);
    }

    @Override
    public AsignacionProfesionalPaciente deactivateAsignacion(Integer id) {
        AsignacionProfesionalPaciente asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Asignación no encontrada con id: " + id));
        asignacion.setActivo(false);
        return asignacionRepository.save(asignacion);
    }
}
