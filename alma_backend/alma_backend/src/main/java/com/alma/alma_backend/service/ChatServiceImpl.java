package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.MensajeChatRequestDTO;
import com.alma.alma_backend.dto.SesionChatRequestDTO;
import com.alma.alma_backend.entity.EstadoSesionChat;
import com.alma.alma_backend.entity.MensajeChat;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.SesionChat;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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
    public SesionChat iniciarSesionChat(SesionChatRequestDTO request) {
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
            return sesionExistente.get();
        }

        // Crear nueva sesión
        SesionChat sesion = new SesionChat();
        sesion.setPaciente(paciente);
        sesion.setProfesional(profesional);
        sesion.setEstado(EstadoSesionChat.ACTIVA);

        return sesionChatRepository.save(sesion);
    }

    @Override
    public SesionChat obtenerSesionPorId(Integer id) {
        SesionChat sesion = sesionChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de chat no encontrada con id: " + id));
        return sesion;
    }

    @Override
    @Transactional
    public SesionChat obtenerOCrearSesionActiva(Integer idPaciente, Integer idProfesional) {
        Optional<SesionChat> sesionExistente = sesionChatRepository.findByPacienteAndProfesionalAndEstado(
                idPaciente,
                idProfesional,
                EstadoSesionChat.ACTIVA
        );

        if (sesionExistente.isPresent()) {
            return sesionExistente.get();
        }

        // Crear nueva sesión usando el método iniciarSesionChat
        SesionChatRequestDTO request = new SesionChatRequestDTO();
        request.setIdPaciente(idPaciente);
        request.setIdProfesional(idProfesional);
        return iniciarSesionChat(request);
    }

    @Override
    public List<SesionChat> obtenerSesionesPorPaciente(Integer idPaciente) {
        return sesionChatRepository.findByPaciente_IdOrderByUltimaActividadDesc(idPaciente);
    }

    @Override
    public List<SesionChat> obtenerSesionesPorProfesional(Integer idProfesional) {
        return sesionChatRepository.findByProfesional_IdOrderByUltimaActividadDesc(idProfesional);
    }

    @Override
    public List<SesionChat> obtenerSesionesActivas() {
        return sesionChatRepository.findByEstadoOrderByUltimaActividadDesc(EstadoSesionChat.ACTIVA);
    }

    @Override
    @Transactional
    public SesionChat archivarSesion(Integer id) {
        SesionChat sesion = sesionChatRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión de chat no encontrada con id: " + id));

        sesion.setEstado(EstadoSesionChat.ARCHIVADA);
        return sesionChatRepository.save(sesion);
    }

    @Override
    @Transactional
    public MensajeChat enviarMensaje(MensajeChatRequestDTO request, Integer idRemitente) {
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

        return mensajeGuardado;
    }

    @Override
    public List<MensajeChat> obtenerMensajesPorSesion(Integer idSesionChat) {
        return mensajeChatRepository.findBySesionChat_IdOrderByFechaEnvioAsc(idSesionChat);
    }

    @Override
    @Transactional
    public MensajeChat marcarMensajeComoLeido(Integer idMensaje) {
        MensajeChat mensaje = mensajeChatRepository.findById(idMensaje)
                .orElseThrow(() -> new ResourceNotFoundException("Mensaje no encontrado con id: " + idMensaje));

        mensaje.setLeido(true);
        mensaje.setFechaLectura(LocalDateTime.now());

        return mensajeChatRepository.save(mensaje);
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
}