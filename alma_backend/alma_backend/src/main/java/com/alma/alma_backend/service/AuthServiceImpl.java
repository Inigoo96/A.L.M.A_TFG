package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.AuthenticationRequest;
import com.alma.alma_backend.dto.AuthenticationResponse;
import com.alma.alma_backend.dto.OrganizacionRegistroDTO;
import com.alma.alma_backend.dto.PacienteRegistroDTO;
import com.alma.alma_backend.dto.ProfesionalRegistroDTO;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.logging.AuditLogService;
import com.alma.alma_backend.repository.*;
import com.alma.alma_backend.security.JwtUtil;
import com.alma.alma_backend.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final OrganizacionRepository organizacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProfesionalRepository profesionalRepository;
    private final PacienteRepository pacienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    @Override
    @Transactional
    public Usuario registrarOrganizacionYAdmin(OrganizacionRegistroDTO registroDTO) {
        // === VALIDACIONES DE FORMATO ===

        // Validar CIF
        if (!ValidationUtils.isValidCIF(registroDTO.getCif())) {
            throw new IllegalArgumentException("El CIF de la organización no es válido.");
        }

        // Validar Número de Seguridad Social
        if (!ValidationUtils.isValidNumeroSeguridadSocial(registroDTO.getNumeroSeguridadSocial())) {
            throw new IllegalArgumentException("El número de la Seguridad Social no es válido. Formato: PP/NNNNNNNN/DD (12 dígitos).");
        }

        // Validar Email Corporativo (no dominios públicos)
        if (!ValidationUtils.isValidEmailCorporativo(registroDTO.getEmailCorporativo())) {
            throw new IllegalArgumentException("El email debe ser corporativo, no se permiten dominios públicos (gmail, hotmail, etc.).");
        }

        // Validar código REGCESS
        if (!ValidationUtils.isValidCodigoREGCESS(registroDTO.getCodigoRegcess())) {
            throw new IllegalArgumentException("El código REGCESS no tiene un formato válido.");
        }

        // Validar DNI del administrador
        if (!ValidationUtils.isValidDNIorNIE(registroDTO.getAdministrador().getDni())) {
            throw new IllegalArgumentException("El DNI/NIE del administrador no es válido.");
        }

        // === VALIDACIONES DE EXISTENCIA ===

        if (organizacionRepository.findByCif(registroDTO.getCif()).isPresent()) {
            throw new IllegalStateException("El CIF de la organización ya está registrado.");
        }
        if (usuarioRepository.findByEmail(registroDTO.getAdministrador().getEmail()).isPresent()) {
            throw new IllegalStateException("El email del administrador ya está en uso.");
        }

        // === CREACIÓN DE ENTIDADES ===

        Organizacion nuevaOrganizacion = new Organizacion();
        nuevaOrganizacion.setCif(registroDTO.getCif());
        nuevaOrganizacion.setNumeroSeguridadSocial(registroDTO.getNumeroSeguridadSocial());
        nuevaOrganizacion.setNombreOficial(registroDTO.getNombreOficial());
        nuevaOrganizacion.setDireccion(registroDTO.getDireccion());
        nuevaOrganizacion.setCodigoRegcess(registroDTO.getCodigoRegcess());
        nuevaOrganizacion.setEmailCorporativo(registroDTO.getEmailCorporativo());
        nuevaOrganizacion.setTelefonoContacto(registroDTO.getTelefonoContacto());
        nuevaOrganizacion.setEstadoVerificacion(EstadoVerificacion.PENDIENTE_VERIFICACION);
        Organizacion organizacionGuardada = organizacionRepository.save(nuevaOrganizacion);

        Usuario adminUsuario = new Usuario();
        OrganizacionRegistroDTO.AdminDTO adminDTO = registroDTO.getAdministrador();
        adminUsuario.setDni(adminDTO.getDni());
        adminUsuario.setNombre(adminDTO.getNombre());
        adminUsuario.setApellidos(adminDTO.getApellidos());
        adminUsuario.setEmail(adminDTO.getEmail());
        adminUsuario.setTelefono(adminDTO.getTelefono());
        adminUsuario.setPasswordHash(passwordEncoder.encode(adminDTO.getPassword()));
        adminUsuario.setCargo(adminDTO.getCargo());
        adminUsuario.setTipoUsuario(TipoUsuario.ADMIN_ORGANIZACION);
        adminUsuario.setOrganizacion(organizacionGuardada);
        adminUsuario.setActivo(true);
        adminUsuario.setPasswordTemporal(false);
        adminUsuario.setFechaRegistro(LocalDateTime.now());

        return usuarioRepository.save(adminUsuario);
    }

    @Override
    @Transactional
    public Usuario registrarProfesional(ProfesionalRegistroDTO registroDTO, Integer organizacionId) {
        // 0. Validaciones de formato
        if (!ValidationUtils.isValidDNIorNIE(registroDTO.getDni())) {
            throw new IllegalArgumentException("El DNI del profesional no es válido.");
        }
        if (!ValidationUtils.isValidEmail(registroDTO.getEmail())) {
            throw new IllegalArgumentException("El email del profesional no es válido.");
        }
        if (!ValidationUtils.isValidNumeroColegiado(registroDTO.getNumeroColegiado())) {
            throw new IllegalArgumentException("El número de colegiado no es válido.");
        }

        // 1. Validar que el profesional no exista
        if (usuarioRepository.findByDni(registroDTO.getDni()).isPresent()) {
            throw new IllegalStateException("El DNI del profesional ya está registrado.");
        }
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("El email del profesional ya está en uso.");
        }

        // 2. Buscar la organización
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new IllegalStateException("Organización no encontrada."));

        // 3. Crear el usuario base
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(registroDTO.getDni());
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellidos(registroDTO.getApellidos());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setTipoUsuario(TipoUsuario.PROFESIONAL);
        nuevoUsuario.setOrganizacion(organizacion);
        nuevoUsuario.setPasswordTemporal(true);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(tempPassword));
        logger.info("Contraseña temporal generada para {}", nuevoUsuario.getEmail());

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Crear la entidad Profesional
        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setUsuario(usuarioGuardado);
        nuevoProfesional.setNumeroColegiado(registroDTO.getNumeroColegiado());
        nuevoProfesional.setEspecialidad(registroDTO.getEspecialidad());
        nuevoProfesional.setCentroSalud(registroDTO.getCentroSalud());
        profesionalRepository.save(nuevoProfesional);

        return usuarioGuardado;
    }

    @Override
    @Transactional
    public Usuario registrarPaciente(PacienteRegistroDTO registroDTO, Integer organizacionId) {
        // 0. Validaciones de formato
        if (!ValidationUtils.isValidDNIorNIE(registroDTO.getDni())) {
            throw new IllegalArgumentException("El DNI del paciente no es válido.");
        }
        if (!ValidationUtils.isValidEmail(registroDTO.getEmail())) {
            throw new IllegalArgumentException("El email del paciente no es válido.");
        }

        // 1. Validar que el paciente no exista
        if (usuarioRepository.findByDni(registroDTO.getDni()).isPresent()) {
            throw new IllegalStateException("El DNI del paciente ya está registrado.");
        }
        if (usuarioRepository.findByEmail(registroDTO.getEmail()).isPresent()) {
            throw new IllegalStateException("El email del paciente ya está en uso.");
        }

        // 2. Buscar la organización
        Organizacion organizacion = organizacionRepository.findById(organizacionId)
                .orElseThrow(() -> new IllegalStateException("Organización no encontrada."));

        // 3. Crear el usuario base
        Usuario nuevoUsuario = new Usuario();
        nuevoUsuario.setDni(registroDTO.getDni());
        nuevoUsuario.setNombre(registroDTO.getNombre());
        nuevoUsuario.setApellidos(registroDTO.getApellidos());
        nuevoUsuario.setEmail(registroDTO.getEmail());
        nuevoUsuario.setTelefono(registroDTO.getTelefono());
        nuevoUsuario.setTipoUsuario(TipoUsuario.PACIENTE);
        nuevoUsuario.setOrganizacion(organizacion);
        nuevoUsuario.setPasswordTemporal(true);
        nuevoUsuario.setActivo(true);
        nuevoUsuario.setFechaRegistro(LocalDateTime.now());

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);
        nuevoUsuario.setPasswordHash(passwordEncoder.encode(tempPassword));
        logger.info("Contraseña temporal generada para {}", nuevoUsuario.getEmail());

        Usuario usuarioGuardado = usuarioRepository.save(nuevoUsuario);

        // 4. Crear la entidad Paciente
        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setUsuario(usuarioGuardado);
        nuevoPaciente.setTarjetaSanitaria(registroDTO.getTarjetaSanitaria());
        nuevoPaciente.setFechaNacimiento(registroDTO.getFechaNacimiento());
        nuevoPaciente.setGenero(registroDTO.getGenero());
        pacienteRepository.save(nuevoPaciente);

        return usuarioGuardado;
    }

    @Override
    @Transactional
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        String normalizedEmail = authenticationRequest.getEmail().toLowerCase().trim();
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, authenticationRequest.getPassword())
            );
        } catch (BadCredentialsException ex) {
            auditLogService.logSecurityWarn("Intento de inicio de sesión con credenciales inválidas para {}", normalizedEmail);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email o contraseña incorrectos", ex);
        } catch (DisabledException ex) {
            auditLogService.logSecurityWarn("Intento de inicio de sesión en cuenta deshabilitada: {}", normalizedEmail);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está deshabilitada", ex);
        } catch (LockedException ex) {
            auditLogService.logSecurityWarn("Intento de inicio de sesión en cuenta bloqueada: {}", normalizedEmail);
            throw new ResponseStatusException(HttpStatus.LOCKED, "La cuenta está bloqueada", ex);
        }

        Usuario usuario = usuarioRepository.findByEmail(normalizedEmail)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            auditLogService.logSecurityWarn("Usuario {} intentó iniciar sesión estando inactivo", normalizedEmail);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "La cuenta está deshabilitada");
        }

        usuario.setUltimoAcceso(LocalDateTime.now());
        usuarioRepository.save(usuario);

        UserDetails userDetails = userDetailsService.loadUserByUsername(normalizedEmail);
        String jwt = jwtUtil.generateToken(userDetails);

        auditLogService.logAuthWarn("Usuario autenticado: {}", normalizedEmail);

        return new AuthenticationResponse(jwt, usuario.getEmail(), usuario.getTipoUsuario().name(), usuario.getPasswordTemporal());
    }
}
