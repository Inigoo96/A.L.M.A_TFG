package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ActualizarEstadoCitaDTO;
import com.alma.alma_backend.dto.CitaRequestDTO;
import com.alma.alma_backend.dto.CitaResponseDTO;
import com.alma.alma_backend.entity.EstadoCita;
import com.alma.alma_backend.service.CitaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.orm.jpa.JpaSystemException;
import java.util.Map;

import java.util.List;

@RestController
@RequestMapping("/api/citas")
public class CitaController {

    private static final Logger logger = LoggerFactory.getLogger(CitaController.class);

    @Autowired
    private CitaService citaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<CitaResponseDTO> crearCita(@Valid @RequestBody CitaRequestDTO request) {
        logger.info("Creando cita para paciente ID: {} con profesional ID: {}",
                request.getIdPaciente(), request.getIdProfesional());
        CitaResponseDTO cita = citaService.crearCita(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(cita);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<CitaResponseDTO> obtenerCitaPorId(@PathVariable Integer id) {
        logger.debug("Obteniendo cita ID: {}", id);
        CitaResponseDTO cita = citaService.obtenerCitaPorId(id);
        return ResponseEntity.ok(cita);
    }

    @GetMapping("/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPorPaciente(@PathVariable Integer idPaciente) {
        logger.debug("Obteniendo citas del paciente ID: {}", idPaciente);
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorPaciente(idPaciente);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/profesional/{idProfesional}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPorProfesional(@PathVariable Integer idProfesional) {
        logger.debug("Obteniendo citas del profesional ID: {}", idProfesional);
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorProfesional(idProfesional);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/paciente/{idPaciente}/proximas")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasProximasPaciente(@PathVariable Integer idPaciente) {
        logger.debug("Obteniendo citas próximas del paciente ID: {}", idPaciente);
        List<CitaResponseDTO> citas = citaService.obtenerCitasProximasPorPaciente(idPaciente);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/profesional/{idProfesional}/proximas")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasProximasProfesional(@PathVariable Integer idProfesional) {
        logger.debug("Obteniendo citas próximas del profesional ID: {}", idProfesional);
        List<CitaResponseDTO> citas = citaService.obtenerCitasProximasPorProfesional(idProfesional);
        return ResponseEntity.ok(citas);
    }

    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<CitaResponseDTO>> obtenerCitasPorEstado(@PathVariable EstadoCita estado) {
        logger.debug("Obteniendo citas con estado: {}", estado);
        List<CitaResponseDTO> citas = citaService.obtenerCitasPorEstado(estado);
        return ResponseEntity.ok(citas);
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<CitaResponseDTO> actualizarEstadoCita(
            @PathVariable Integer id,
            @Valid @RequestBody ActualizarEstadoCitaDTO request) {
        logger.info("Actualizando estado de cita ID: {} a {}", id, request.getEstado());
        CitaResponseDTO cita = citaService.actualizarEstadoCita(id, request);
        return ResponseEntity.ok(cita);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<CitaResponseDTO> actualizarCita(
            @PathVariable Integer id,
            @Valid @RequestBody CitaRequestDTO request) {
        logger.info("Actualizando cita ID: {}", id);
        CitaResponseDTO cita = citaService.actualizarCita(id, request);
        return ResponseEntity.ok(cita);
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> cancelarCita(@PathVariable Integer id) {
        logger.info("Cancelando cita ID: {}", id);
        citaService.cancelarCita(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> eliminarCita(@PathVariable Integer id) {
        logger.info("Eliminando cita ID: {}", id);
        citaService.eliminarCita(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(JpaSystemException.class)
    public ResponseEntity<Map<String, String>> handleJpaSystemException(JpaSystemException ex) {
        String errorMessage = ex.getMostSpecificCause().getMessage();
        if (errorMessage != null && errorMessage.contains("El profesional ya tiene una cita programada en ese horario")) {
            logger.warn("Intento de crear cita solapada detectado: {}", errorMessage);
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Conflicto de Citas", "message", "El profesional ya tiene una cita programada en ese horario."));
        }
        logger.error("Error inesperado en la capa de persistencia: {}", errorMessage, ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Error Interno del Servidor", "message", "Ocurrió un error inesperado al procesar la cita."));
    }
}
