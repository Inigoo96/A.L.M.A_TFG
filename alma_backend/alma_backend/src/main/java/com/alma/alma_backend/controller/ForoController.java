package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ForoDTO;
import com.alma.alma_backend.dto.MensajeForoRequestDTO;
import com.alma.alma_backend.dto.MensajeForoResponseDTO;
import com.alma.alma_backend.service.ForoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/foros")
public class ForoController {

    @Autowired
    private ForoService foroService;

    @GetMapping
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<ForoDTO>> getAllForos() {
        return ResponseEntity.ok(foroService.findAllForos());
    }

    @GetMapping("/{foroId}/mensajes")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<MensajeForoResponseDTO>> getMensajesByForoId(@PathVariable Integer foroId) {
        return ResponseEntity.ok(foroService.findMensajesByForoId(foroId));
    }

    @PostMapping("/mensajes")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<MensajeForoResponseDTO> postMensaje(@RequestBody MensajeForoRequestDTO mensajeForoRequestDTO, Authentication authentication) {
        return ResponseEntity.ok(foroService.postMensaje(mensajeForoRequestDTO, authentication));
    }
}
