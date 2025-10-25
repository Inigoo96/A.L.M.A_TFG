package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.MensajeChatRequestDTO;
import com.alma.alma_backend.dto.SesionChatRequestDTO;
import com.alma.alma_backend.entity.MensajeChat;
import com.alma.alma_backend.entity.SesionChat;

import java.util.List;

public interface ChatService {

    SesionChat iniciarSesionChat(SesionChatRequestDTO request);

    SesionChat obtenerSesionPorId(Integer id);

    SesionChat obtenerOCrearSesionActiva(Integer idPaciente, Integer idProfesional);

    List<SesionChat> obtenerSesionesPorPaciente(Integer idPaciente);

    List<SesionChat> obtenerSesionesPorProfesional(Integer idProfesional);

    List<SesionChat> obtenerSesionesActivas();

    SesionChat archivarSesion(Integer id);

    MensajeChat enviarMensaje(MensajeChatRequestDTO request, Integer idRemitente);

    List<MensajeChat> obtenerMensajesPorSesion(Integer idSesionChat);

    MensajeChat marcarMensajeComoLeido(Integer idMensaje);

    void marcarMensajesComoLeidos(Integer idSesionChat, Integer idUsuario);

    Long contarMensajesNoLeidos(Integer idSesionChat);
}