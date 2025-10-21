package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.*;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.service.AsignacionProfesionalPacienteService;
import com.alma.alma_backend.service.UsuarioService;
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

        return ResponseEntity.status(HttpStatus.CREATED).body(mapToAsignacionResponseDTO(asignacionGuardada));
    }

    @GetMapping("/paciente/{pacienteId}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL')")
    public ResponseEntity<List<AsignacionResponseDTO>> getAsignacionesByPacienteId(@PathVariable Integer pacienteId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        logger.debug("Usuario '{}' consultando asignaciones del paciente ID {}", currentUser.getEmail(), pacienteId);
        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByPacienteIdAndOrganizacionId(pacienteId, userOrgId);
        List<AsignacionResponseDTO> dtos = asignaciones.stream().map(this::mapToAsignacionResponseDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/profesional/{profesionalId}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL')")
    public ResponseEntity<List<AsignacionResponseDTO>> getAsignacionesByProfesionalId(@PathVariable Integer profesionalId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        logger.debug("Usuario '{}' consultando asignaciones del profesional ID {}", currentUser.getEmail(), profesionalId);
        List<AsignacionProfesionalPaciente> asignaciones = asignacionService.findByProfesionalIdAndOrganizacionId(profesionalId, userOrgId);
        List<AsignacionResponseDTO> dtos = asignaciones.stream().map(this::mapToAsignacionResponseDTO).collect(Collectors.toList());
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
        return ResponseEntity.ok(mapToAsignacionResponseDTO(deactivatedAsignacion));
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

    // --- Métodos de Mapeo a DTOs --- //

    private AsignacionResponseDTO mapToAsignacionResponseDTO(AsignacionProfesionalPaciente asignacion) {
        if (asignacion == null) return null;
        AsignacionResponseDTO dto = new AsignacionResponseDTO();
        dto.setId(asignacion.getIdAsignacion());
        dto.setProfesional(mapToProfesionalResponseDTO(asignacion.getProfesional()));
        dto.setPaciente(mapToPacienteResponseDTO(asignacion.getPaciente()));
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setActiva(asignacion.getActivo());
        dto.setEsPrincipal(asignacion.getEsPrincipal()); // CORREGIDO
        return dto;
    }

    private ProfesionalResponseDTO mapToProfesionalResponseDTO(Profesional profesional) {
        if (profesional == null) return null;
        ProfesionalResponseDTO dto = new ProfesionalResponseDTO();
        dto.setId(profesional.getId());
        dto.setUsuario(mapToUsuarioResponseDTO(profesional.getUsuario()));
        dto.setNumeroColegiado(profesional.getNumeroColegiado());
        dto.setEspecialidad(profesional.getEspecialidad());
        dto.setCentroSalud(profesional.getCentroSalud());
        return dto;
    }

    private PacienteResponseDTO mapToPacienteResponseDTO(Paciente paciente) {
        if (paciente == null) return null;
        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setUsuario(mapToUsuarioResponseDTO(paciente.getUsuario()));
        dto.setTarjetaSanitaria(paciente.getTarjetaSanitaria());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setGenero(paciente.getGenero());
        return dto;
    }

    private UsuarioResponseDTO mapToUsuarioResponseDTO(Usuario usuario) {
        if (usuario == null) return null;
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setDni(usuario.getDni());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setTelefono(usuario.getTelefono());
        dto.setTipoUsuario(usuario.getTipoUsuario());
        dto.setOrganizacion(usuario.getOrganizacion());
        dto.setActivo(usuario.getActivo());
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setPasswordTemporal(usuario.getPasswordTemporal());
        dto.setCargo(usuario.getCargo());
        dto.setDocumentoCargoUrl(usuario.getDocumentoCargoUrl());
        return dto;
    }
}
