package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.InformeEmocionalRequestDTO;
import com.alma.alma_backend.dto.InformeEmocionalResponseDTO;
import com.alma.alma_backend.mapper.InformeEmocionalMapper;
import com.alma.alma_backend.service.InformeEmocionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/informes")
public class InformeEmocionalController {

    @Autowired
    private InformeEmocionalService informeEmocionalService;

    @PostMapping("/generar-manual")
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<InformeEmocionalResponseDTO> generarInformeManual(@RequestBody InformeEmocionalRequestDTO request, Authentication authentication) {
        return ResponseEntity.ok(InformeEmocionalMapper.toResponse(
                informeEmocionalService.generarInformeManual(request, authentication)));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL')")
    public ResponseEntity<List<InformeEmocionalResponseDTO>> getInformesByPaciente(@PathVariable Integer pacienteId, Authentication authentication) {
        return ResponseEntity.ok(informeEmocionalService.findInformesByPaciente(pacienteId, authentication).stream()
                .map(InformeEmocionalMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/profesional/{profesionalId}")
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<List<InformeEmocionalResponseDTO>> getInformesByProfesional(@PathVariable Integer profesionalId, Authentication authentication) {
        return ResponseEntity.ok(informeEmocionalService.findInformesByProfesional(profesionalId, authentication).stream()
                .map(InformeEmocionalMapper::toResponse)
                .collect(Collectors.toList()));
}
}
