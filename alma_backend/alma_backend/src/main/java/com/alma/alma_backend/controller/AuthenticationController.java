package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.AuthenticationRequest;
import com.alma.alma_backend.dto.AuthenticationResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import com.alma.alma_backend.dto.RegisterRequest;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.security.JwtUtil;
import com.alma.alma_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final OrganizacionRepository organizacionRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService,
                                    UsuarioService usuarioService,
                                    OrganizacionRepository organizacionRepository,
                                    JwtUtil jwtUtil,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
        this.organizacionRepository = organizacionRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register-organization")
    @Transactional
    public ResponseEntity<?> registerOrganization(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            if (organizacionRepository.existsByCif(registerRequest.getCif())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("El CIF de la organización ya está registrado"));
            }
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("El email del administrador ya está en uso"));
            }

            Organizacion newOrganizacion = new Organizacion();
            newOrganizacion.setNombreOrganizacion(registerRequest.getNombreOrganizacion());
            newOrganizacion.setCif(registerRequest.getCif());
            Organizacion savedOrganizacion = organizacionRepository.save(newOrganizacion);

            Usuario newUser = new Usuario();
            newUser.setEmail(registerRequest.getEmail().toLowerCase().trim());
            newUser.setNombre(registerRequest.getNombre().trim());
            newUser.setApellidos(registerRequest.getApellidos().trim());
            newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
            newUser.setOrganizacion(savedOrganizacion);
            newUser.setTipoUsuario(TipoUsuario.ADMIN_ORGANIZACION);

            Usuario savedUser = usuarioService.save(newUser);

            logger.info("Organización '{}' y administrador '{}' registrados exitosamente", savedOrganizacion.getNombreOrganizacion(), savedUser.getEmail());

            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthenticationResponse(jwt, savedUser.getEmail(), savedUser.getTipoUsuario().name()));

        } catch (DataIntegrityViolationException e) {
            logger.error("Error de integridad al registrar organización: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(new ErrorResponse("Conflicto de datos. El CIF o el email ya existen."));
        } catch (Exception e) {
            logger.error("Error inesperado al registrar organización: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error interno al registrar la organización"));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            String email = authenticationRequest.getEmail().toLowerCase().trim();
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, authenticationRequest.getPassword())
            );

            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);
            final String jwt = jwtUtil.generateToken(userDetails);

            Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado después de una autenticación exitosa. Error de consistencia de datos."));

            logger.info("Usuario autenticado exitosamente: {}", email);

            return ResponseEntity.ok(
                new AuthenticationResponse(jwt, usuario.getEmail(), usuario.getTipoUsuario().name())
            );

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Email o contraseña incorrectos"));
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse("La cuenta está deshabilitada"));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.LOCKED).body(new ErrorResponse("La cuenta está bloqueada"));
        } catch (Exception e) {
            logger.error("Error durante la autenticación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorResponse("Error durante la autenticación"));
        }
    }
}
