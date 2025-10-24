package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ChatPacientesRequestDTO;
import com.alma.alma_backend.dto.ChatPacientesResponseDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesRequestDTO;
import com.alma.alma_backend.dto.MensajeChatPacientesResponseDTO;
import com.alma.alma_backend.entity.ChatPacientes;
import com.alma.alma_backend.entity.MensajeChatPacientes;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.ChatPacientesRepository;
import com.alma.alma_backend.repository.MensajeChatPacientesRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatPacientesServiceImpl implements ChatPacientesService {

    @Autowired
    private ChatPacientesRepository chatPacientesRepository;

    @Autowired
    private MensajeChatPacientesRepository mensajeChatPacientesRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public ChatPacientesResponseDTO findOrCreateChat(ChatPacientesRequestDTO request) {
        return chatPacientesRepository.findChatByPacientes(request.getPacienteId1(), request.getPacienteId2())
                .map(this::mapToChatPacientesResponseDTO)
                .orElseGet(() -> {
                    Paciente p1 = pacienteRepository.findById(request.getPacienteId1())
                            .orElseThrow(() -> new ResourceNotFoundException("Paciente 1 no encontrado"));
                    Paciente p2 = pacienteRepository.findById(request.getPacienteId2())
                            .orElseThrow(() -> new ResourceNotFoundException("Paciente 2 no encontrado"));
                    ChatPacientes newChat = new ChatPacientes();
                    newChat.setPaciente1(p1);
                    newChat.setPaciente2(p2);
                    return mapToChatPacientesResponseDTO(chatPacientesRepository.save(newChat));
                });
    }

    @Override
    public List<MensajeChatPacientesResponseDTO> findMensajesByChatId(Integer chatId) {
        return mensajeChatPacientesRepository.findByChatPacientesIdOrderByFechaEnvioAsc(chatId).stream()
                .map(this::mapToMensajeChatPacientesResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MensajeChatPacientesResponseDTO postMensaje(MensajeChatPacientesRequestDTO request) {
        ChatPacientes chat = chatPacientesRepository.findById(request.getChatPacientesId())
                .orElseThrow(() -> new ResourceNotFoundException("Chat no encontrado"));
        Paciente remitente = pacienteRepository.findById(request.getRemitenteId())
                .orElseThrow(() -> new ResourceNotFoundException("Remitente no encontrado"));

        MensajeChatPacientes mensaje = new MensajeChatPacientes();
        mensaje.setChatPacientes(chat);
        mensaje.setRemitente(remitente);
        mensaje.setMensaje(request.getMensaje());

        MensajeChatPacientes savedMensaje = mensajeChatPacientesRepository.save(mensaje);
        return mapToMensajeChatPacientesResponseDTO(savedMensaje);
    }

    private ChatPacientesResponseDTO mapToChatPacientesResponseDTO(ChatPacientes chat) {
        ChatPacientesResponseDTO dto = new ChatPacientesResponseDTO();
        dto.setId(chat.getId());
        dto.setPacienteId1(chat.getPaciente1().getId());
        dto.setNombrePaciente1(chat.getPaciente1().getUsuario().getNombre());
        dto.setPacienteId2(chat.getPaciente2().getId());
        dto.setNombrePaciente2(chat.getPaciente2().getUsuario().getNombre());
        dto.setFechaCreacion(chat.getFechaCreacion());
        dto.setUltimaActividad(chat.getUltimaActividad());
        dto.setEstado(chat.getEstado().name());
        return dto;
    }

    private MensajeChatPacientesResponseDTO mapToMensajeChatPacientesResponseDTO(MensajeChatPacientes mensaje) {
        MensajeChatPacientesResponseDTO dto = new MensajeChatPacientesResponseDTO();
        dto.setId(mensaje.getId());
        dto.setChatPacientesId(mensaje.getChatPacientes().getId());
        dto.setRemitenteId(mensaje.getRemitente().getId());
        dto.setNombreRemitente(mensaje.getRemitente().getUsuario().getNombre());
        dto.setMensaje(mensaje.getMensaje());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());
        return dto;
    }
}
