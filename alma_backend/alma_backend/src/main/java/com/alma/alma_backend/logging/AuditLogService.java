package com.alma.alma_backend.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Arrays;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    public void logControllerEntry(String signature, Object[] args) {
        logger.info("[CONTROLLER] Entering {} with args {}", signature, Arrays.toString(args));
    }

    public void logControllerExit(String signature, Object result) {
        logger.info("[CONTROLLER] Exiting {} with result {}", signature, result);
    }

    public void logControllerException(String signature, Throwable throwable) {
        logger.error("[CONTROLLER] Exception in {}", signature, throwable);
    }

    public void logServiceEntry(String signature, Object[] args) {
        logger.info("[SERVICE] Entering {} with args {}", signature, Arrays.toString(args));
    }

    public void logServiceExit(String signature, Object result) {
        logger.info("[SERVICE] Exiting {} with result {}", signature, result);
    }

    public void logServiceException(String signature, Throwable throwable) {
        logger.error("[SERVICE] Exception in {}", signature, throwable);
    }

    public void logSecurityWarn(String message, Object... args) {
        logger.warn("[SECURITY] " + message, args);
    }

    public void logAuthWarn(String message, Object... args) {
        logger.warn("[AUTH] " + message, args);
    }
}
