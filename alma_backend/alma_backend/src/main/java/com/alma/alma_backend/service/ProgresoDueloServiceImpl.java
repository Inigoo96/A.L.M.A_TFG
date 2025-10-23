package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.FaseDueloDTO;
import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.dto.ProgresoDueloResponseDTO;
import com.alma.alma_backend.entity.*;
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
import java.util.stream.Collectors;

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
    public ProgresoDueloResponseDTO registrarProgreso(ProgresoDueloRequestDTO request) {
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

        return convertirADTO(progresoDueloRepository.save(progreso));
    }

    @Override
    @Transactional(readOnly = true)
    public ProgresoDueloResponseDTO obtenerProgresoPorId(Integer id) {
        return progresoDueloRepository.findById(id)
                .map(this::convertirADTO)
                .orElseThrow(() -> new EntityNotFoundException("Progreso no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDueloResponseDTO> obtenerProgresosPorPaciente(Integer idPaciente) {
        return progresoDueloRepository.findByPacienteId(idPaciente).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDueloResponseDTO> obtenerProgresosPorProfesional(Integer idProfesional) {
        return progresoDueloRepository.findByProfesionalId(idProfesional).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProgresoDueloResponseDTO> obtenerProgresosPorPacienteYRangoFecha(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return progresoDueloRepository.findByPacienteIdAndFechaRegistroBetween(idPaciente, fechaInicio, fechaFin).stream()
                .map(this::convertirADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ProgresoDueloResponseDTO actualizarProgreso(Integer id, ProgresoDueloRequestDTO request) {
        ProgresoDuelo progreso = progresoDueloRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Progreso no encontrado"));

        FaseDuelo faseDuelo = faseDueloRepository.findById(request.getIdFaseDuelo())
                .orElseThrow(() -> new EntityNotFoundException("Fase de duelo no encontrada"));

        progreso.setFaseDuelo(faseDuelo);
        // Asignación directa: Spring convierte el String del DTO a Enum
        progreso.setEstadoEmocional(request.getEstadoEmocional());
        progreso.setNotas(request.getNotas());

        return convertirADTO(progresoDueloRepository.save(progreso));
    }

    @Override
    @Transactional
    public void eliminarProgreso(Integer id) {
        progresoDueloRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FaseDueloDTO> obtenerTodasLasFases() {
        return faseDueloRepository.findAllByOrderByOrdenFaseAsc().stream()
                .map(this::convertirFaseADTO)
                .collect(Collectors.toList());
    }

    private ProgresoDueloResponseDTO convertirADTO(ProgresoDuelo progreso) {
        ProgresoDueloResponseDTO dto = new ProgresoDueloResponseDTO();
        dto.setId(progreso.getId());
        dto.setIdPaciente(progreso.getPaciente().getId());
        dto.setIdProfesional(progreso.getProfesional() != null ? progreso.getProfesional().getId() : null);
        dto.setNombreFaseDuelo(progreso.getFaseDuelo().getNombre());
        // Convertir Enum a String para la respuesta
        if (progreso.getEstadoEmocional() != null) {
            dto.setEstadoEmocional(progreso.getEstadoEmocional().name());
        }
        dto.setNotas(progreso.getNotas());
        dto.setFechaRegistro(progreso.getFechaRegistro());
        return dto;
    }

    private FaseDueloDTO convertirFaseADTO(FaseDuelo fase) {
        FaseDueloDTO dto = new FaseDueloDTO();
        dto.setId(fase.getId());
        dto.setNombre(fase.getNombre());
        dto.setDescripcion(fase.getDescripcion());
        dto.setOrden(fase.getOrdenFase());
        return dto;
    }
}
