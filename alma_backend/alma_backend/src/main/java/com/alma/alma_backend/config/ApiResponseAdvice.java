package com.alma.alma_backend.config;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Set;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    // Rutas que Swagger necesita exponer sin tocar
    private static final Set<String> SWAGGER_PATHS = Set.of(
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // Siempre true: la exclusión se maneja en beforeBodyWrite
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        String path = request.getURI().getPath();
        if (path == null) return body;

        for (String prefix : SWAGGER_PATHS) {
            if (path.startsWith(prefix)) {
                return body; // No tocar Swagger
            }
        }

        // También excluye OPTIONS (CORS preflight) y HEAD
        if ("OPTIONS".equalsIgnoreCase(request.getMethod().name()) ||
                "HEAD".equalsIgnoreCase(request.getMethod().name())) {
            return body;
        }

        // No envolver si ya viene envuelto o es nulo
        if (body instanceof ApiResponse || body instanceof ResponseEntity || body == null) {
            return body;
        }

        // Determinar código HTTP actual
        int status = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
            if (status == 0) status = 200;
        }

        // Manejo de errores estructurados
        if (status >= 400) {
            String message = extractErrorMessage(body);
            return ApiResponse.error(status, message, body);
        }

        // Envolver respuesta exitosa
        return ApiResponse.success(status, "OK", body);
    }

    private String extractErrorMessage(Object body) {
        if (body instanceof ErrorResponse errorResponse) {
            return errorResponse.getMessage();
        }
        return body != null ? body.toString() : "Error";
    }
}
