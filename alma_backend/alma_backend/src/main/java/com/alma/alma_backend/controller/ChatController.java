package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.MensajeChatRequestDTO;
import com.alma.alma_backend.dto.MensajeChatResponseDTO;
import com.alma.alma_backend.dto.SesionChatRequestDTO;
import com.alma.alma_backend.dto.SesionChatResponseDTO;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.mapper.ChatMapper;
import com.alma.alma_backend.service.ChatService;
import com.alma.alma_backend.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Autowired
    private ChatService chatService;

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/sesion")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<SesionChatResponseDTO> iniciarSesion(@Valid @RequestBody SesionChatRequestDTO request) {
        logger.info("Iniciando sesión de chat entre paciente ID: {} y profesional ID: {}",
                request.getIdPaciente(), request.getIdProfesional());
        var sesion = chatService.iniciarSesionChat(request);
        long unread = chatService.contarMensajesNoLeidos(sesion.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ChatMapper.toResponse(sesion, unread));
    }

    @GetMapping("/sesion/{id}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<SesionChatResponseDTO> obtenerSesion(@PathVariable Integer id) {
        logger.debug("Obteniendo sesión de chat ID: {}", id);
        var sesion = chatService.obtenerSesionPorId(id);
        return ResponseEntity.ok(ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())));
    }

    @GetMapping("/sesion/activa")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<SesionChatResponseDTO> obtenerOCrearSesionActiva(
            @RequestParam Integer idPaciente,
            @RequestParam Integer idProfesional) {
        logger.debug("Obteniendo o creando sesión activa entre paciente ID: {} y profesional ID: {}",
                idPaciente, idProfesional);
        var sesion = chatService.obtenerOCrearSesionActiva(idPaciente, idProfesional);
        return ResponseEntity.ok(ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())));
    }

    @GetMapping("/sesiones/paciente/{idPaciente}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<SesionChatResponseDTO>> obtenerSesionesPaciente(@PathVariable Integer idPaciente) {
        logger.debug("Obteniendo sesiones del paciente ID: {}", idPaciente);
        List<SesionChatResponseDTO> sesiones = chatService.obtenerSesionesPorPaciente(idPaciente).stream()
                .map(sesion -> ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sesiones);
    }

    @GetMapping("/sesiones/profesional/{idProfesional}")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<SesionChatResponseDTO>> obtenerSesionesProfesional(@PathVariable Integer idProfesional) {
        logger.debug("Obteniendo sesiones del profesional ID: {}", idProfesional);
        List<SesionChatResponseDTO> sesiones = chatService.obtenerSesionesPorProfesional(idProfesional).stream()
                .map(sesion -> ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sesiones);
    }

    @GetMapping("/sesiones/activas")
    @PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'SUPER_ADMIN')")
    public ResponseEntity<List<SesionChatResponseDTO>> obtenerSesionesActivas() {
        logger.debug("Obteniendo todas las sesiones activas");
        List<SesionChatResponseDTO> sesiones = chatService.obtenerSesionesActivas().stream()
                .map(sesion -> ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())))
                .collect(Collectors.toList());
        return ResponseEntity.ok(sesiones);
    }

    @PutMapping("/sesion/{id}/archivar")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<SesionChatResponseDTO> archivarSesion(@PathVariable Integer id) {
        logger.info("Archivando sesión de chat ID: {}", id);
        var sesion = chatService.archivarSesion(id);
        return ResponseEntity.ok(ChatMapper.toResponse(sesion, chatService.contarMensajesNoLeidos(sesion.getId())));
    }

    @PostMapping("/mensaje")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<MensajeChatResponseDTO> enviarMensaje(
            @Valid @RequestBody MensajeChatRequestDTO request,
            Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        logger.info("Usuario ID: {} enviando mensaje en sesión ID: {}",
                currentUser.getId(), request.getIdSesionChat());

        MensajeChatResponseDTO mensaje = ChatMapper.toResponse(chatService.enviarMensaje(request, currentUser.getId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(mensaje);
    }

    @GetMapping("/sesion/{idSesionChat}/mensajes")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE', 'ADMIN_ORGANIZACION')")
    public ResponseEntity<List<MensajeChatResponseDTO>> obtenerMensajes(@PathVariable Integer idSesionChat) {
        logger.debug("Obteniendo mensajes de la sesión ID: {}", idSesionChat);
        List<MensajeChatResponseDTO> mensajes = chatService.obtenerMensajesPorSesion(idSesionChat).stream()
                .map(ChatMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(mensajes);
    }

    @PutMapping("/mensaje/{idMensaje}/leer")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<MensajeChatResponseDTO> marcarComoLeido(@PathVariable Integer idMensaje) {
        logger.debug("Marcando mensaje ID: {} como leído", idMensaje);
        MensajeChatResponseDTO mensaje = ChatMapper.toResponse(chatService.marcarMensajeComoLeido(idMensaje));
        return ResponseEntity.ok(mensaje);
    }

    @PutMapping("/sesion/{idSesionChat}/marcar-leidos")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<Void> marcarTodosComoLeidos(
            @PathVariable Integer idSesionChat,
            Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        logger.info("Usuario ID: {} marcando todos los mensajes como leídos en sesión ID: {}",
                currentUser.getId(), idSesionChat);

        chatService.marcarMensajesComoLeidos(idSesionChat, currentUser.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/sesion/{idSesionChat}/no-leidos")
    @PreAuthorize("hasAnyRole('PROFESIONAL', 'PACIENTE')")
    public ResponseEntity<Long> contarMensajesNoLeidos(@PathVariable Integer idSesionChat) {
        logger.debug("Contando mensajes no leídos en sesión ID: {}", idSesionChat);
        Long count = chatService.contarMensajesNoLeidos(idSesionChat);
        return ResponseEntity.ok(count);
    }
}