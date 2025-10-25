package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.MensajeChatResponseDTO;
import com.alma.alma_backend.dto.SesionChatResponseDTO;
import com.alma.alma_backend.entity.MensajeChat;
import com.alma.alma_backend.entity.SesionChat;

/**
 * Mapper for chat entities. The repository dependency is required to enrich the session response
 * with unread message counts.
 */
public final class ChatMapper {

    private ChatMapper() {
    }

    public static SesionChatResponseDTO toResponse(SesionChat sesion, long unreadCount) {
        if (sesion == null) {
            return null;
        }

        SesionChatResponseDTO dto = new SesionChatResponseDTO();
        dto.setId(sesion.getId());
        if (sesion.getPaciente() != null) {
            dto.setIdPaciente(sesion.getPaciente().getId());
            if (sesion.getPaciente().getUsuario() != null) {
                dto.setNombrePaciente(sesion.getPaciente().getUsuario().getNombre() + " " +
                        sesion.getPaciente().getUsuario().getApellidos());
            }
        }
        if (sesion.getProfesional() != null) {
            dto.setIdProfesional(sesion.getProfesional().getId());
            if (sesion.getProfesional().getUsuario() != null) {
                dto.setNombreProfesional(sesion.getProfesional().getUsuario().getNombre() + " " +
                        sesion.getProfesional().getUsuario().getApellidos());
            }
        }
        dto.setFechaCreacion(sesion.getFechaCreacion());
        dto.setUltimaActividad(sesion.getUltimaActividad());
        dto.setEstado(sesion.getEstado());
        dto.setMensajesNoLeidos(unreadCount);
        return dto;
    }

    public static MensajeChatResponseDTO toResponse(MensajeChat mensaje) {
        if (mensaje == null) {
            return null;
        }

        MensajeChatResponseDTO dto = new MensajeChatResponseDTO();
        dto.setId(mensaje.getId());
        dto.setIdSesionChat(mensaje.getSesionChat() != null ? mensaje.getSesionChat().getId() : null);
        if (mensaje.getRemitente() != null) {
            dto.setIdRemitente(mensaje.getRemitente().getId());
            dto.setNombreRemitente(mensaje.getRemitente().getNombre() + " " + mensaje.getRemitente().getApellidos());
            dto.setTipoRemitente(mensaje.getRemitente().getTipoUsuario().name());
        }
        dto.setMensaje(mensaje.getMensaje());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());
        dto.setFechaLectura(mensaje.getFechaLectura());
        return dto;
    }
}
