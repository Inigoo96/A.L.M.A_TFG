package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ApiResponse;
import com.alma.alma_backend.dto.ErrorResponse;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Map;
import java.util.Set;

@RestControllerAdvice(basePackages = ApiResponseAdvice.APPLICATION_CONTROLLER_PACKAGE)
public class ApiResponseAdvice implements ResponseBodyAdvice<Object> {

    public static final String APPLICATION_CONTROLLER_PACKAGE = "com.alma.alma_backend.controller";
    private static final Set<String> FRAMEWORK_PATH_PREFIXES = Set.of(
        "/swagger-ui",
        "/v3/api-docs",
        "/swagger-resources",
        "/webjars",
        "/error"
    );
    private static final String WRAPPED_ATTRIBUTE = ApiResponseAdvice.class.getName() + ".WRAPPED";
    private static final String ERROR_URI_ATTRIBUTE = "jakarta.servlet.error.request_uri";

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> controllerClass = returnType.getContainingClass();

        if (controllerClass == null) {
            return false;
        }

        if (!controllerClass.getPackageName().startsWith(APPLICATION_CONTROLLER_PACKAGE) ||
                BasicErrorController.class.isAssignableFrom(controllerClass) ||
                ResponseEntityExceptionHandler.class.isAssignableFrom(controllerClass)) {
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
        if (body == null || body instanceof ApiResponse) {
            return body;
        }

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();
            if (shouldSkip(httpRequest)) {
                return body;
            }
            httpRequest.setAttribute(WRAPPED_ATTRIBUTE, Boolean.TRUE);
        }

        return wrapBody(body, response);
    }

    private Object wrapBody(Object body, ServerHttpResponse response) {
        int status = HttpStatus.OK.value();
        if (response instanceof ServletServerHttpResponse servletResponse) {
            status = servletResponse.getServletResponse().getStatus();
            if (status <= 0) {
                status = HttpStatus.OK.value();
            }
        }

        if (status >= HttpStatus.BAD_REQUEST.value()) {
            return ApiResponse.error(status, extractErrorMessage(body), null);
        }

        return ApiResponse.success(status, "OK", body);
    }

    private boolean shouldSkip(HttpServletRequest request) {
        if (request == null) {
            return true;
        }
        if (Boolean.TRUE.equals(request.getAttribute(WRAPPED_ATTRIBUTE))) {
            return true;
        }
        if (request.getDispatcherType() == DispatcherType.ERROR) {
            return true;
        }

        String path = (String) request.getAttribute(ERROR_URI_ATTRIBUTE);
        if (path == null || path.isBlank()) {
            path = request.getRequestURI();
        }

        if (path == null) {
            return true;
        }

        String normalized = normalizePath(path, request.getContextPath());
        return FRAMEWORK_PATH_PREFIXES.stream().anyMatch(normalized::startsWith);
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
        if (body instanceof ProblemDetail problemDetail) {
            if (problemDetail.getDetail() != null && !problemDetail.getDetail().isBlank()) {
                return problemDetail.getDetail();
            }
            if (problemDetail.getTitle() != null && !problemDetail.getTitle().isBlank()) {
                return problemDetail.getTitle();
            }
        }
        if (body instanceof Map) {
            Object message = ((Map<?, ?>) body).get("message");
            if (message != null) {
                return message.toString();
            }
        }
        return body != null ? body.toString() : "Error";
    }
}
