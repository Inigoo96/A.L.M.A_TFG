package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.service.OrganizacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/organizaciones")
public class OrganizacionController {

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
}
