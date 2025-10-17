package com.alma.alma_backend.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class PruebaController {

    @GetMapping("/prueba")
    public Map<String, String> prueba() {
        return Map.of(
            "status", "OK",
            "message", "Backend funcionando correctamente"
        );
    }
}