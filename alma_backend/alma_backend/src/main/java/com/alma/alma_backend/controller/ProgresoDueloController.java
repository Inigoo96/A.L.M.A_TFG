package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.FaseDueloDTO;
import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.dto.ProgresoDueloResponseDTO;
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
        ProgresoDueloResponseDTO progreso = progresoDueloService.registrarProgreso(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(progreso);
    }

    @GetMapping("/fases")
    public ResponseEntity<List<FaseDueloDTO>> obtenerFasesDuelo() {
        logger.info("Solicitando catálogo de fases del duelo");
        List<FaseDueloDTO> fases = progresoDueloService.obtenerTodasLasFases();
        return ResponseEntity.ok(fases);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<ProgresoDueloResponseDTO> obtenerProgresoPorId(@PathVariable Integer id) {
        logger.debug("Obteniendo progreso ID: {}", id);
        ProgresoDueloResponseDTO progreso = progresoDueloService.obtenerProgresoPorId(id);
        return ResponseEntity.ok(progreso);
    }

    @GetMapping("/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorPaciente(@PathVariable Integer idPaciente) {
        logger.debug("Obteniendo progresos del paciente ID: {}", idPaciente);
        List<ProgresoDueloResponseDTO> progresos = progresoDueloService.obtenerProgresosPorPaciente(idPaciente);
        return ResponseEntity.ok(progresos);
    }

    @GetMapping("/profesional/{idProfesional}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorProfesional(@PathVariable Integer idProfesional) {
        logger.debug("Obteniendo progresos registrados por profesional ID: {}", idProfesional);
        List<ProgresoDueloResponseDTO> progresos = progresoDueloService.obtenerProgresosPorProfesional(idProfesional);
        return ResponseEntity.ok(progresos);
    }

    @GetMapping("/paciente/{idPaciente}/rango")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProgresoDueloResponseDTO>> obtenerProgresosPorRangoFecha(
            @PathVariable Integer idPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        logger.debug("Obteniendo progresos del paciente ID: {} entre {} y {}", idPaciente, fechaInicio, fechaFin);
        List<ProgresoDueloResponseDTO> progresos = progresoDueloService.obtenerProgresosPorPacienteYRangoFecha(
                idPaciente, fechaInicio, fechaFin);
        return ResponseEntity.ok(progresos);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<ProgresoDueloResponseDTO> actualizarProgreso(
            @PathVariable Integer id,
            @Valid @RequestBody ProgresoDueloRequestDTO request) {
        logger.info("Actualizando progreso ID: {}", id);
        ProgresoDueloResponseDTO progreso = progresoDueloService.actualizarProgreso(id, request);
        return ResponseEntity.ok(progreso);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> eliminarProgreso(@PathVariable Integer id) {
        logger.info("Eliminando progreso ID: {}", id);
        progresoDueloService.eliminarProgreso(id);
        return ResponseEntity.noContent().build();
    }
}
