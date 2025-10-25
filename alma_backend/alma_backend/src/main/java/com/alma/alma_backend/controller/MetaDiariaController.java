package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ActualizarMetaRequestDTO;
import com.alma.alma_backend.dto.EstadisticasMetasDTO;
import com.alma.alma_backend.dto.MetaDiariaRequestDTO;
import com.alma.alma_backend.dto.MetaDiariaResponseDTO;
import com.alma.alma_backend.entity.EstadoMeta;
import com.alma.alma_backend.mapper.MetaDiariaMapper;
import com.alma.alma_backend.service.MetaDiariaService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/metas")
public class MetaDiariaController {

    private static final Logger logger = LoggerFactory.getLogger(MetaDiariaController.class);

    @Autowired
    private MetaDiariaService metaDiariaService;

    @PostMapping
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<MetaDiariaResponseDTO> crearMeta(@Valid @RequestBody MetaDiariaRequestDTO request) {
        logger.info("Creando meta diaria para paciente ID: {}", request.getIdPaciente());
        MetaDiariaResponseDTO meta = MetaDiariaMapper.toResponse(metaDiariaService.crearMeta(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(meta);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<MetaDiariaResponseDTO> obtenerMetaPorId(@PathVariable Integer id) {
        logger.debug("Obteniendo meta ID: {}", id);
        return ResponseEntity.ok(MetaDiariaMapper.toResponse(metaDiariaService.obtenerMetaPorId(id)));
    }

    @GetMapping("/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MetaDiariaResponseDTO>> obtenerMetasPorPaciente(
            @PathVariable Integer idPaciente) {
        logger.debug("Obteniendo metas del paciente ID: {}", idPaciente);
        return ResponseEntity.ok(metaDiariaService.obtenerMetasPorPaciente(idPaciente).stream()
                .map(MetaDiariaMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/paciente/{idPaciente}/hoy")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MetaDiariaResponseDTO>> obtenerMetasHoy(
            @PathVariable Integer idPaciente) {
        logger.debug("Obteniendo metas de hoy para paciente ID: {}", idPaciente);
        return ResponseEntity.ok(metaDiariaService.obtenerMetasHoyPorPaciente(idPaciente).stream()
                .map(MetaDiariaMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/paciente/{idPaciente}/rango")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MetaDiariaResponseDTO>> obtenerMetasEnRango(
            @PathVariable Integer idPaciente,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        logger.debug("Obteniendo metas del paciente ID: {} entre {} y {}",
                idPaciente, fechaInicio, fechaFin);
        return ResponseEntity.ok(metaDiariaService.obtenerMetasEnRango(idPaciente, fechaInicio, fechaFin).stream()
                .map(MetaDiariaMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/paciente/{idPaciente}/estado/{estado}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MetaDiariaResponseDTO>> obtenerMetasPorEstado(
            @PathVariable Integer idPaciente,
            @PathVariable EstadoMeta estado) {
        logger.debug("Obteniendo metas con estado {} para paciente ID: {}", estado, idPaciente);
        return ResponseEntity.ok(metaDiariaService.obtenerMetasPorEstado(idPaciente, estado).stream()
                .map(MetaDiariaMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/actualizar-estado")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<MetaDiariaResponseDTO> actualizarEstadoMeta(
            @Valid @RequestBody ActualizarMetaRequestDTO request) {
        logger.info("Actualizando estado de meta ID: {} a {}", request.getIdMeta(), request.getEstado());
        return ResponseEntity.ok(MetaDiariaMapper.toResponse(metaDiariaService.actualizarEstadoMeta(request)));
    }

    @PutMapping("/{id}/completar")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL')")
    public ResponseEntity<MetaDiariaResponseDTO> completarMeta(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        logger.info("Completando meta ID: {}", id);
        String notas = body != null ? body.get("notas") : null;
        return ResponseEntity.ok(MetaDiariaMapper.toResponse(metaDiariaService.completarMeta(id, notas)));
    }

    @PutMapping("/{id}/cancelar")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<MetaDiariaResponseDTO> cancelarMeta(
            @PathVariable Integer id,
            @RequestBody(required = false) Map<String, String> body) {
        logger.info("Cancelando meta ID: {}", id);
        String notas = body != null ? body.get("notas") : null;
        return ResponseEntity.ok(MetaDiariaMapper.toResponse(metaDiariaService.cancelarMeta(id, notas)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<MetaDiariaResponseDTO> actualizarMeta(
            @PathVariable Integer id,
            @Valid @RequestBody MetaDiariaRequestDTO request) {
        logger.info("Actualizando meta ID: {}", id);
        return ResponseEntity.ok(MetaDiariaMapper.toResponse(metaDiariaService.actualizarMeta(id, request)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> eliminarMeta(@PathVariable Integer id) {
        logger.info("Eliminando meta ID: {}", id);
        metaDiariaService.eliminarMeta(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/paciente/{idPaciente}/estadisticas")
    @PreAuthorize("hasAnyRole('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<EstadisticasMetasDTO> obtenerEstadisticas(
            @PathVariable Integer idPaciente) {
        logger.debug("Obteniendo estadísticas de metas para paciente ID: {}", idPaciente);
        EstadisticasMetasDTO estadisticas = metaDiariaService.obtenerEstadisticasMetas(idPaciente);
        return ResponseEntity.ok(estadisticas);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Error de validación: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Validación Fallida", "message", ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> handleIllegalStateException(IllegalStateException ex) {
        logger.warn("Error de estado: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Estado Inválido", "message", ex.getMessage()));
    }
}