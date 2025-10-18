package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.service.ProfesionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/profesionales")
public class ProfesionalController {

    @Autowired
    private ProfesionalService profesionalService;

    @PostMapping
    public ResponseEntity<Profesional> createProfesional(@RequestBody Profesional profesional) {
        // La lógica de creación (crear Usuario, luego Profesional) debería estar en el Service.
        // Por simplicidad aquí, asumimos que el objeto llega completo.
        return ResponseEntity.ok(profesionalService.save(profesional));
    }

    @GetMapping
    public ResponseEntity<List<Profesional>> getAllProfesionales() {
        return ResponseEntity.ok(profesionalService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profesional> getProfesionalById(@PathVariable Integer id) {
        return profesionalService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Profesional> getProfesionalByUsuarioId(@PathVariable Integer usuarioId) {
        return profesionalService.findByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/organizacion/{organizacionId}")
    public ResponseEntity<List<Profesional>> getProfesionalesByOrganizacionId(@PathVariable Integer organizacionId) {
        return ResponseEntity.ok(profesionalService.findByOrganizacionId(organizacionId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profesional> updateProfesional(@PathVariable Integer id, @RequestBody Profesional profesionalDetails) {
        try {
            return ResponseEntity.ok(profesionalService.updateProfesional(id, profesionalDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfesional(@PathVariable Integer id) {
        profesionalService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
