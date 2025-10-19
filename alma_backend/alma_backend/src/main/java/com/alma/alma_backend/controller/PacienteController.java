package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.service.PacienteService;
import com.alma.alma_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL', 'SUPER_ADMIN')")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.save(paciente));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        List<Paciente> pacientesDeLaOrganizacion = pacienteService.findAll().stream()
                .filter(paciente -> Objects.equals(paciente.getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(pacientesDeLaOrganizacion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Paciente> getPacienteById(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return pacienteService.findById(id)
                .map(paciente -> {
                    if (!Objects.equals(paciente.getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build();
                    }
                    return ResponseEntity.ok(paciente);
                })
                .orElse(ResponseEntity.notFound().<Paciente>build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<Paciente> getPacienteByUsuarioId(@PathVariable Integer usuarioId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return pacienteService.findByUsuarioId(usuarioId)
                .map(paciente -> {
                    if (!Objects.equals(paciente.getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build();
                    }
                    return ResponseEntity.ok(paciente);
                })
                .orElse(ResponseEntity.notFound().<Paciente>build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Paciente> updatePaciente(@PathVariable Integer id, @RequestBody Paciente pacienteDetails, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return pacienteService.findById(id)
            .map(pacienteExistente -> {
                if (!Objects.equals(pacienteExistente.getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                     return ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build();
                }
                try {
                    return ResponseEntity.ok(pacienteService.updatePaciente(id, pacienteDetails));
                } catch (RuntimeException e) {
                    return ResponseEntity.notFound().<Paciente>build();
                }
            })
            .orElse(ResponseEntity.notFound().<Paciente>build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        pacienteService.findById(id).ifPresent(paciente -> {
            if (Objects.equals(paciente.getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                pacienteService.deleteById(id);
            }
        });

        return ResponseEntity.noContent().build();
    }
}
