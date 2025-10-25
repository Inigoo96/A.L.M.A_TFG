package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.OrganizacionRegistroDTO;
import com.alma.alma_backend.dto.PacienteRegistroDTO;
import com.alma.alma_backend.dto.ProfesionalRegistroDTO;
import com.alma.alma_backend.dto.UsuarioResponseDTO;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.mapper.UsuarioMapper;
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

    @PostMapping("/register/organization")
    public ResponseEntity<?> registrarOrganizacion(@RequestBody OrganizacionRegistroDTO registroDTO) {
        try {
            UsuarioResponseDTO nuevoAdmin = UsuarioMapper.toResponse(authService.registrarOrganizacionYAdmin(registroDTO));
            return new ResponseEntity<>(nuevoAdmin, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Conflict", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error", "message", "Error al procesar el registro de la organización"));
        }
    }

    @PostMapping("/register/profesional")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<?> registrarProfesional(@RequestBody ProfesionalRegistroDTO registroDTO, Authentication authentication) {
        try {
            // Obtener el usuario autenticado desde el token
            Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener el ID de la organización del administrador autenticado
            Integer organizacionId = currentUser.getOrganizacion().getId();

            UsuarioResponseDTO nuevoProfesional = UsuarioMapper.toResponse(
                    authService.registrarProfesional(registroDTO, organizacionId));
            return new ResponseEntity<>(nuevoProfesional, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Conflict", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error", "message", "Error al procesar el registro del profesional"));
        }
    }

    @PostMapping("/register/paciente")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<?> registrarPaciente(@RequestBody PacienteRegistroDTO registroDTO, Authentication authentication) {
        try {
            // Obtener el usuario autenticado desde el token
            Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

            // Obtener el ID de la organización del administrador autenticado
            Integer organizacionId = currentUser.getOrganizacion().getId();

            UsuarioResponseDTO nuevoPaciente = UsuarioMapper.toResponse(
                    authService.registrarPaciente(registroDTO, organizacionId));
            return new ResponseEntity<>(nuevoPaciente, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Validation Error", "message", e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Conflict", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Internal Server Error", "message", "Error al procesar el registro del paciente"));
        }
    }
}
