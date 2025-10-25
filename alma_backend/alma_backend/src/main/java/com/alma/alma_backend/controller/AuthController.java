package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ApiResponse;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final UsuarioService usuarioService;
    private final AuditLogService auditLogService;

    @PostMapping("/register/organization")
    public ResponseEntity<ApiResponse<?>> registrarOrganizacion(@RequestBody OrganizacionRegistroDTO registroDTO) {
        try {
            UsuarioResponseDTO nuevoAdmin = UsuarioMapper.toResponse(authService.registrarOrganizacionYAdmin(registroDTO));
            auditLogService.logAuthWarn("Nueva organización registrada: {}", nuevoAdmin.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Organización registrada", nuevoAdmin));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Map.of("error", "Validation Error")));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(), Map.of("error", "Conflict")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar el registro de la organización"));
        }
    }

    @PostMapping("/register/profesional")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ApiResponse<?>> registrarProfesional(@RequestBody ProfesionalRegistroDTO registroDTO, Authentication authentication) {
        try {
            // Obtener el usuario autenticado desde el token
            Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            auditLogService.logSecurityWarn("Verificación de rol para usuario {} con rol {}", currentUser.getEmail(), currentUser.getTipoUsuario());

            // Obtener el ID de la organización del administrador autenticado
            Integer organizacionId = currentUser.getOrganizacion().getId();

            UsuarioResponseDTO nuevoProfesional = UsuarioMapper.toResponse(
                    authService.registrarProfesional(registroDTO, organizacionId));
            auditLogService.logAuthWarn("Profesional registrado por {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Profesional registrado", nuevoProfesional));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Map.of("error", "Validation Error")));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(), Map.of("error", "Conflict")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar el registro del profesional"));
        }
    }

    @PostMapping("/register/paciente")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ApiResponse<?>> registrarPaciente(@RequestBody PacienteRegistroDTO registroDTO, Authentication authentication) {
        try {
            // Obtener el usuario autenticado desde el token
            Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            auditLogService.logSecurityWarn("Verificación de rol para usuario {} con rol {}", currentUser.getEmail(), currentUser.getTipoUsuario());

            // Obtener el ID de la organización del administrador autenticado
            Integer organizacionId = currentUser.getOrganizacion().getId();

            UsuarioResponseDTO nuevoPaciente = UsuarioMapper.toResponse(
                    authService.registrarPaciente(registroDTO, organizacionId));
            auditLogService.logAuthWarn("Paciente registrado por {}", currentUser.getEmail());
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(HttpStatus.CREATED.value(), "Paciente registrado", nuevoPaciente));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), e.getMessage(), Map.of("error", "Validation Error")));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(HttpStatus.CONFLICT.value(), e.getMessage(), Map.of("error", "Conflict")));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Error al procesar el registro del paciente"));
        }
    }
}
