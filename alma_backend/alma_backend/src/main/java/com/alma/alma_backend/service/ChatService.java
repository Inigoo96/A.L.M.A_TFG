package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.MensajeChatRequestDTO;
import com.alma.alma_backend.dto.MensajeChatResponseDTO;
import com.alma.alma_backend.dto.SesionChatRequestDTO;
import com.alma.alma_backend.dto.SesionChatResponseDTO;

import java.util.List;

public interface ChatService {

    SesionChatResponseDTO iniciarSesionChat(SesionChatRequestDTO request);

    SesionChatResponseDTO obtenerSesionPorId(Integer id);

    SesionChatResponseDTO obtenerOCrearSesionActiva(Integer idPaciente, Integer idProfesional);

    List<SesionChatResponseDTO> obtenerSesionesPorPaciente(Integer idPaciente);

    List<SesionChatResponseDTO> obtenerSesionesPorProfesional(Integer idProfesional);

    List<SesionChatResponseDTO> obtenerSesionesActivas();

    SesionChatResponseDTO archivarSesion(Integer id);

    MensajeChatResponseDTO enviarMensaje(MensajeChatRequestDTO request, Integer idRemitente);

    List<MensajeChatResponseDTO> obtenerMensajesPorSesion(Integer idSesionChat);

    MensajeChatResponseDTO marcarMensajeComoLeido(Integer idMensaje);

    void marcarMensajesComoLeidos(Integer idSesionChat, Integer idUsuario);

    Long contarMensajesNoLeidos(Integer idSesionChat);
}