package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.InformeEmocionalRequestDTO;
import com.alma.alma_backend.entity.GeneradoPor;
import com.alma.alma_backend.entity.InformeEmocional;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Profesional;
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

@Service
public class InformeEmocionalServiceImpl implements InformeEmocionalService {

    @Autowired
    private InformeEmocionalRepository informeEmocionalRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    public InformeEmocional generarInformeManual(InformeEmocionalRequestDTO request, Authentication authentication) {
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

        return informeEmocionalRepository.save(informe);
    }

    @Override
    public List<InformeEmocional> findInformesByPaciente(Integer pacienteId, Authentication authentication) {
        return informeEmocionalRepository.findByPacienteIdOrderByFechaGeneracionDesc(pacienteId);
    }

    @Override
    public List<InformeEmocional> findInformesByProfesional(Integer profesionalId, Authentication authentication) {
        return informeEmocionalRepository.findByProfesionalIdOrderByFechaGeneracionDesc(profesionalId);
    }
}
