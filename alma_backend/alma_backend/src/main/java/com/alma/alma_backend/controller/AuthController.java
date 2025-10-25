package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.AuthenticationRequest;
import com.alma.alma_backend.dto.AuthenticationResponse;
import com.alma.alma_backend.dto.OrganizacionRegistroDTO;
import com.alma.alma_backend.dto.PacienteRegistroDTO;
import com.alma.alma_backend.dto.ProfesionalRegistroDTO;
import com.alma.alma_backend.dto.UsuarioResponseDTO;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.mapper.UsuarioMapper;
import com.alma.alma_backend.logging.AuditLogService;
import com.alma.alma_backend.service.AuthService;
import com.alma.alma_backend.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import java.util.Map;
import java.util.function.Supplier;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final AuditLogService auditLogService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthenticationResponse>> login(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        AuthenticationResponse response = authService.authenticate(authenticationRequest);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping("/register/organization")
    public ResponseEntity<ApiResponse<?>> registrarOrganizacion(@Valid @RequestBody OrganizacionRegistroDTO registroDTO) {
        return handleRegistration(
            () -> {
                UsuarioResponseDTO nuevoAdmin = UsuarioMapper.toResponse(authService.registrarOrganizacionYAdmin(registroDTO));
                auditLogService.logAuthWarn("Nueva organización registrada: {}", nuevoAdmin.getEmail());
                return nuevoAdmin;
            },
            HttpStatus.CREATED,
            "Organización registrada",
            "Error al procesar el registro de la organización"
        );
    }

    @PostMapping("/register/profesional")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ApiResponse<?>> registrarProfesional(@Valid @RequestBody ProfesionalRegistroDTO registroDTO, Authentication authentication) {
        Usuario currentUser = requireAuthenticatedUser(authentication);

        return handleRegistration(
            () -> {
                Integer organizacionId = currentUser.getOrganizacion().getId();
                UsuarioResponseDTO nuevoProfesional = UsuarioMapper.toResponse(authService.registrarProfesional(registroDTO, organizacionId));
                auditLogService.logAuthWarn("Profesional registrado por {}", currentUser.getEmail());
                return nuevoProfesional;
            },
            HttpStatus.CREATED,
            "Profesional registrado",
            "Error al procesar el registro del profesional"
        );
    }

    @PostMapping("/register/paciente")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ApiResponse<?>> registrarPaciente(@Valid @RequestBody PacienteRegistroDTO registroDTO, Authentication authentication) {
        Usuario currentUser = requireAuthenticatedUser(authentication);

        return handleRegistration(
            () -> {
                Integer organizacionId = currentUser.getOrganizacion().getId();
                UsuarioResponseDTO nuevoPaciente = UsuarioMapper.toResponse(authService.registrarPaciente(registroDTO, organizacionId));
                auditLogService.logAuthWarn("Paciente registrado por {}", currentUser.getEmail());
                return nuevoPaciente;
            },
            HttpStatus.CREATED,
            "Paciente registrado",
            "Error al procesar el registro del paciente"
        );
    }

    private Usuario requireAuthenticatedUser(Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        Usuario usuario = usuarioService.findByEmail(authentication.getName())
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario autenticado no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta del administrador está deshabilitada");
        }
        return usuario;
    }

    private <T> ResponseEntity<ApiResponse<?>> handleRegistration(Supplier<T> action, HttpStatus successStatus, String successMessage, String errorMessage) {
        try {
            T result = action.get();
            return ResponseEntity.status(successStatus)
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(ApiResponse.success(successStatus.value(), successMessage, result));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Map.of("error", "Validation Error")));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(), Map.of("error", "Conflict")));
        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            auditLogService.logControllerException("AuthController#handleRegistration", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), errorMessage));
        }
    }
}
