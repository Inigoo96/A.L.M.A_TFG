package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.AsignacionRequest;
import com.alma.alma_backend.dto.ErrorResponse;
import com.alma.alma_backend.dto.UserCreationRequest;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.AsignacionProfesionalPacienteRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@RequestMapping("/api/admin-org")
@PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
public class AdminOrganizacionController {

    private static final Logger logger = LoggerFactory.getLogger(AdminOrganizacionController.class);

    private final UsuarioService usuarioService;
    private final ProfesionalRepository profesionalRepository;
    private final PacienteRepository pacienteRepository;
    private final AsignacionProfesionalPacienteRepository asignacionRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AdminOrganizacionController(UsuarioService usuarioService,
                                     ProfesionalRepository profesionalRepository,
                                     PacienteRepository pacienteRepository,
                                     AsignacionProfesionalPacienteRepository asignacionRepository,
                                     PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.profesionalRepository = profesionalRepository;
        this.pacienteRepository = pacienteRepository;
        this.asignacionRepository = asignacionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")
    @Transactional
    public ResponseEntity<?> createUser(@Valid @RequestBody UserCreationRequest request, Authentication authentication) {
        Usuario admin = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalStateException("Admin autenticado no encontrado"));
        Organizacion organizacion = admin.getOrganizacion();

        if (usuarioService.existsByEmail(request.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("El email ya está en uso"));
        }
        if (request.getTipoUsuario() != TipoUsuario.PROFESIONAL && request.getTipoUsuario() != TipoUsuario.PACIENTE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Solo se pueden crear usuarios de tipo PROFESIONAL o PACIENTE"));
        }

        Usuario newUser = new Usuario();
        newUser.setNombre(request.getNombre());
        newUser.setApellidos(request.getApellidos());
        newUser.setEmail(request.getEmail().toLowerCase().trim());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setTipoUsuario(request.getTipoUsuario());
        newUser.setOrganizacion(organizacion);

        try {
            switch (request.getTipoUsuario()) {
                case PROFESIONAL:
                    Profesional profesional = new Profesional();
                    profesional.setUsuario(newUser);
                    profesional.setNumeroColegiado(request.getNumeroColegiado());
                    profesional.setEspecialidad(request.getEspecialidad());
                    profesionalRepository.save(profesional);
                    break;
                case PACIENTE:
                    Paciente paciente = new Paciente();
                    paciente.setUsuario(newUser);
                    pacienteRepository.save(paciente);
                    break;
                default:
                    throw new IllegalStateException("Tipo de usuario no soportado: " + request.getTipoUsuario());
            }
            logger.info("Usuario '{}' de tipo {} creado por el admin '{}' para la organización '{}'", newUser.getEmail(), newUser.getTipoUsuario(), admin.getEmail(), organizacion.getNombreOrganizacion());
            return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
        } catch (Exception e) {
            logger.error("Error al crear el perfil específico para el usuario: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error al crear el perfil del usuario"));
        }
    }

    @PostMapping("/assignments")
    @Transactional
    public ResponseEntity<?> assignPatientToProfessional(@Valid @RequestBody AsignacionRequest request, Authentication authentication) {
        Usuario admin = usuarioService.findByEmail(authentication.getName()).orElseThrow(() -> new IllegalStateException("Admin autenticado no encontrado"));
        Organizacion adminOrg = admin.getOrganizacion();

        Profesional profesional = profesionalRepository.findById(request.getProfesionalId())
            .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con ID: " + request.getProfesionalId()));
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
            .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con ID: " + request.getPacienteId()));

        if (!Objects.equals(profesional.getUsuario().getOrganizacion().getIdOrganizacion(), adminOrg.getIdOrganizacion()) ||
            !Objects.equals(paciente.getUsuario().getOrganizacion().getIdOrganizacion(), adminOrg.getIdOrganizacion())) {
            logger.warn("Intento de asignación cruzada por el admin '{}'.", admin.getEmail());
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("No tiene permiso para asignar usuarios que no pertenecen a su organización"));
        }

        AsignacionProfesionalPaciente asignacion = new AsignacionProfesionalPaciente();
        asignacion.setProfesional(profesional);
        asignacion.setPaciente(paciente);

        asignacionRepository.save(asignacion);

        logger.info("Paciente {} asignado al profesional {} por el admin '{}'", paciente.getUsuario().getEmail(), profesional.getUsuario().getEmail(), admin.getEmail());

        return ResponseEntity.status(HttpStatus.CREATED).body(asignacion);
    }
}
