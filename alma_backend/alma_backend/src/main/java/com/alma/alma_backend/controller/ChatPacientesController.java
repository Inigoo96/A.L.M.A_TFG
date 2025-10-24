package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.ChatPacientesRequestDTO;
import com.alma.alma_backend.dto.ChatPacientesResponseDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesRequestDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesResponseDTO;
import com.alma.alma_backend.service.ChatPacientesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat-pacientes")
public class ChatPacientesController {

    @Autowired
    private ChatPacientesService chatPacientesService;

    @PostMapping("/sesion")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<ChatPacientesResponseDTO> findOrCreateChat(@RequestBody ChatPacientesRequestDTO request) {
        return ResponseEntity.ok(chatPacientesService.findOrCreateChat(request));
    }

    @GetMapping("/{chatId}/mensajes")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<List<MensajeChatPacientesResponseDTO>> getMensajesByChatId(@PathVariable Integer chatId) {
        return ResponseEntity.ok(chatPacientesService.findMensajesByChatId(chatId));
    }

    @PostMapping("/mensajes")
    @PreAuthorize("hasRole('PACIENTE')")
    public ResponseEntity<MensajeChatPacientesResponseDTO> postMensaje(@RequestBody MensajeChatPacientesRequestDTO request) {
        return ResponseEntity.ok(chatPacientesService.postMensaje(request));
    }
}
