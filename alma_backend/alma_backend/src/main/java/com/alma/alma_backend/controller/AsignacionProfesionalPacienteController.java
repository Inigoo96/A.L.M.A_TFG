package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.service.AsignacionProfesionalPacienteService;
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
@RequestMapping("/api/asignaciones")
@PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL', 'SUPER_ADMIN')")
public class AsignacionProfesionalPacienteController {

    @Autowired
    private AsignacionProfesionalPacienteService asignacionService;

    @Autowired
    private UsuarioService usuarioService;

    // La creación ya se gestiona de forma segura en AdminOrganizacionController
    @PostMapping
    public ResponseEntity<AsignacionProfesionalPaciente> createAsignacion(@RequestBody AsignacionProfesionalPaciente asignacion) {
        return ResponseEntity.ok(asignacionService.save(asignacion));
    }

    // CORREGIDO: Ahora comprueba la organización
    @GetMapping("/paciente/{pacienteId}")
    public ResponseEntity<List<AsignacionProfesionalPaciente>> getAsignacionesByPacienteId(@PathVariable Integer pacienteId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByPacienteId(pacienteId);
        if (asignaciones.stream().anyMatch(a -> !Objects.equals(a.getPaciente().getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(asignaciones);
    }

    // CORREGIDO: Ahora comprueba la organización
    @GetMapping("/profesional/{profesionalId}")
    public ResponseEntity<List<AsignacionProfesionalPaciente>> getAsignacionesByProfesionalId(@PathVariable Integer profesionalId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByProfesionalId(profesionalId);
        if (asignaciones.stream().anyMatch(a -> !Objects.equals(a.getProfesional().getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(asignaciones);
    }

    // CORREGIDO: Ahora comprueba la organización
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<AsignacionProfesionalPaciente> deactivateAsignacion(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return asignacionService.findById(id).map(asignacion -> {
            if (!Objects.equals(asignacion.getProfesional().getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).<AsignacionProfesionalPaciente>build();
            }
            try {
                return ResponseEntity.ok(asignacionService.deactivateAsignacion(id));
            } catch (RuntimeException e) {
                return ResponseEntity.notFound().<AsignacionProfesionalPaciente>build();
            }
        }).orElse(ResponseEntity.notFound().<AsignacionProfesionalPaciente>build());
    }

    // CORREGIDO: Ahora comprueba la organización
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAsignacion(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        asignacionService.findById(id).ifPresent(asignacion -> {
            if (Objects.equals(asignacion.getProfesional().getUsuario().getOrganizacion().getIdOrganizacion(), userOrgId)) {
                asignacionService.deleteById(id);
            }
        });

        return ResponseEntity.noContent().build();
    }
}
