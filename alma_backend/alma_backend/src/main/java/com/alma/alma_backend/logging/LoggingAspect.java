package com.alma.alma_backend.logging;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class LoggingAspect {

    private final AuditLogService auditLogService;

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void controllerBeans() {
    }

    @Pointcut("within(@org.springframework.stereotype.Service *)")
    public void serviceBeans() {
    }

    @Around("controllerBeans()")
    public Object logController(final ProceedingJoinPoint joinPoint) throws Throwable {
        auditLogService.logControllerEntry(joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            auditLogService.logControllerExit(joinPoint.getSignature().toShortString(), result);
            return result;
        } catch (Throwable throwable) {
            auditLogService.logControllerException(joinPoint.getSignature().toShortString(), throwable);
            throw throwable;
        }
    }

    @Around("serviceBeans()")
    public Object logService(final ProceedingJoinPoint joinPoint) throws Throwable {
        auditLogService.logServiceEntry(joinPoint.getSignature().toShortString(), joinPoint.getArgs());
        try {
            Object result = joinPoint.proceed();
            auditLogService.logServiceExit(joinPoint.getSignature().toShortString(), result);
            return result;
        } catch (Throwable throwable) {
            auditLogService.logServiceException(joinPoint.getSignature().toShortString(), throwable);
            throw throwable;
        }
    }
}
