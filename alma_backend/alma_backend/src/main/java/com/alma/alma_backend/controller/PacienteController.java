package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.AsignacionProfesionalPacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
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
import java.util.Optional;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL', 'SUPER_ADMIN')")
public class PacienteController {

    @Autowired
    private PacienteService pacienteService;

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private AsignacionProfesionalPacienteRepository asignacionRepository;

    @PostMapping
    public ResponseEntity<Paciente> createPaciente(@RequestBody Paciente paciente) {
        return ResponseEntity.ok(pacienteService.save(paciente));
    }

    @GetMapping
    public ResponseEntity<List<Paciente>> getAllPacientes(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        List<Paciente> pacientesDeLaOrganizacion = pacienteService.findByOrganizacionId(userOrgId);

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

                    if (currentUser.getTipoUsuario() == TipoUsuario.ADMIN_ORGANIZACION || currentUser.getTipoUsuario() == TipoUsuario.SUPER_ADMIN) {
                        return ResponseEntity.ok(paciente);
                    }

                    if (currentUser.getTipoUsuario() == TipoUsuario.PROFESIONAL) {
                        return profesionalRepository.findByUsuario(currentUser).flatMap(profesional -> {
                            if (asignacionRepository.existeAsignacionActiva(profesional.getIdProfesional(), paciente.getIdPaciente())) {
                                return Optional.of(ResponseEntity.ok(paciente));
                            }
                            return Optional.of(ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build());
                        }).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build());
                    }

                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<Paciente>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
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
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado para el usuario con id: " + usuarioId));
    }

    // CORREGIDO: Se elimina el try-catch para dejar que el GlobalExceptionHandler actúe.
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
                Paciente updatedPaciente = pacienteService.updatePaciente(id, pacienteDetails);
                return ResponseEntity.ok(updatedPaciente);
            })
            .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
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
