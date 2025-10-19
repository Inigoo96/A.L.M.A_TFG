package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ErrorResponse;
import com.alma.alma_backend.dto.UpdatePasswordRequest;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        if (usuario.getOrganizacion() == null || usuario.getOrganizacion().getIdOrganizacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la organización es obligatorio.");
        }

        Organizacion organizacion = organizacionRepository.findById(usuario.getOrganizacion().getIdOrganizacion())
                .orElseThrow(() -> new ResourceNotFoundException("La organización especificada no existe."));

        usuario.setOrganizacion(organizacion);
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        Usuario savedUsuario = usuarioService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<List<Usuario>> getAllUsuarios(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        List<Usuario> usuariosDeLaOrganizacion = usuarioService.findByOrganizacionId(userOrgId);

        return ResponseEntity.ok(usuariosDeLaOrganizacion);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return usuarioService.findById(id)
                .map(usuario -> {
                    if (!Objects.equals(usuario.getOrganizacion().getIdOrganizacion(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Usuario>build();
                    }
                    return ResponseEntity.ok(usuario);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return usuarioService.findById(id)
                .map(usuarioExistente -> {
                    if (!Objects.equals(usuarioExistente.getOrganizacion().getIdOrganizacion(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Usuario>build();
                    }
                    Usuario updatedUsuario = usuarioService.updateUser(id, usuarioDetails);
                    return ResponseEntity.ok(updatedUsuario);
                })
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        usuarioService.findById(id).ifPresent(usuario -> {
            if (Objects.equals(usuario.getOrganizacion().getIdOrganizacion(), userOrgId)) {
                usuarioService.deleteById(id);
            }
        });

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMyPassword(@Valid @RequestBody UpdatePasswordRequest passwordRequest, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));

        if (!passwordEncoder.matches(passwordRequest.getOldPassword(), currentUser.getPasswordHash())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("La contraseña actual es incorrecta"));
        }

        currentUser.setPasswordHash(passwordEncoder.encode(passwordRequest.getNewPassword()));
        currentUser.setPasswordTemporal(false);
        usuarioService.save(currentUser);

        return ResponseEntity.ok().body("Contraseña actualizada correctamente");
    }
}
