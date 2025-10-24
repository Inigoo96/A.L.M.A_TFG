package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.RecursoDTO;
import com.alma.alma_backend.dto.UsoRecursoRequestDTO;
import com.alma.alma_backend.dto.UsoRecursoResponseDTO;
import com.alma.alma_backend.service.RecursoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recursos")
public class RecursoController {

    @Autowired
    private RecursoService recursoService;

    @GetMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<RecursoDTO>> getAllRecursos() {
        return ResponseEntity.ok(recursoService.findAllRecursos());
    }

    @GetMapping("/recomendados/{pacienteId}")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<RecursoDTO>> getRecursosRecomendados(@PathVariable Integer pacienteId) {
        return ResponseEntity.ok(recursoService.findRecursosRecomendados(pacienteId));
    }

    @PostMapping("/uso")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<UsoRecursoResponseDTO> registrarUso(@RequestBody UsoRecursoRequestDTO usoRecursoRequestDTO) {
        return ResponseEntity.ok(recursoService.registrarUso(usoRecursoRequestDTO));
    }
}
