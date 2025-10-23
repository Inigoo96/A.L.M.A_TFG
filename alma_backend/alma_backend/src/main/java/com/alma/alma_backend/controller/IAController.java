package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.*;
import com.alma.alma_backend.service.IAService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ia")
public class IAController {

    private static final Logger logger = LoggerFactory.getLogger(IAController.class);

    @Autowired
    private IAService iaService;

    @PostMapping("/sesion")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<SesionInteraccionResponseDTO> iniciarSesion(
            @Valid @RequestBody IniciarSesionIARequestDTO request) {
        logger.info("Iniciando sesión de IA para paciente ID: {}", request.getIdPaciente());
        SesionInteraccionResponseDTO sesion = iaService.iniciarSesion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(sesion);
    }

    @PostMapping("/mensaje")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<MensajeIAResponseDTO> enviarMensaje(
            @Valid @RequestBody EnviarMensajeIARequestDTO request) {
        logger.debug("Enviando mensaje a sesión ID: {}", request.getIdSesion());
        MensajeIAResponseDTO mensaje = iaService.enviarMensaje(request);
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/sesion/finalizar")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL')")
    public ResponseEntity<SesionInteraccionResponseDTO> finalizarSesion(
            @Valid @RequestBody FinalizarSesionIARequestDTO request) {
        logger.info("Finalizando sesión ID: {}", request.getIdSesion());
        SesionInteraccionResponseDTO sesion = iaService.finalizarSesion(request);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/sesion/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<SesionInteraccionResponseDTO> obtenerSesionPorId(@PathVariable Integer id) {
        logger.debug("Obteniendo sesión ID: {}", id);
        SesionInteraccionResponseDTO sesion = iaService.obtenerSesionPorId(id);
        return ResponseEntity.ok(sesion);
    }

    @GetMapping("/sesiones/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<SesionInteraccionResponseDTO>> obtenerSesionesPorPaciente(
            @PathVariable Integer idPaciente) {
        logger.debug("Obteniendo sesiones del paciente ID: {}", idPaciente);
        List<SesionInteraccionResponseDTO> sesiones = iaService.obtenerSesionesPorPaciente(idPaciente);
        return ResponseEntity.ok(sesiones);
    }

    @GetMapping("/sesion/{idSesion}/mensajes")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MensajeIAResponseDTO>> obtenerMensajesPorSesion(
            @PathVariable Integer idSesion) {
        logger.debug("Obteniendo mensajes de sesión ID: {}", idSesion);
        List<MensajeIAResponseDTO> mensajes = iaService.obtenerMensajesPorSesion(idSesion);
        return ResponseEntity.ok(mensajes);
    }

    @GetMapping("/alertas/profesional/{idProfesional}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<SesionInteraccionResponseDTO>> obtenerSesionesConAlertas(
            @PathVariable Integer idProfesional) {
        logger.debug("Obteniendo sesiones con alertas para profesional ID: {}", idProfesional);
        List<SesionInteraccionResponseDTO> sesiones = iaService.obtenerSesionesConAlertasPorProfesional(idProfesional);
        return ResponseEntity.ok(sesiones);
    }

    @PutMapping("/sesion/{idSesion}/notas")
    @PreAuthorize("hasRole('PROFESIONAL')")
    public ResponseEntity<SesionInteraccionResponseDTO> agregarNotasProfesional(
            @PathVariable Integer idSesion,
            @RequestBody Map<String, String> body) {
        logger.info("Agregando notas a sesión ID: {}", idSesion);
        String notas = body.get("notas");
        SesionInteraccionResponseDTO sesion = iaService.agregarNotasProfesional(idSesion, notas);
        return ResponseEntity.ok(sesion);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Error de estado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Estado Inválido", "message", ex.getMessage()));
    }
}