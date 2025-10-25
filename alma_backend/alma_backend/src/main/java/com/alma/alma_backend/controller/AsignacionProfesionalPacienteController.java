package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.AsignacionRequestDTO;
import com.alma.alma_backend.dto.AsignacionResponseDTO;
import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.service.AsignacionProfesionalPacienteService;
import com.alma.alma_backend.service.UsuarioService;
import com.alma.alma_backend.mapper.AsignacionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/asignaciones")
public class AsignacionProfesionalPacienteController {

    private static final Logger logger = LoggerFactory.getLogger(AsignacionProfesionalPacienteController.class);

    @Autowired
    private AsignacionProfesionalPacienteService asignacionService;
    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProfesionalRepository profesionalRepository;
    @Autowired
    private PacienteRepository pacienteRepository;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<AsignacionResponseDTO> createAsignacion(@RequestBody AsignacionRequestDTO request, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer adminOrgId = currentUser.getOrganizacion().getId();

        Profesional profesional = profesionalRepository.findById(request.getProfesionalId())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + request.getProfesionalId()));

        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + request.getPacienteId()));

        if (!Objects.equals(profesional.getUsuario().getOrganizacion().getId(), adminOrgId) ||
            !Objects.equals(paciente.getUsuario().getOrganizacion().getId(), adminOrgId)) {
            logger.warn("Admin '{}' intentó crear una asignación fuera de su organización.", currentUser.getEmail());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No se pueden asignar usuarios de otras organizaciones.");
        }

        AsignacionProfesionalPaciente nuevaAsignacion = new AsignacionProfesionalPaciente();
        nuevaAsignacion.setProfesional(profesional);
        nuevaAsignacion.setPaciente(paciente);
        nuevaAsignacion.setEsPrincipal(request.getEsPrincipal()); // CORREGIDO

        AsignacionProfesionalPaciente asignacionGuardada = asignacionService.save(nuevaAsignacion);
        logger.info("Admin '{}' creó la asignación ID {} entre profesional {} y paciente {}", currentUser.getEmail(), asignacionGuardada.getIdAsignacion(), profesional.getId(), paciente.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(AsignacionMapper.toResponse(asignacionGuardada));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL')")
    public ResponseEntity<List<AsignacionResponseDTO>> getAsignacionesByPacienteId(@PathVariable Integer pacienteId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        logger.debug("Usuario '{}' consultando asignaciones del paciente ID {}", currentUser.getEmail(), pacienteId);
        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByPacienteIdAndOrganizacionId(pacienteId, userOrgId);
        List<AsignacionResponseDTO> dtos = asignaciones.stream().map(AsignacionMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profesional/{profesionalId}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL')")
    public ResponseEntity<List<AsignacionResponseDTO>> getAsignacionesByProfesionalId(@PathVariable Integer profesionalId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        logger.debug("Usuario '{}' consultando asignaciones del profesional ID {}", currentUser.getEmail(), profesionalId);
        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByProfesionalIdAndOrganizacionId(profesionalId, userOrgId);
        List<AsignacionResponseDTO> dtos = asignaciones.stream().map(AsignacionMapper::toResponse).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @PutMapping("/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<AsignacionResponseDTO> deactivateAsignacion(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        AsignacionProfesionalPaciente asignacion = asignacionService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con id: " + id));

        if (!Objects.equals(asignacion.getProfesional().getUsuario().getOrganizacion().getId(), userOrgId)) {
            logger.warn("Admin '{}' intentó desactivar una asignación fuera de su organización.", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        AsignacionProfesionalPaciente deactivatedAsignacion = asignacionService.deactivateAsignacion(id);
        logger.info("Admin '{}' desactivó la asignación ID {}", currentUser.getEmail(), id);
        return ResponseEntity.ok(AsignacionMapper.toResponse(deactivatedAsignacion));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<Void> deleteAsignacion(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        AsignacionProfesionalPaciente asignacion = asignacionService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Asignación no encontrada con id: " + id));

        if (!Objects.equals(asignacion.getProfesional().getUsuario().getOrganizacion().getId(), userOrgId)) {
            logger.warn("Admin '{}' intentó borrar una asignación fuera de su organización.", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        asignacionService.deleteById(id);
        logger.info("Admin '{}' eliminó la asignación ID {}", currentUser.getEmail(), id);
        return ResponseEntity.noContent().build();
    }

}
