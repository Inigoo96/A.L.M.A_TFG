package com.alma.alma_backend.config;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.PathContainer;
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
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;
import java.util.stream.Stream;

@RestControllerAdvice(basePackages = "com.alma.alma_backend.controller")
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();
    private static final List<PathPattern> SPRINGDOC_PATH_PATTERNS = Stream.of(
            "/v3/api-docs",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/swagger-resources",
            "/swagger-resources/**",
            "/webjars/**"
    ).map(PATH_PATTERN_PARSER::parse).toList();
    private static final String SPRINGDOC_PACKAGE_PREFIX = "org.springdoc";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> controllerClass = returnType.getContainingClass();
        if (controllerClass != null && controllerClass.getPackageName().startsWith(SPRINGDOC_PACKAGE_PREFIX)) {
            return false;
        }
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
        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest servletHttpRequest = servletRequest.getServletRequest();
            if (servletHttpRequest != null) {
                return isSwaggerPath(servletHttpRequest);
            }
        }

        String path = request.getURI().getPath();
        return isSwaggerPath(path, null);
    }

    private boolean isSwaggerPath(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        return isSwaggerPath(request.getRequestURI(), request.getContextPath());
    }

    private boolean isSwaggerPath(String path) {
        return isSwaggerPath(path, null);
    }

    private boolean isSwaggerPath(String path, String contextPath) {
        if (path == null) {
            return false;
        }
        String lookupPath = normalizePath(path, contextPath);
        PathContainer pathContainer = PathContainer.parsePath(lookupPath);
        return SPRINGDOC_PATH_PATTERNS.stream().anyMatch(pattern -> pattern.matches(pathContainer));
    }

    private String normalizePath(String path, String contextPath) {
        if (contextPath != null && !contextPath.isEmpty() && path.startsWith(contextPath)) {
            String withoutContext = path.substring(contextPath.length());
            return withoutContext.isEmpty() ? "/" : withoutContext;
        }
        return path;
    }

    private String extractErrorMessage(Object body) {
        if (body instanceof ErrorResponse errorResponse) {
            return errorResponse.getMessage();
        }
        return body != null ? body.toString() : "Error";
    }

}
