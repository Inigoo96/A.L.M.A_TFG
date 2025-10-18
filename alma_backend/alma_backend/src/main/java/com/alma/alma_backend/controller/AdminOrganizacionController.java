package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.AdminOrganizacion;
import com.alma.alma_backend.service.AdminOrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins-organizacion")
public class AdminOrganizacionController {

    @Autowired
    private AdminOrganizacionService adminOrganizacionService;

    @PostMapping
    public ResponseEntity<AdminOrganizacion> createAdminOrganizacion(@RequestBody AdminOrganizacion adminOrganizacion) {
        return ResponseEntity.ok(adminOrganizacionService.save(adminOrganizacion));
    }

    @GetMapping
    public ResponseEntity<List<AdminOrganizacion>> getAllAdminOrganizaciones() {
        return ResponseEntity.ok(adminOrganizacionService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AdminOrganizacion> getAdminOrganizacionById(@PathVariable Integer id) {
        return adminOrganizacionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<AdminOrganizacion> getAdminOrganizacionByUsuarioId(@PathVariable Integer usuarioId) {
        return adminOrganizacionService.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AdminOrganizacion> updateAdminOrganizacion(@PathVariable Integer id, @RequestBody AdminOrganizacion adminDetails) {
        try {
            return ResponseEntity.ok(adminOrganizacionService.updateAdminOrganizacion(id, adminDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAdminOrganizacion(@PathVariable Integer id) {
        adminOrganizacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
