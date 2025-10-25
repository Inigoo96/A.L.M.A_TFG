package com.alma.alma_backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final Instant timestamp;
    private final int status;
    private final String message;
    private final T data;

    private ApiResponse(Instant timestamp, int status, String message, T data) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
        this.data = data;
    }

    public static <T> ApiResponse<T> success(int status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status, message, data);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(200, "OK", data);
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(200, message, null);
    }

    public static <T> ApiResponse<T> error(int status, String message, T data) {
        return new ApiResponse<>(Instant.now(), status, message, data);
    }

    public static ApiResponse<Void> error(int status, String message) {
        return new ApiResponse<>(Instant.now(), status, message, null);
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }
}
