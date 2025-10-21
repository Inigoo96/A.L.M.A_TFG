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
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Organizacion> createOrganizacion(@RequestBody Organizacion organizacion) {
        logger.info("Admin creando una nueva organización");
        return ResponseEntity.ok(organizacionService.save(organizacion));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<List<Organizacion>> getAllOrganizaciones() {
        logger.info("Solicitando todas las organizaciones");
        return ResponseEntity.ok(organizacionService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Organizacion> getOrganizacionById(@PathVariable Integer id) {
        logger.info("Solicitando organización por ID: {}", id);
        return organizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/cif/{cif}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Organizacion> getOrganizacionByCif(@PathVariable String cif) {
        logger.info("Solicitando organización por CIF: {}", cif);
        return organizacionService.findByCif(cif)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<Organizacion> updateOrganizacion(@PathVariable Integer id, @RequestBody Organizacion organizacionDetails) {
        logger.info("Admin actualizando organización ID: {}", id);
        try {
            return ResponseEntity.ok(organizacionService.updateOrganizacion(id, organizacionDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
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
}
