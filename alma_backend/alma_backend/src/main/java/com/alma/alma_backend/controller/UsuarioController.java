package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.service.UsuarioService;
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
@PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')") // Solo los administradores pueden gestionar usuarios "en crudo"
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @PostMapping
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        if (usuario.getOrganizacion() == null || usuario.getOrganizacion().getIdOrganizacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la organización es obligatorio.");
        }

        Organizacion organizacion = organizacionRepository.findById(usuario.getOrganizacion().getIdOrganizacion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La organización especificada no existe."));

        usuario.setOrganizacion(organizacion);
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));

        Usuario savedUsuario = usuarioService.save(usuario);

        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
    }

    // OPTIMIZADO: Ahora usa una consulta directa a la base de datos.
    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        // Llama al nuevo método optimizado del servicio
        List<Usuario> usuariosDeLaOrganizacion = usuarioService.findByOrganizacionId(userOrgId);

        return ResponseEntity.ok(usuariosDeLaOrganizacion);
    }

    // CORREGIDO: Ahora comprueba que el usuario pertenezca a la organización del admin.
    @GetMapping("/{id}")
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
                .orElse(ResponseEntity.notFound().<Usuario>build());
    }

    // CORREGIDO: Ahora comprueba los permisos antes de actualizar.
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getIdOrganizacion();

        return usuarioService.findById(id)
                .map(usuarioExistente -> {
                    if (!Objects.equals(usuarioExistente.getOrganizacion().getIdOrganizacion(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<Usuario>build();
                    }
                    try {
                        return ResponseEntity.ok(usuarioService.updateUser(id, usuarioDetails));
                    } catch (RuntimeException e) {
                        return ResponseEntity.notFound().<Usuario>build();
                    }
                })
                .orElse(ResponseEntity.notFound().<Usuario>build());
    }

    // CORREGIDO: Ahora comprueba los permisos antes de borrar.
    @DeleteMapping("/{id}")
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
}
