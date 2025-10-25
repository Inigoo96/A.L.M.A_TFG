package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.AsignacionProfesionalPacienteRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AsignacionProfesionalPacienteServiceImpl implements AsignacionProfesionalPacienteService {

    @Autowired
    private AsignacionProfesionalPacienteRepository asignacionRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public AsignacionProfesionalPaciente save(AsignacionProfesionalPaciente asignacionRequest) {
        if (asignacionRequest.getProfesional() == null || asignacionRequest.getProfesional().getId() == null) {
            throw new IllegalStateException("El ID del profesional es obligatorio");
        }
        if (asignacionRequest.getPaciente() == null || asignacionRequest.getPaciente().getId() == null) {
            throw new IllegalStateException("El ID del paciente es obligatorio");
        }

        Profesional profesional = profesionalRepository.findById(asignacionRequest.getProfesional().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + asignacionRequest.getProfesional().getId()));

        Paciente paciente = pacienteRepository.findById(asignacionRequest.getPaciente().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + asignacionRequest.getPaciente().getId()));

        AsignacionProfesionalPaciente nuevaAsignacion = new AsignacionProfesionalPaciente();
        nuevaAsignacion.setProfesional(profesional);
        nuevaAsignacion.setPaciente(paciente);
        nuevaAsignacion.setEsPrincipal(asignacionRequest.getEsPrincipal());

        return asignacionRepository.save(nuevaAsignacion);
    }

    @Override
    public Optional<AsignacionProfesionalPaciente> findById(Integer id) {
        return asignacionRepository.findById(id);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_Id(pacienteId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByProfesionalId(Integer profesionalId) {
        return asignacionRepository.findByProfesional_Id(profesionalId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByPacienteIdAndOrganizacionId(Integer pacienteId, Integer organizacionId) {
        return asignacionRepository.findByPaciente_IdAndPaciente_Usuario_Organizacion_Id(pacienteId, organizacionId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findByProfesionalIdAndOrganizacionId(Integer profesionalId, Integer organizacionId) {
        return asignacionRepository.findByProfesional_IdAndProfesional_Usuario_Organizacion_Id(profesionalId, organizacionId);
    }

    @Override
    public List<AsignacionProfesionalPaciente> findActivasByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_IdAndActivoTrue(pacienteId);
    }

    @Override
    public Optional<AsignacionProfesionalPaciente> findPrincipalByPacienteId(Integer pacienteId) {
        return asignacionRepository.findByPaciente_IdAndEsPrincipalTrueAndActivoTrue(pacienteId);
    }

    @Override
    public void deleteById(Integer id) {
        asignacionRepository.deleteById(id);
    }

    @Override
    public AsignacionProfesionalPaciente deactivateAsignacion(Integer id) {
        AsignacionProfesionalPaciente asignacion = asignacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con id: " + id));
        asignacion.setActivo(false);
        return asignacionRepository.save(asignacion);
    }

    @Override
    public boolean isAsignacionActiva(Long idProfesional, Long idPaciente) {
        if (idProfesional == null || idPaciente == null) {
            return false;
        }

        return asignacionRepository.existeAsignacionActiva(
                Math.toIntExact(idProfesional),
                Math.toIntExact(idPaciente)
        );
    }
}
