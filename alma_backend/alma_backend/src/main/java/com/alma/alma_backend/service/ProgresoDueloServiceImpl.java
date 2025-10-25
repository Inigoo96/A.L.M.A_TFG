package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.entity.FaseDuelo;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.ProgresoDuelo;
import com.alma.alma_backend.repository.FaseDueloRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.repository.ProgresoDueloRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgresoDueloServiceImpl implements ProgresoDueloService {

    @Autowired
    private ProgresoDueloRepository progresoDueloRepository;
    @Autowired
    private PacienteRepository pacienteRepository;
    @Autowired
    private ProfesionalRepository profesionalRepository;
    @Autowired
    private FaseDueloRepository faseDueloRepository;

    @Override
    @Transactional
    public ProgresoDuelo registrarProgreso(ProgresoDueloRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new EntityNotFoundException("Paciente no encontrado"));
        Profesional profesional = profesionalRepository.findById(request.getIdProfesional())
                .orElseThrow(() -> new EntityNotFoundException("Profesional no encontrado"));
        FaseDuelo faseDuelo = faseDueloRepository.findById(request.getIdFaseDuelo())
                .orElseThrow(() -> new EntityNotFoundException("Fase de duelo no encontrada"));

        ProgresoDuelo progreso = new ProgresoDuelo();
        progreso.setPaciente(paciente);
        progreso.setProfesional(profesional);
        progreso.setFaseDuelo(faseDuelo);
        // Asignación directa: Spring convierte el String del DTO a Enum
        progreso.setEstadoEmocional(request.getEstadoEmocional());
        progreso.setNotas(request.getNotas());

        return progresoDueloRepository.save(progreso);
    }

    @Override
    @Transactional(readOnly = true)
    public ProgresoDuelo obtenerProgresoPorId(Integer id) {
        return progresoDueloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Progreso no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDuelo> obtenerProgresosPorPaciente(Integer idPaciente) {
        return progresoDueloRepository.findByPacienteId(idPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDuelo> obtenerProgresosPorProfesional(Integer idProfesional) {
        return progresoDueloRepository.findByProfesionalId(idProfesional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDuelo> obtenerProgresosPorPacienteYRangoFecha(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return progresoDueloRepository.findByPacienteIdAndFechaRegistroBetween(idPaciente, fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public ProgresoDuelo actualizarProgreso(Integer id, ProgresoDueloRequestDTO request) {
        ProgresoDuelo progreso = progresoDueloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Progreso no encontrado"));

        FaseDuelo faseDuelo = faseDueloRepository.findById(request.getIdFaseDuelo())
                .orElseThrow(() -> new EntityNotFoundException("Fase de duelo no encontrada"));

        progreso.setFaseDuelo(faseDuelo);
        // Asignación directa: Spring convierte el String del DTO a Enum
        progreso.setEstadoEmocional(request.getEstadoEmocional());
        progreso.setNotas(request.getNotas());

        return progresoDueloRepository.save(progreso);
    }

    @Override
    @Transactional
    public void eliminarProgreso(Integer id) {
        progresoDueloRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaseDuelo> obtenerTodasLasFases() {
        return faseDueloRepository.findAllByOrderByOrdenFaseAsc();
    }
}
