package com.alma.alma_backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Utilidad para la generación y validación de tokens JWT.
 * Proporciona métodos para crear, parsear y validar tokens de autenticación.
 */
@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret.key}")
    private String secretKeyString;

    @Value("${jwt.expiration.ms:36000000}") // Por defecto 10 horas
    private long jwtExpirationMs;

    @Value("${jwt.issuer:alma-backend}")
    private String jwtIssuer;

    private SecretKey signingKey;

    /**
     * Inicializa la clave de firma después de la inyección de dependencias.
     * Valida que la clave tenga la longitud mínima requerida para HS512.
     */
    @PostConstruct
    public void init() {
        if (!StringUtils.hasText(secretKeyString)) {
            throw new IllegalStateException("La clave secreta JWT no puede estar vacía");
        }

        // HS512 requiere al menos 512 bits (64 bytes)
        if (secretKeyString.getBytes(StandardCharsets.UTF_8).length < 64) {
            logger.warn("ADVERTENCIA: La clave secreta JWT es demasiado corta. " +
                "Se recomienda al menos 64 caracteres para HS512");
        }

        this.signingKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
        logger.info("JwtUtil inicializado correctamente. Expiración: {} ms", jwtExpirationMs);
    }

    /**
     * Extrae el nombre de usuario (subject) del token.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrae la fecha de expiración del token.
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrae los roles del token.
     */
    public String extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", String.class));
    }

    /**
     * Extrae un claim específico del token.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Extrae todos los claims del token.
     * Lanza excepciones si el token es inválido.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Verifica si el token ha expirado.
     */
    private Boolean isTokenExpired(String token) {
        try {
            return extractExpiration(token).before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    /**
     * Genera un nuevo token JWT para el usuario.
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Añadimos los roles del usuario como una claim en el token
        String roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));
        claims.put("roles", roles);

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Crea el token JWT con los claims y configuración especificados.
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer(jwtIssuer)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Valida el token JWT verificando:
     * 1. Que la firma sea válida
     * 2. Que el token no haya expirado
     * 3. Que el subject coincida con el username
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        if (token == null || token.trim().isEmpty()) {
            logger.error("Token vacío o nulo");
            return false;
        }

        try {
            final String username = extractUsername(token);

            // Validar que el username del token coincida con el del usuario
            if (!username.equals(userDetails.getUsername())) {
                logger.warn("El username del token no coincide con el usuario autenticado");
                return false;
            }

            // Validar que el token no haya expirado
            if (isTokenExpired(token)) {
                logger.warn("Token expirado para el usuario: {}", username);
                return false;
            }

            logger.debug("Token válido para el usuario: {}", username);
            return true;

        } catch (SignatureException e) {
            logger.error("Firma JWT inválida: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Token JWT malformado: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("Token JWT expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("Token JWT no soportado: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("Claims JWT vacíos: {}", e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al validar token: {}", e.getMessage());
        }

        return false;
    }

    /**
     * Valida el token sin necesidad de UserDetails.
     * Útil para validaciones preliminares.
     */
    public Boolean validateTokenStructure(String token) {
        if (token == null || token.trim().isEmpty()) {
            return false;
        }

        try {
            Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            logger.debug("Token inválido: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Obtiene el tiempo de expiración configurado en milisegundos.
     */
    public long getJwtExpirationMs() {
        return jwtExpirationMs;
    }
}
