package com.alma.alma_backend.controller;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
    public ResponseEntity<Usuario> createUsuario(@RequestBody Usuario usuario) {
        if (usuario.getOrganizacion() == null || usuario.getOrganizacion().getIdOrganizacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El ID de la organización es obligatorio.");
        }

        // Cargar la entidad Organización completa
        Organizacion organizacion = organizacionRepository.findById(usuario.getOrganizacion().getIdOrganizacion())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "La organización especificada no existe."));
        
        // Asignar la organización completa al usuario
        usuario.setOrganizacion(organizacion);

        // Hashear la contraseña antes de guardarla
        usuario.setPasswordHash(passwordEncoder.encode(usuario.getPasswordHash()));
        
        Usuario savedUsuario = usuarioService.save(usuario);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUsuario);
    }

    @GetMapping
    public ResponseEntity<List<Usuario>> getAllUsuarios() {
        return ResponseEntity.ok(usuarioService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Usuario> getUsuarioById(@PathVariable Integer id) {
        return usuarioService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Usuario> updateUsuario(@PathVariable Integer id, @RequestBody Usuario usuarioDetails) {
        try {
            return ResponseEntity.ok(usuarioService.updateUser(id, usuarioDetails));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Integer id) {
        usuarioService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
