package com.alma.alma_backend.security;

import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

/**
 * Implementación personalizada de UserDetailsService para Spring Security.
 * Carga los detalles del usuario desde la base de datos durante el proceso de autenticación.
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(UserDetailsServiceImpl.class);

    private final UsuarioRepository usuarioRepository;

    @Autowired
    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Carga un usuario por su email (usado como username en el sistema).
     * Este método es llamado por Spring Security durante el proceso de autenticación.
     *
     * @param email El email del usuario (usado como username)
     * @return UserDetails con la información del usuario y sus roles
     * @throws UsernameNotFoundException si el usuario no existe
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        logger.debug("Intentando cargar usuario con email: {}", email);

        if (email == null || email.trim().isEmpty()) {
            logger.error("Intento de carga de usuario con email vacío o nulo");
            throw new UsernameNotFoundException("El email no puede estar vacío");
        }

        // Normalizar el email a minúsculas para búsqueda case-insensitive
        String normalizedEmail = email.toLowerCase().trim();

        // Buscar el usuario en la base de datos
        Usuario usuario = usuarioRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> {
                    logger.warn("Usuario no encontrado con el email: {}", normalizedEmail);
                    return new UsernameNotFoundException(
                        "No se encontró un usuario con el email: " + normalizedEmail
                    );
                });

        // Verificar que el usuario tenga un tipo asignado
        if (usuario.getTipoUsuario() == null) {
            logger.error("Usuario {} no tiene tipo de usuario asignado", usuario.getEmail());
            throw new IllegalStateException("El usuario no tiene un rol asignado");
        }

        // Crear la autoridad (rol) para Spring Security
        // El prefijo "ROLE_" es una convención de Spring Security
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(
            "ROLE_" + usuario.getTipoUsuario().name()
        );

        logger.debug("Usuario cargado exitosamente: {} con rol: {}",
            usuario.getEmail(), usuario.getTipoUsuario().name());

        // Devolver un objeto User de Spring Security que implementa UserDetails
        // Contiene: email (username), contraseña hasheada y lista de roles/autoridades
        return new User(
            usuario.getEmail(),
            usuario.getPasswordHash(),
            true, // enabled
            true, // accountNonExpired
            true, // credentialsNonExpired
            true, // accountNonLocked
            Collections.singletonList(authority)
        );
    }
}
