package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ChatPacientesRequestDTO;
import com.alma.alma_backend.dto.ChatPacientesResponseDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesRequestDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesResponseDTO;

import java.util.List;

public interface ChatPacientesService {

    ChatPacientesResponseDTO findOrCreateChat(ChatPacientesRequestDTO request);

    List<MensajeChatPacientesResponseDTO> findMensajesByChatId(Integer chatId);

    MensajeChatPacientesResponseDTO postMensaje(MensajeChatPacientesRequestDTO request);

}
