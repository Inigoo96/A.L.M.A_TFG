package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.service.AsignacionProfesionalPacienteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionProfesionalPacienteController {

    @Autowired
    private AsignacionProfesionalPacienteService asignacionService;

    @PostMapping
    public ResponseEntity<AsignacionProfesionalPaciente> createAsignacion(@RequestBody AsignacionProfesionalPaciente asignacion) {
        return ResponseEntity.ok(asignacionService.save(asignacion));
    }

    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AsignacionProfesionalPaciente>> getAsignacionesByPacienteId(@PathVariable Integer pacienteId) {
        return ResponseEntity.ok(asignacionService.findByPacienteId(pacienteId));
    }

    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<AsignacionProfesionalPaciente>> getAsignacionesByProfesionalId(@PathVariable Integer profesionalId) {
        return ResponseEntity.ok(asignacionService.findByProfesionalId(profesionalId));
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AsignacionProfesionalPaciente> deactivateAsignacion(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(asignacionService.deactivateAsignacion(id));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignacion(@PathVariable Integer id) {
        asignacionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
