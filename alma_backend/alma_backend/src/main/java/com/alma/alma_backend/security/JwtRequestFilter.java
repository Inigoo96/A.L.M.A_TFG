package com.alma.alma_backend.security;

import com.alma.alma_backend.logging.AuditLogService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Filtro que intercepta las peticiones HTTP para validar tokens JWT.
 * Se ejecuta una vez por petición y establece el contexto de seguridad si el token es válido.
 */
@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtRequestFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final AuditLogService auditLogService;

    @Autowired
    public JwtRequestFilter(UserDetailsService userDetailsService, JwtUtil jwtUtil, AuditLogService auditLogService) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.auditLogService = auditLogService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                   @NonNull HttpServletResponse response,
                                   @NonNull FilterChain chain) throws ServletException, IOException {

        try {
            // Extraer el token JWT del header Authorization
            String jwt = extractJwtFromRequest(request);

            // Si hay un token y es estructuralmente válido
            if (StringUtils.hasText(jwt) && jwtUtil.validateTokenStructure(jwt)) {

                // Extraer el username del token
                String username = jwtUtil.extractUsername(jwt);

                // Si hay un username y no hay autenticación previa en el contexto
                if (StringUtils.hasText(username) && SecurityContextHolder.getContext().getAuthentication() == null) {

                    try {
                        // Cargar los detalles del usuario desde la base de datos
                        UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                        // Validar el token contra los detalles del usuario
                        if (jwtUtil.validateToken(jwt, userDetails)) {

                            // Crear el token de autenticación de Spring Security
                            UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                                );

                            // Establecer detalles adicionales de la petición
                            authenticationToken.setDetails(
                                new WebAuthenticationDetailsSource().buildDetails(request)
                            );

                            // Establecer la autenticación en el contexto de seguridad
                            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                            logger.debug("Usuario '{}' autenticado exitosamente mediante JWT", username);
                        } else {
                            auditLogService.logSecurityWarn("Token JWT inválido para el usuario: {}", username);
                            writeUnauthorizedResponse(request, response, "Invalid or expired token");
                            return;
                        }

                    } catch (UsernameNotFoundException e) {
                        auditLogService.logSecurityWarn("Usuario no encontrado para token JWT: {}", username);
                        writeUnauthorizedResponse(request, response, "Invalid or expired token");
                        return;
                    }
                }
            }

        } catch (JwtException e) {
            auditLogService.logSecurityWarn("Error al procesar token JWT: {}", e.getMessage());
            writeUnauthorizedResponse(request, response, "Invalid or expired token");
            return;
        } catch (Exception e) {
            logger.error("Error inesperado en el filtro JWT: {}", e.getMessage(), e);
            writeUnauthorizedResponse(request, response, "Invalid or expired token");
            return;
        }

        // Continuar con la cadena de filtros independientemente del resultado
        chain.doFilter(request, response);
    }

    /**
     * Extrae el token JWT del header Authorization.
     * Retorna null si no hay header, no tiene el prefijo Bearer, o está vacío.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);

        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            String token = bearerToken.substring(BEARER_PREFIX.length());
            return StringUtils.hasText(token) ? token : null;
        }

        return null;
    }

    /**
     * No aplicar este filtro a rutas públicas específicas para optimizar rendimiento.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        // No filtrar solo endpoints de autenticación públicos y recursos públicos
        return path.startsWith("/api/auth/") ||
               path.startsWith("/swagger-ui") ||
               path.startsWith("/v3/api-docs") ||
               path.startsWith("/swagger-resources") ||
               path.startsWith("/webjars") ||
               path.equals("/error");
    }

    private void writeUnauthorizedResponse(HttpServletRequest request, HttpServletResponse response, String message) throws IOException {
        if (response.isCommitted()) {
            return;
        }
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", message);
        body.put("path", request.getRequestURI());

        new ObjectMapper().writeValue(response.getOutputStream(), body);
    }
}
