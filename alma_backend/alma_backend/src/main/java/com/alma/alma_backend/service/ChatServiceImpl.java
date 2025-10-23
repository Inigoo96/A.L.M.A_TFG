package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.MensajeChatRequestDTO;
import com.alma.alma_backend.dto.MensajeChatResponseDTO;
import com.alma.alma_backend.dto.SesionChatRequestDTO;
import com.alma.alma_backend.dto.SesionChatResponseDTO;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChatServiceImpl implements ChatService {

    @Autowired
    private SesionChatRepository sesionChatRepository;

    @Autowired
    private MensajeChatRepository mensajeChatRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    @Transactional
    public SesionChatResponseDTO iniciarSesionChat(SesionChatRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + request.getIdPaciente()));

        Profesional profesional = profesionalRepository.findById(request.getIdProfesional())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + request.getIdProfesional()));

        // Verificar si ya existe una sesión activa
        Optional<SesionChat> sesionExistente = sesionChatRepository.findByPacienteAndProfesionalAndEstado(
                request.getIdPaciente(),
                request.getIdProfesional(),
                EstadoSesionChat.ACTIVA
        );

        if (sesionExistente.isPresent()) {
            return convertirSesionADTO(sesionExistente.get());
        }

        // Crear nueva sesión
        SesionChat sesion = new SesionChat();
        sesion.setPaciente(paciente);
        sesion.setProfesional(profesional);
        sesion.setEstado(EstadoSesionChat.ACTIVA);

        SesionChat sesionGuardada = sesionChatRepository.save(sesion);
        return convertirSesionADTO(sesionGuardada);
    }

    @Override
    public SesionChatResponseDTO obtenerSesionPorId(Integer id) {
        SesionChat sesion = sesionChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de chat no encontrada con id: " + id));
        return convertirSesionADTO(sesion);
    }

    @Override
    @Transactional
    public SesionChatResponseDTO obtenerOCrearSesionActiva(Integer idPaciente, Integer idProfesional) {
        Optional<SesionChat> sesionExistente = sesionChatRepository.findByPacienteAndProfesionalAndEstado(
                idPaciente,
                idProfesional,
                EstadoSesionChat.ACTIVA
        );

        if (sesionExistente.isPresent()) {
            return convertirSesionADTO(sesionExistente.get());
        }

        // Crear nueva sesión usando el método iniciarSesionChat
        SesionChatRequestDTO request = new SesionChatRequestDTO();
        request.setIdPaciente(idPaciente);
        request.setIdProfesional(idProfesional);
        return iniciarSesionChat(request);
    }

    @Override
    public List<SesionChatResponseDTO> obtenerSesionesPorPaciente(Integer idPaciente) {
        List<SesionChat> sesiones = sesionChatRepository.findByPaciente_IdOrderByUltimaActividadDesc(idPaciente);
        return sesiones.stream().map(this::convertirSesionADTO).collect(Collectors.toList());
    }

    @Override
    public List<SesionChatResponseDTO> obtenerSesionesPorProfesional(Integer idProfesional) {
        List<SesionChat> sesiones = sesionChatRepository.findByProfesional_IdOrderByUltimaActividadDesc(idProfesional);
        return sesiones.stream().map(this::convertirSesionADTO).collect(Collectors.toList());
    }

    @Override
    public List<SesionChatResponseDTO> obtenerSesionesActivas() {
        List<SesionChat> sesiones = sesionChatRepository.findByEstadoOrderByUltimaActividadDesc(EstadoSesionChat.ACTIVA);
        return sesiones.stream().map(this::convertirSesionADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SesionChatResponseDTO archivarSesion(Integer id) {
        SesionChat sesion = sesionChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de chat no encontrada con id: " + id));

        sesion.setEstado(EstadoSesionChat.ARCHIVADA);
        SesionChat sesionActualizada = sesionChatRepository.save(sesion);
        return convertirSesionADTO(sesionActualizada);
    }

    @Override
    @Transactional
    public MensajeChatResponseDTO enviarMensaje(MensajeChatRequestDTO request, Integer idRemitente) {
        SesionChat sesion = sesionChatRepository.findById(request.getIdSesionChat())
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de chat no encontrada con id: " + request.getIdSesionChat()));

        Usuario remitente = usuarioRepository.findById(idRemitente)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario remitente no encontrado con id: " + idRemitente));

        MensajeChat mensaje = new MensajeChat();
        mensaje.setSesionChat(sesion);
        mensaje.setRemitente(remitente);
        mensaje.setMensaje(request.getMensaje());
        mensaje.setLeido(false);

        MensajeChat mensajeGuardado = mensajeChatRepository.save(mensaje);

        // Actualizar última actividad de la sesión
        sesion.setUltimaActividad(LocalDateTime.now());
        sesionChatRepository.save(sesion);

        return convertirMensajeADTO(mensajeGuardado);
    }

    @Override
    public List<MensajeChatResponseDTO> obtenerMensajesPorSesion(Integer idSesionChat) {
        List<MensajeChat> mensajes = mensajeChatRepository.findBySesionChat_IdOrderByFechaEnvioAsc(idSesionChat);
        return mensajes.stream().map(this::convertirMensajeADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MensajeChatResponseDTO marcarMensajeComoLeido(Integer idMensaje) {
        MensajeChat mensaje = mensajeChatRepository.findById(idMensaje)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado con id: " + idMensaje));

        mensaje.setLeido(true);
        mensaje.setFechaLectura(LocalDateTime.now());

        MensajeChat mensajeActualizado = mensajeChatRepository.save(mensaje);
        return convertirMensajeADTO(mensajeActualizado);
    }

    @Override
    @Transactional
    public void marcarMensajesComoLeidos(Integer idSesionChat, Integer idUsuario) {
        List<MensajeChat> mensajesNoLeidos = mensajeChatRepository.findMensajesNoLeidosPorDestinatario(idSesionChat, idUsuario);

        for (MensajeChat mensaje : mensajesNoLeidos) {
            mensaje.setLeido(true);
            mensaje.setFechaLectura(LocalDateTime.now());
            mensajeChatRepository.save(mensaje);
        }
    }

    @Override
    public Long contarMensajesNoLeidos(Integer idSesionChat) {
        return mensajeChatRepository.countMensajesNoLeidos(idSesionChat);
    }

    private SesionChatResponseDTO convertirSesionADTO(SesionChat sesion) {
        SesionChatResponseDTO dto = new SesionChatResponseDTO();
        dto.setId(sesion.getId());
        dto.setIdPaciente(sesion.getPaciente().getId());
        dto.setNombrePaciente(sesion.getPaciente().getUsuario().getNombre() + " " +
                sesion.getPaciente().getUsuario().getApellidos());
        dto.setIdProfesional(sesion.getProfesional().getId());
        dto.setNombreProfesional(sesion.getProfesional().getUsuario().getNombre() + " " +
                sesion.getProfesional().getUsuario().getApellidos());
        dto.setFechaCreacion(sesion.getFechaCreacion());
        dto.setUltimaActividad(sesion.getUltimaActividad());
        dto.setEstado(sesion.getEstado());
        dto.setMensajesNoLeidos(mensajeChatRepository.countMensajesNoLeidos(sesion.getId()));

        return dto;
    }

    private MensajeChatResponseDTO convertirMensajeADTO(MensajeChat mensaje) {
        MensajeChatResponseDTO dto = new MensajeChatResponseDTO();
        dto.setId(mensaje.getId());
        dto.setIdSesionChat(mensaje.getSesionChat().getId());
        dto.setIdRemitente(mensaje.getRemitente().getId());
        dto.setNombreRemitente(mensaje.getRemitente().getNombre() + " " +
                mensaje.getRemitente().getApellidos());
        dto.setTipoRemitente(mensaje.getRemitente().getTipoUsuario().name());
        dto.setMensaje(mensaje.getMensaje());
        dto.setFechaEnvio(mensaje.getFechaEnvio());
        dto.setLeido(mensaje.getLeido());
        dto.setFechaLectura(mensaje.getFechaLectura());

        return dto;
    }
}