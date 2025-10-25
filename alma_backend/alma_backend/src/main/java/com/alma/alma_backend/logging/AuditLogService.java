package com.alma.alma_backend.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    public void logControllerEntry(String signature, Object[] args) {
        logger.info("[CONTROLLER] Entering {} with args {}", signature, formatArgs(args));
    }

    public void logControllerExit(String signature, Object result) {
        logger.info("[CONTROLLER] Exiting {} with result {}", signature, safeDescribe(result));
    }

    public void logControllerException(String signature, Throwable throwable) {
        logger.error("[CONTROLLER] Exception in {}", signature, throwable);
    }

    public void logServiceEntry(String signature, Object[] args) {
        logger.info("[SERVICE] Entering {} with args {}", signature, formatArgs(args));
    }

    public void logServiceExit(String signature, Object result) {
        logger.info("[SERVICE] Exiting {} with result {}", signature, safeDescribe(result));
    }

    public void logServiceException(String signature, Throwable throwable) {
        logger.error("[SERVICE] Exception in {}", signature, throwable);
    }

    public void logSecurityWarn(String message, Object... args) {
        logger.warn("[SECURITY] " + message, sanitizeArgs(args));
    }

    public void logAuthWarn(String message, Object... args) {
        logger.warn("[AUTH] " + message, sanitizeArgs(args));
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return "[]";
        }
        return Arrays.stream(args)
                .map(this::safeDescribe)
                .collect(Collectors.joining(", ", "[", "]"));
    }

    private Object[] sanitizeArgs(Object[] args) {
        if (args == null || args.length == 0) {
            return new Object[0];
        }
        return Arrays.stream(args)
                .map(this::safeDescribe)
                .toArray(Object[]::new);
    }

    private String safeDescribe(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof CharSequence || value instanceof Number || value instanceof Boolean || value instanceof Enum<?>) {
            return value.toString();
        }
        if (value instanceof ResponseEntity<?> response) {
            Object body = response.getBody();
            String bodyDescription = body == null ? "null" : body.getClass().getSimpleName();
            return "ResponseEntity(status=" + response.getStatusCode() + ", body=" + bodyDescription + ")";
        }
        if (value instanceof Optional<?> optional) {
            return optional.map(o -> "Optional[" + safeDescribe(o) + "]").orElse("Optional.empty");
        }
        if (value.getClass().isArray()) {
            return value.getClass().getComponentType().getSimpleName() + "[] length=" + Array.getLength(value);
        }
        if (value instanceof Collection<?> collection) {
            return value.getClass().getSimpleName() + "(size=" + collection.size() + ")";
        }
        if (value instanceof Map<?, ?> map) {
            return value.getClass().getSimpleName() + "(size=" + map.size() + ")";
        }
        return value.getClass().getSimpleName();
    }
}
