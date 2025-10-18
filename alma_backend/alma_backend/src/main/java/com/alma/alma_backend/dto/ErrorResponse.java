package com.alma.alma_backend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * DTO para las respuestas de error en la API.
 */
@Data
public class ErrorResponse {

    private String message;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime timestamp;

    private String error;

    public ErrorResponse(String message) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.error = "Error";
    }

    public ErrorResponse(String message, String error) {
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.error = error;
    }
}