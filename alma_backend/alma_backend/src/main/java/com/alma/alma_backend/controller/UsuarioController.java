package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import com.alma.alma_backend.dto.ResetPasswordRequestDTO;
import com.alma.alma_backend.dto.UpdatePasswordRequest;
import com.alma.alma_backend.dto.UsuarioResponseDTO;
import com.alma.alma_backend.dto.UsuarioUpdateRequestDTO;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.service.UsuarioService;
import com.alma.alma_backend.mapper.UsuarioMapper;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final PasswordEncoder passwordEncoder;
    public UsuarioController(UsuarioService usuarioService,
                             PasswordEncoder passwordEncoder) {
        this.usuarioService = usuarioService;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Listar usuarios de la organización actual")
    public ResponseEntity<List<UsuarioResponseDTO>> getAllUsuarios(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        List<Usuario> usuariosDeLaOrganizacion = usuarioService.findByOrganizacionId(userOrgId);
        List<UsuarioResponseDTO> usuariosDTO = usuariosDeLaOrganizacion.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(usuariosDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Obtener detalles de un usuario por ID")
    public ResponseEntity<UsuarioResponseDTO> getUsuarioById(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        return usuarioService.findById(id)
                .map(usuario -> {
                    if (!Objects.equals(usuario.getOrganizacion().getId(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<UsuarioResponseDTO>build();
                    }
                    return ResponseEntity.ok(mapToDTO(usuario));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    @Operation(summary = "Actualizar parcialmente un usuario", description = "Solo campos presentes en el cuerpo serán modificados")
    public ResponseEntity<UsuarioResponseDTO> updateUsuario(@PathVariable Integer id,
                                                            @Valid @RequestBody UsuarioUpdateRequestDTO usuarioDetails,
                                                            Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        return usuarioService.findById(id)
                .map(usuarioExistente -> {
                    if (!Objects.equals(usuarioExistente.getOrganizacion().getId(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<UsuarioResponseDTO>build();
                    }
                    Usuario updatedUsuario = usuarioService.updateUser(id, usuarioDetails);
                    return ResponseEntity.ok(mapToDTO(updatedUsuario));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        usuarioService.findById(id).ifPresent(usuario -> {
            if (Objects.equals(usuario.getOrganizacion().getId(), userOrgId)) {
                usuarioService.deleteById(id);
            }
        });

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> updateMyPassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), currentUser.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error(HttpStatus.UNAUTHORIZED.value(), "La contraseña actual es incorrecta"));
        }

        currentUser.setPasswordHash(passwordEncoder.encode(passwordRequest.getNewPassword()));
        currentUser.setPasswordTemporal(false);
        usuarioService.save(currentUser);

        return ResponseEntity.ok(ApiResponse.success("Contraseña actualizada correctamente"));
    }

    @PostMapping("/{id}/reset-password")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@PathVariable Integer id, @Valid @RequestBody ResetPasswordRequestDTO request, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario administrador no encontrado"));
        Integer adminOrgId = currentUser.getOrganizacion().getId();

        Usuario usuarioAModificar = usuarioService.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));

        if (currentUser.getTipoUsuario().equals(TipoUsuario.ADMIN_ORGANIZACION) && !Objects.equals(usuarioAModificar.getOrganizacion().getId(), adminOrgId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(HttpStatus.FORBIDDEN.value(), "No puede modificar usuarios de otra organización."));
        }

        usuarioAModificar.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        usuarioAModificar.setPasswordTemporal(true);
        usuarioService.save(usuarioAModificar);

        return ResponseEntity.ok(ApiResponse.success("Contraseña del usuario " + id + " actualizada correctamente."));
    }

    private UsuarioResponseDTO mapToDTO(Usuario usuario) {
        return UsuarioMapper.toResponse(usuario);
    }
}
