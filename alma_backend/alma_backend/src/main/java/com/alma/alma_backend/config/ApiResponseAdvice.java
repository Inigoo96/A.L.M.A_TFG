package com.alma.alma_backend.config;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Set;

@RestControllerAdvice
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final Set<String> SWAGGER_PATH_PREFIXES = Set.of(
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-resources",
        "/webjars/swagger-ui"
    );

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        if (isSwaggerRequest()) {
            return false;
        }
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {
        if (isSwaggerRequest(request)) {
            return body;
        }

        if (body instanceof ApiResponse || body instanceof ResponseEntity || body == null) {
            return body;
        }

        int status = 200;
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
            if (status == 0) {
                status = 200;
            }
        }

        if (status >= 400) {
            String message = extractErrorMessage(body);
            return ApiResponse.error(status, message, body);
        }

        return ApiResponse.success(status, "OK", body);
    }

    private boolean isSwaggerRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes servletAttributes) {
            return isSwaggerPath(servletAttributes.getRequest());
        }
        return false;
    }

    private boolean isSwaggerRequest(ServerHttpRequest request) {
        String path = request.getURI().getPath();
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest servletHttpRequest = servletRequest.getServletRequest();
            if (servletHttpRequest != null) {
                return isSwaggerPath(servletHttpRequest);
            }
        }

        return path != null && isSwaggerPath(path);
    }

    private boolean isSwaggerPath(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return isSwaggerPath(request.getRequestURI());
    }

    private boolean isSwaggerPath(String path) {
        if (path == null) {
            return false;
        }
        return SWAGGER_PATH_PREFIXES.stream().anyMatch(path::startsWith);
    }

    private String extractErrorMessage(Object body) {
        if (body instanceof ErrorResponse errorResponse) {
            return errorResponse.getMessage();
        }
        return body != null ? body.toString() : "Error";
    }

}
