package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.AuthenticationRequest;
import com.alma.alma_backend.dto.AuthenticationResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import com.alma.alma_backend.dto.RegisterRequest;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
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
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de autenticación que maneja el registro de usuarios y el login.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthenticationController(AuthenticationManager authenticationManager,
                                    UserDetailsService userDetailsService,
                                    UsuarioService usuarioService,
                                    JwtUtil jwtUtil,
                                    PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Endpoint para registrar un nuevo usuario en el sistema.
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            // Validar que el email no esté ya registrado
            if (usuarioService.existsByEmail(registerRequest.getEmail())) {
                logger.warn("Intento de registro con email ya existente: {}", registerRequest.getEmail());
                return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("El email ya está registrado"));
            }

            // Validar formato del email
            if (!isValidEmail(registerRequest.getEmail())) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Formato de email inválido"));
            }

            // Validar longitud de la contraseña
            if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 8) {
                return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("La contraseña debe tener al menos 8 caracteres"));
            }

            // Crear una nueva entidad Usuario a partir del DTO de registro
            Usuario newUser = new Usuario();
            newUser.setEmail(registerRequest.getEmail().toLowerCase().trim());
            newUser.setNombre(registerRequest.getNombre().trim());
            newUser.setApellidos(registerRequest.getApellidos().trim());

            // Hashear la contraseña del DTO antes de guardarla en la entidad
            newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

            // Asignar un rol por defecto para los nuevos registros
            newUser.setTipoUsuario(TipoUsuario.PACIENTE);

            Usuario savedUser = usuarioService.save(newUser);

            logger.info("Usuario registrado exitosamente: {}", savedUser.getEmail());

            // Generar token JWT automáticamente tras el registro
            final UserDetails userDetails = userDetailsService.loadUserByUsername(savedUser.getEmail());
            final String jwt = jwtUtil.generateToken(userDetails);

            return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new AuthenticationResponse(jwt, savedUser.getEmail(), savedUser.getTipoUsuario().name()));

        } catch (DataIntegrityViolationException e) {
            logger.error("Error de integridad al registrar usuario: {}", e.getMessage());
            return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("El email ya está registrado"));
        } catch (Exception e) {
            logger.error("Error al registrar usuario: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error al registrar el usuario"));
        }
    }

    /**
     * Endpoint para autenticar a un usuario y generar un token JWT.
     */
    @PostMapping("/login")
    public ResponseEntity<?> createAuthenticationToken(@Valid @RequestBody AuthenticationRequest authenticationRequest) {
        try {
            // Normalizar email
            String email = authenticationRequest.getEmail().toLowerCase().trim();

            // Intentar autenticar al usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, authenticationRequest.getPassword())
            );

            // Si la autenticación es exitosa, cargar los detalles del usuario
            final UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            // Generar token JWT
            final String jwt = jwtUtil.generateToken(userDetails);

            // Obtener información adicional del usuario
            Usuario usuario = usuarioService.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            logger.info("Usuario autenticado exitosamente: {}", email);

            return ResponseEntity.ok(
                new AuthenticationResponse(jwt, usuario.getEmail(), usuario.getTipoUsuario().name())
            );

        } catch (BadCredentialsException e) {
            logger.warn("Intento de login fallido - credenciales incorrectas para: {}",
                authenticationRequest.getEmail());
            return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse("Email o contraseña incorrectos"));

        } catch (DisabledException e) {
            logger.warn("Intento de login con cuenta deshabilitada: {}", authenticationRequest.getEmail());
            return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse("La cuenta está deshabilitada"));

        } catch (LockedException e) {
            logger.warn("Intento de login con cuenta bloqueada: {}", authenticationRequest.getEmail());
            return ResponseEntity
                .status(HttpStatus.LOCKED)
                .body(new ErrorResponse("La cuenta está bloqueada"));

        } catch (Exception e) {
            logger.error("Error durante la autenticación: {}", e.getMessage(), e);
            return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Error durante la autenticación"));
        }
    }

    /**
     * Valida que el formato del email sea correcto.
     */
    private boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Expresión regular básica para validar emails
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }
}
