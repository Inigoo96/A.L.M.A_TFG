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

    /**
     * Obtiene estadísticas agregadas de usuarios para todas las organizaciones.
     *
     * Este endpoint devuelve información detallada sobre el número de usuarios de cada tipo
     * (ADMIN_ORGANIZACION, PROFESIONAL, PACIENTE, SUPER_ADMIN) para cada organización
     * registrada en el sistema.
     *
     * Respuesta de ejemplo:
     * [
     *   {
     *     "idOrganizacion": 1,
     *     "nombreOrganizacion": "Clínica Mental Health",
     *     "cif": "A12345678",
     *     "activa": true,
     *     "totalUsuarios": 25,
     *     "admins": 2,
     *     "profesionales": 10,
     *     "pacientes": 13,
     *     "superAdmins": 0
     *   }
     * ]
     *
     * Solo accesible por usuarios con rol SUPER_ADMIN
     *
     * @return ResponseEntity con la lista de estadísticas de todas las organizaciones
     */
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizaciones() {
        logger.info("Solicitando estadísticas de todas las organizaciones");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizaciones();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estadísticas agregadas de usuarios para una organización específica.
     *
     * Este endpoint permite consultar las estadísticas de una organización individual,
     * incluyendo el conteo de usuarios por tipo.
     *
     * Solo accesible por usuarios con rol SUPER_ADMIN
     *
     * @param id ID de la organización a consultar
     * @return ResponseEntity con las estadísticas de la organización, o 404 si no existe
     */
    @GetMapping("/{id}/estadisticas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(@PathVariable Integer id) {
        logger.info("Solicitando estadísticas para la organización ID: {}", id);
        try {
            return organizacionService.obtenerEstadisticasPorOrganizacion(id)
                    .map(ResponseEntity::ok)
                    .orElseGet(() -> {
                        logger.warn("No se encontraron estadísticas para la organización ID: {}", id);
                        return ResponseEntity.notFound().build();
                    });
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas para la organización ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Obtiene estadísticas agregadas de usuarios solo para organizaciones activas.
     *
     * Similar al endpoint /estadisticas, pero filtra solo organizaciones con activa=true.
     * Útil para dashboards que solo deben mostrar organizaciones operativas.
     *
     * Solo accesible por usuarios con rol SUPER_ADMIN
     *
     * @return ResponseEntity con la lista de estadísticas de organizaciones activas
     */
    @GetMapping("/estadisticas/activas")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<List<OrganizacionEstadisticasDTO>> obtenerEstadisticasOrganizacionesActivas() {
        logger.info("Solicitando estadísticas de organizaciones activas");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionService.obtenerEstadisticasOrganizacionesActivas();
            return ResponseEntity.ok(estadisticas);
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones activas", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
