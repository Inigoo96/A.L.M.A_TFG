package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.AuditoriaDTO;
import com.alma.alma_backend.dto.CambioEstadoOrganizacionDTO;
import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.dto.OrganizacionRequestDTO;
import com.alma.alma_backend.dto.OrganizacionResponseDTO;
import com.alma.alma_backend.entity.AuditoriaAdmin;
import com.alma.alma_backend.entity.EstadoOrganizacion;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.mapper.OrganizacionMapper;
import com.alma.alma_backend.service.AuditoriaAdminService;
import com.alma.alma_backend.service.OrganizacionService;
import com.alma.alma_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/organizaciones")
@RequiredArgsConstructor
public class OrganizacionController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacionController.class);

    private final OrganizacionService organizacionService;
    private final UsuarioService usuarioService;
    private final AuditoriaAdminService auditoriaAdminService;

    @PostMapping
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Registrar una nueva organización")
    @ApiResponse(responseCode = "200", description = "Organización creada")
    public ResponseEntity<OrganizacionResponseDTO> createOrganizacion(@Valid @RequestBody OrganizacionRequestDTO request) {
        logger.info("Admin creando una nueva organización");
        Organizacion nuevaOrganizacion = OrganizacionMapper.fromRequest(request);
        Organizacion guardada = organizacionService.save(nuevaOrganizacion);
        return ResponseEntity.ok(OrganizacionMapper.toResponse(guardada));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Listar organizaciones")
    @ApiResponse(responseCode = "200", description = "Organizaciones recuperadas")
    public ResponseEntity<List<OrganizacionResponseDTO>> getAllOrganizaciones() {
        logger.info("Solicitando todas las organizaciones");
        List<OrganizacionResponseDTO> response = organizacionService.findAll().stream()
                .map(OrganizacionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Consultar una organización por ID")
    @ApiResponse(responseCode = "200", description = "Organización encontrada")
    public ResponseEntity<OrganizacionResponseDTO> getOrganizacionById(@PathVariable Integer id) {
        logger.info("Solicitando organización por ID: {}", id);
        return organizacionService.findById(id)
                .map(OrganizacionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cif/{cif}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Consultar una organización por CIF")
    @ApiResponse(responseCode = "200", description = "Organización encontrada")
    public ResponseEntity<OrganizacionResponseDTO> getOrganizacionByCif(@PathVariable String cif) {
        logger.info("Solicitando organización por CIF: {}", cif);
        return organizacionService.findByCif(cif)
                .map(OrganizacionMapper::toResponse)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Actualizar datos básicos de una organización")
    @ApiResponse(responseCode = "200", description = "Organización actualizada")
    public ResponseEntity<OrganizacionResponseDTO> updateOrganizacion(@PathVariable Integer id,
                                                                      @Valid @RequestBody OrganizacionRequestDTO organizacionDetails) {
        logger.info("Admin actualizando organización ID: {}", id);
        try {
            Organizacion updated = organizacionService.updateOrganizacion(id, organizacionDetails);
            return ResponseEntity.ok(OrganizacionMapper.toResponse(updated));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    @Operation(summary = "Eliminar lógicamente una organización")
    @ApiResponse(responseCode = "204", description = "Organización eliminada")
    public ResponseEntity<Void> deleteOrganizacion(@PathVariable Integer id) {
        logger.info("Admin eliminando organización ID: {}", id);
        organizacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizaciones() {
        logger.info("Solicitando estadísticas de todas las organizaciones");
        List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizaciones();
        return ResponseEntity.ok(estadisticas);
    }

    @GetMapping("/{id}/estadisticas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(@PathVariable Integer id) {
        logger.info("Solicitando estadísticas para la organización ID: {}", id);
        return organizacionService.obtenerEstadisticasPorOrganizacion(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    logger.warn("No se encontraron estadísticas para la organización ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    @GetMapping("/estadisticas/activas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizacionesActivas() {
        logger.info("Solicitando estadísticas de organizaciones activas");
        List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizacionesActivas();
        return ResponseEntity.ok(estadisticas);
    }

    // =====================================================
    // ENDPOINTS DE GESTIÓN DE ESTADO (FASE 1)
    // =====================================================

    /**
     * Suspende temporalmente una organización.
     * La organización no podrá realizar operaciones críticas mientras esté suspendida.
     */
    @PutMapping("/{id}/suspender")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionResponseDTO> suspenderOrganizacion(
            @PathVariable Integer id,
            @RequestBody CambioEstadoOrganizacionDTO cambioEstadoDTO,
            Authentication authentication,
            HttpServletRequest request) {

        logger.warn("SUPER_ADMIN {} solicitando suspensión de organización ID: {}", authentication.getName(), id);

        try {
            // Obtener usuario autenticado
            Usuario usuarioAdmin = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener IP del cliente
            String ipOrigen = obtenerIpCliente(request);

            // Suspender organización
            Organizacion organizacionSuspendida = organizacionService.suspenderOrganizacion(
                    id,
                    cambioEstadoDTO.getMotivo(),
                    usuarioAdmin,
                    ipOrigen
            );

            logger.info("Organización ID: {} suspendida exitosamente", id);
            return ResponseEntity.ok(OrganizacionMapper.toResponse(organizacionSuspendida));

        } catch (RuntimeException e) {
            logger.error("Error al suspender organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Activa una organización previamente suspendida.
     */
    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionResponseDTO> activarOrganizacion(
            @PathVariable Integer id,
            @RequestBody CambioEstadoOrganizacionDTO cambioEstadoDTO,
            Authentication authentication,
            HttpServletRequest request) {

        logger.info("SUPER_ADMIN {} solicitando activación de organización ID: {}", authentication.getName(), id);

        try {
            // Obtener usuario autenticado
            Usuario usuarioAdmin = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener IP del cliente
            String ipOrigen = obtenerIpCliente(request);

            // Activar organización
            Organizacion organizacionActivada = organizacionService.activarOrganizacion(
                    id,
                    cambioEstadoDTO.getMotivo(),
                    usuarioAdmin,
                    ipOrigen
            );

            logger.info("Organización ID: {} activada exitosamente", id);
            return ResponseEntity.ok(OrganizacionMapper.toResponse(organizacionActivada));

        } catch (RuntimeException e) {
            logger.error("Error al activar organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Da de baja definitivamente una organización.
     * IMPORTANTE: La organización NO se elimina de la base de datos, solo cambia su estado a BAJA.
     */
    @PutMapping("/{id}/dar-baja")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionResponseDTO> darDeBajaOrganizacion(
            @PathVariable Integer id,
            @RequestBody CambioEstadoOrganizacionDTO cambioEstadoDTO,
            Authentication authentication,
            HttpServletRequest request) {

        logger.warn("SUPER_ADMIN {} solicitando BAJA DEFINITIVA de organización ID: {}. NO SE ELIMINA DE BD.",
                authentication.getName(), id);

        try {
            // Validar que el motivo no esté vacío (es crítico para baja definitiva)
            if (cambioEstadoDTO.getMotivo() == null || cambioEstadoDTO.getMotivo().trim().isEmpty()) {
                logger.error("Intento de dar de baja organización sin motivo");
                return ResponseEntity.badRequest().build();
            }

            // Obtener usuario autenticado
            Usuario usuarioAdmin = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener IP del cliente
            String ipOrigen = obtenerIpCliente(request);

            // Dar de baja organización
            Organizacion organizacionBaja = organizacionService.darDeBajaOrganizacion(
                    id,
                    cambioEstadoDTO.getMotivo(),
                    usuarioAdmin,
                    ipOrigen
            );

            logger.warn("Organización ID: {} dada de baja definitivamente. Estado cambiado a BAJA.", id);
            return ResponseEntity.ok(OrganizacionMapper.toResponse(organizacionBaja));

        } catch (RuntimeException e) {
            logger.error("Error al dar de baja organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Cambia el estado de una organización de forma genérica.
     */
    @PutMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionResponseDTO> cambiarEstadoOrganizacion(
            @PathVariable Integer id,
            @RequestBody CambioEstadoOrganizacionDTO cambioEstadoDTO,
            Authentication authentication,
            HttpServletRequest request) {

        logger.info("SUPER_ADMIN {} solicitando cambio de estado de organización ID: {} a {}",
                authentication.getName(), id, cambioEstadoDTO.getNuevoEstado());

        try {
            // Validar que el motivo no esté vacío
            if (cambioEstadoDTO.getMotivo() == null || cambioEstadoDTO.getMotivo().trim().isEmpty()) {
                logger.error("Intento de cambiar estado sin motivo");
                return ResponseEntity.badRequest().build();
            }

            // Obtener usuario autenticado
            Usuario usuarioAdmin = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener IP del cliente
            String ipOrigen = obtenerIpCliente(request);

            // Cambiar estado
            Organizacion organizacionActualizada = organizacionService.cambiarEstadoOrganizacion(
                    id,
                    cambioEstadoDTO,
                    usuarioAdmin,
                    ipOrigen
            );

            logger.info("Estado de organización ID: {} cambiado exitosamente a {}", id, cambioEstadoDTO.getNuevoEstado());
            return ResponseEntity.ok(OrganizacionMapper.toResponse(organizacionActualizada));

        } catch (RuntimeException e) {
            logger.error("Error al cambiar estado de organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene todas las organizaciones filtradas por estado.
     */
    @GetMapping("/estado/{estado}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionResponseDTO>> getOrganizacionesByEstado(@PathVariable EstadoOrganizacion estado) {
        logger.info("Solicitando organizaciones con estado: {}", estado);
        List<OrganizacionResponseDTO> organizaciones = organizacionService.findByEstado(estado).stream()
                .map(OrganizacionMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(organizaciones);
    }

    /**
     * Obtiene el historial de auditoría de una organización específica.
     */
    @GetMapping("/{id}/auditoria")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriaDTO>> getAuditoriaOrganizacion(@PathVariable Integer id) {
        logger.info("Solicitando historial de auditoría para organización ID: {}", id);

        try {
            List<AuditoriaAdmin> auditorias = auditoriaAdminService.obtenerAuditoriaOrganizacion(id);

            // Convertir a DTO
            List<AuditoriaDTO> auditoriasDTO = auditorias.stream()
                    .map(this::convertirAuditoriaADTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(auditoriasDTO);

        } catch (RuntimeException e) {
            logger.error("Error al obtener auditoría de organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene las últimas N acciones de auditoría del sistema.
     */
    @GetMapping("/auditoria/recientes")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<AuditoriaDTO>> getUltimasAuditorias(@RequestParam(defaultValue = "50") int limit) {
        logger.info("Solicitando las últimas {} acciones de auditoría", limit);

        try {
            List<AuditoriaAdmin> auditorias = auditoriaAdminService.obtenerUltimasAcciones(limit);

            // Convertir a DTO
            List<AuditoriaDTO> auditoriasDTO = auditorias.stream()
                    .map(this::convertirAuditoriaADTO)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(auditoriasDTO);

        } catch (RuntimeException e) {
            logger.error("Error al obtener últimas auditorías", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // =====================================================
    // MÉTODOS PRIVADOS AUXILIARES
    // =====================================================

    /**
     * Obtiene la dirección IP del cliente desde la petición HTTP.
     * Considera proxies y balanceadores de carga.
     */
    private String obtenerIpCliente(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // Si hay múltiples IPs, tomar la primera
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }

    /**
     * Convierte una entidad AuditoriaAdmin a DTO.
     */
    private AuditoriaDTO convertirAuditoriaADTO(AuditoriaAdmin auditoria) {
        AuditoriaDTO dto = new AuditoriaDTO();
        dto.setId(auditoria.getId());
        dto.setTipoAccion(auditoria.getTipoAccion());
        dto.setTablaAfectada(auditoria.getTablaAfectada());
        dto.setIdRegistroAfectado(auditoria.getIdRegistroAfectado());
        dto.setDatosAnteriores(auditoria.getDatosAnteriores());
        dto.setDatosNuevos(auditoria.getDatosNuevos());
        dto.setMotivo(auditoria.getMotivo());
        dto.setIpOrigen(auditoria.getIpOrigen());
        dto.setFechaAccion(auditoria.getFechaAccion());

        // Información del administrador
        Usuario admin = auditoria.getUsuarioAdmin();
        if (admin != null) {
            dto.setIdUsuarioAdmin(admin.getId());
            dto.setEmailAdmin(admin.getEmail());
            dto.setNombreAdmin(admin.getNombre());
            dto.setApellidosAdmin(admin.getApellidos());
        }

        return dto;
    }
}
