package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.FaseDueloDTO;
import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.dto.ProgresoDueloResponseDTO;
import com.alma.alma_backend.mapper.ProgresoDueloMapper;
import com.alma.alma_backend.service.ProgresoDueloService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/progreso-duelo")
public class ProgresoDueloController {

    private static final Logger logger = LoggerFactory.getLogger(ProgresoDueloController.class);

    @Autowired
    private ProgresoDueloService progresoDueloService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<ProgresoDueloResponseDTO> registrarProgreso(@Valid @RequestBody ProgresoDueloRequestDTO request) {
        logger.info("Registrando progreso de duelo para paciente ID: {}", request.getIdPaciente());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ProgresoDueloMapper.toResponse(progresoDueloService.registrarProgreso(request)));
    }

    @GetMapping("/fases")
    public ResponseEntity<List<FaseDueloDTO>> obtenerFasesDuelo() {
        logger.info("Solicitando catálogo de fases del duelo");
        return ResponseEntity.ok(progresoDueloService.obtenerTodasLasFases().stream()
                .map(ProgresoDueloMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<ProgresoDueloResponseDTO> obtenerProgresoPorId(@PathVariable Integer id) {
        logger.debug("Obteniendo progreso ID: {}", id);
        return ResponseEntity.ok(ProgresoDueloMapper.toResponse(progresoDueloService.obtenerProgresoPorId(id)));
    }

    @GetMapping("/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorPaciente(@PathVariable Integer idPaciente) {
        logger.debug("Obteniendo progresos del paciente ID: {}", idPaciente);
        return ResponseEntity.ok(progresoDueloService.obtenerProgresosPorPaciente(idPaciente).stream()
                .map(ProgresoDueloMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/profesional/{idProfesional}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorProfesional(@PathVariable Integer idProfesional) {
        logger.debug("Obteniendo progresos registrados por profesional ID: {}", idProfesional);
        return ResponseEntity.ok(progresoDueloService.obtenerProgresosPorProfesional(idProfesional).stream()
                .map(ProgresoDueloMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/paciente/{idPaciente}/rango")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorRangoFecha(
            @PathVariable Integer idPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        logger.debug("Obteniendo progresos del paciente ID: {} entre {} y {}", idPaciente, fechaInicio, fechaFin);
        return ResponseEntity.ok(progresoDueloService.obtenerProgresosPorPacienteYRangoFecha(idPaciente, fechaInicio, fechaFin).stream()
                .map(ProgresoDueloMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<ProgresoDueloResponseDTO> actualizarProgreso(
            @PathVariable Integer id,
            @Valid @RequestBody ProgresoDueloRequestDTO request) {
        logger.info("Actualizando progreso ID: {}", id);
        return ResponseEntity.ok(ProgresoDueloMapper.toResponse(progresoDueloService.actualizarProgreso(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> eliminarProgreso(@PathVariable Integer id) {
        logger.info("Eliminando progreso ID: {}", id);
        progresoDueloService.eliminarProgreso(id);
        return ResponseEntity.noContent().build();
    }
}
