package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.service.OrganizacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
public class OrganizacionController {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacionController.class);

    @Autowired
    private OrganizacionService organizacionService;

    @PostMapping
    public ResponseEntity<Organizacion> createOrganizacion(@RequestBody Organizacion organizacion) {
        return ResponseEntity.ok(organizacionService.save(organizacion));
    }

    @GetMapping
    public ResponseEntity<List<Organizacion>> getAllOrganizaciones() {
        return ResponseEntity.ok(organizacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Organizacion> getOrganizacionById(@PathVariable Integer id) {
        return organizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cif/{cif}")
    public ResponseEntity<Organizacion> getOrganizacionByCif(@PathVariable String cif) {
        return organizacionService.findByCif(cif)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Organizacion> updateOrganizacion(@PathVariable Integer id, @RequestBody Organizacion organizacionDetails) {
        try {
            return ResponseEntity.ok(organizacionService.updateOrganizacion(id, organizacionDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrganizacion(@PathVariable Integer id) {
        organizacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // CORREGIDO: Se elimina el bloque try-catch para permitir que Spring Security maneje las excepciones de acceso.
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizaciones() {
        logger.info("Solicitando estadísticas de todas las organizaciones");
        List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizaciones();
        return ResponseEntity.ok(estadisticas);
    }

    // CORREGIDO: Se elimina el bloque try-catch.
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

    // CORREGIDO: Se elimina el bloque try-catch.
    @GetMapping("/estadisticas/activas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizacionesActivas() {
        logger.info("Solicitando estadísticas de organizaciones activas");
        List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizacionesActivas();
        return ResponseEntity.ok(estadisticas);
    }
}
