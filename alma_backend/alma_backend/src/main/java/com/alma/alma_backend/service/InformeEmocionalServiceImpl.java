package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.InformeEmocionalRequestDTO;
import com.alma.alma_backend.dto.InformeEmocionalResponseDTO;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.InformeEmocionalRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class InformeEmocionalServiceImpl implements InformeEmocionalService {

    @Autowired
    private InformeEmocionalRepository informeEmocionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    public InformeEmocionalResponseDTO generarInformeManual(InformeEmocionalRequestDTO request, Authentication authentication) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));
        Profesional profesional = profesionalRepository.findById(request.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado"));

        InformeEmocional informe = new InformeEmocional();
        informe.setPaciente(paciente);
        informe.setProfesional(profesional);
        informe.setPeriodoInicio(request.getPeriodoInicio());
        informe.setPeriodoFin(request.getPeriodoFin());
        informe.setGeneradoPor(GeneradoPor.MANUAL);

        // Lógica de generación de contenido del informe (simplificada)
        Map<String, Object> contenido = new HashMap<>();
        contenido.put("resumen", "Informe generado manualmente.");
        informe.setContenidoInforme(contenido);

        InformeEmocional savedInforme = informeEmocionalRepository.save(informe);
        return mapToInformeEmocionalResponseDTO(savedInforme);
    }

    @Override
    public List<InformeEmocionalResponseDTO> findInformesByPaciente(Integer pacienteId, Authentication authentication) {
        return informeEmocionalRepository.findByPacienteIdOrderByFechaGeneracionDesc(pacienteId).stream()
                .map(this::mapToInformeEmocionalResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InformeEmocionalResponseDTO> findInformesByProfesional(Integer profesionalId, Authentication authentication) {
        return informeEmocionalRepository.findByProfesionalIdOrderByFechaGeneracionDesc(profesionalId).stream()
                .map(this::mapToInformeEmocionalResponseDTO)
                .collect(Collectors.toList());
    }

    private InformeEmocionalResponseDTO mapToInformeEmocionalResponseDTO(InformeEmocional informe) {
        InformeEmocionalResponseDTO dto = new InformeEmocionalResponseDTO();
        dto.setId(informe.getId());
        dto.setPacienteId(informe.getPaciente().getId());
        dto.setNombrePaciente(informe.getPaciente().getUsuario().getNombre());
        dto.setProfesionalId(informe.getProfesional().getId());
        dto.setNombreProfesional(informe.getProfesional().getUsuario().getNombre());
        dto.setPeriodoInicio(informe.getPeriodoInicio());
        dto.setPeriodoFin(informe.getPeriodoFin());
        dto.setContenidoInforme(informe.getContenidoInforme());
        dto.setGeneradoPor(informe.getGeneradoPor());
        dto.setFechaGeneracion(informe.getFechaGeneracion());
        return dto;
    }
}
