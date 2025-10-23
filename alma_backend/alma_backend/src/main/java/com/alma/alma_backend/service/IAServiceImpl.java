package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.*;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.MensajeIARepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.repository.SesionInteraccionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class IAServiceImpl implements IAService {

    @Autowired
    private SesionInteraccionRepository sesionRepository;

    @Autowired
    private MensajeIARepository mensajeRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    // TODO: Inyectar cliente de API de IA (OpenAI, Anthropic, etc.) cuando se configure
    // @Autowired
    // private IAClientService iaClientService;

    @Override
    @Transactional
    public SesionInteraccionResponseDTO iniciarSesion(IniciarSesionIARequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + request.getIdPaciente()));

        SesionInteraccion sesion = new SesionInteraccion();
        sesion.setPaciente(paciente);
        sesion.setTipoSesion(request.getTipoSesion() != null ? request.getTipoSesion() : TipoSesion.CONVERSACION);
        sesion.setEstado(EstadoSesion.ACTIVA);

        // Si se proporciona un profesional, asignarlo
        if (request.getIdProfesional() != null) {
            Profesional profesional = profesionalRepository.findById(request.getIdProfesional())
                    .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + request.getIdProfesional()));
            sesion.setProfesional(profesional);
        }

        SesionInteraccion sesionGuardada = sesionRepository.save(sesion);
        return convertirSesionADTO(sesionGuardada);
    }

    @Override
    @Transactional
    public MensajeIAResponseDTO enviarMensaje(EnviarMensajeIARequestDTO request) {
        SesionInteraccion sesion = sesionRepository.findById(request.getIdSesion())
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada con id: " + request.getIdSesion()));

        // Validar que la sesión esté activa
        if (sesion.getEstado() != EstadoSesion.ACTIVA) {
            throw new IllegalStateException("La sesión no está activa");
        }

        // Validar límite de mensajes por sesión (opcional)
        Long contadorMensajes = mensajeRepository.countBySesionId(request.getIdSesion());
        if (contadorMensajes >= 100) {
            throw new IllegalStateException("Se ha alcanzado el límite de 100 mensajes por sesión");
        }

        // Guardar mensaje del usuario
        MensajeIA mensajeUsuario = new MensajeIA();
        mensajeUsuario.setSesion(sesion);
        mensajeUsuario.setRol(RolMensajeIA.USUARIO);
        mensajeUsuario.setMensaje(request.getMensaje());
        mensajeRepository.save(mensajeUsuario);

        // TODO: Llamar a la API de IA para obtener respuesta
        // String respuestaIA = iaClientService.obtenerRespuesta(request.getMensaje(), obtenerContextoSesion(sesion));
        // SentimientoDetectado sentimiento = iaClientService.analizarSentimiento(request.getMensaje());

        // TEMPORAL: Respuesta simulada hasta que se configure la API de IA
        String respuestaIA = generarRespuestaSimulada(request.getMensaje());
        SentimientoDetectado sentimiento = analizarSentimientoBasico(request.getMensaje());

        // Guardar respuesta del asistente
        MensajeIA mensajeAsistente = new MensajeIA();
        mensajeAsistente.setSesion(sesion);
        mensajeAsistente.setRol(RolMensajeIA.ASISTENTE);
        mensajeAsistente.setMensaje(respuestaIA);
        mensajeAsistente.setSentimientoDetectado(sentimiento);
        MensajeIA mensajeGuardado = mensajeRepository.save(mensajeAsistente);

        // Actualizar contador de mensajes en la sesión
        sesion.setNumeroMensajes(sesion.getNumeroMensajes() + 2); // Usuario + Asistente
        sesionRepository.save(sesion);

        return convertirMensajeADTO(mensajeGuardado);
    }

    @Override
    @Transactional
    public SesionInteraccionResponseDTO finalizarSesion(FinalizarSesionIARequestDTO request) {
        SesionInteraccion sesion = sesionRepository.findById(request.getIdSesion())
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada con id: " + request.getIdSesion()));

        if (sesion.getEstado() != EstadoSesion.ACTIVA) {
            throw new IllegalStateException("La sesión ya ha sido finalizada");
        }

        sesion.setEstado(EstadoSesion.FINALIZADA);
        sesion.setFechaFin(LocalDateTime.now());

        // Calcular duración en segundos
        if (sesion.getFechaInicio() != null) {
            long duracion = java.time.Duration.between(sesion.getFechaInicio(), sesion.getFechaFin()).getSeconds();
            sesion.setDuracionSegundos((int) duracion);
        }

        if (request.getSatisfaccion() != null) {
            sesion.setSatisfaccion(request.getSatisfaccion());
        }

        if (request.getEstadoEmocionalDetectado() != null) {
            sesion.setEstadoEmocionalDetectado(request.getEstadoEmocionalDetectado());
        }

        if (request.getNotasProfesional() != null) {
            sesion.setNotasProfesional(request.getNotasProfesional());
        }

        // TODO: Analizar conversación completa con IA para extraer temas y alertas
        // List<String> temas = iaClientService.extraerTemas(obtenerMensajesSesion(sesion.getId()));
        // List<String> alertas = iaClientService.detectarAlertas(obtenerMensajesSesion(sesion.getId()));
        // sesion.setTemasConversados(temas.toArray(new String[0]));
        // sesion.setAlertasGeneradas(alertas.toArray(new String[0]));

        SesionInteraccion sesionGuardada = sesionRepository.save(sesion);
        return convertirSesionADTO(sesionGuardada);
    }

    @Override
    public SesionInteraccionResponseDTO obtenerSesionPorId(Integer id) {
        SesionInteraccion sesion = sesionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada con id: " + id));
        return convertirSesionADTO(sesion);
    }

    @Override
    public List<SesionInteraccionResponseDTO> obtenerSesionesPorPaciente(Integer idPaciente) {
        List<SesionInteraccion> sesiones = sesionRepository.findByPacienteIdOrderByFechaInicioDesc(idPaciente);
        return sesiones.stream().map(this::convertirSesionADTO).collect(Collectors.toList());
    }

    @Override
    public List<MensajeIAResponseDTO> obtenerMensajesPorSesion(Integer idSesion) {
        List<MensajeIA> mensajes = mensajeRepository.findBySesionIdOrderByTimestampMensajeAsc(idSesion);
        return mensajes.stream().map(this::convertirMensajeADTO).collect(Collectors.toList());
    }

    @Override
    public List<SesionInteraccionResponseDTO> obtenerSesionesConAlertasPorProfesional(Integer idProfesional) {
        List<SesionInteraccion> sesiones = sesionRepository.findSesionesConAlertasPorProfesional(idProfesional);
        // Filtrar sesiones que realmente tienen alertas (array no vacío)
        return sesiones.stream()
                .filter(s -> s.getAlertasGeneradas() != null && s.getAlertasGeneradas().length > 0)
                .map(this::convertirSesionADTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public SesionInteraccionResponseDTO agregarNotasProfesional(Integer idSesion, String notas) {
        SesionInteraccion sesion = sesionRepository.findById(idSesion)
                .orElseThrow(() -> new ResourceNotFoundException("Sesión no encontrada con id: " + idSesion));

        sesion.setNotasProfesional(notas);
        SesionInteraccion sesionGuardada = sesionRepository.save(sesion);
        return convertirSesionADTO(sesionGuardada);
    }

    // Métodos auxiliares privados

    private SesionInteraccionResponseDTO convertirSesionADTO(SesionInteraccion sesion) {
        SesionInteraccionResponseDTO dto = new SesionInteraccionResponseDTO();
        dto.setId(sesion.getId());
        dto.setIdPaciente(sesion.getPaciente().getId());
        dto.setNombrePaciente(sesion.getPaciente().getUsuario().getNombre() + " " +
                             sesion.getPaciente().getUsuario().getApellidos());

        if (sesion.getProfesional() != null) {
            dto.setIdProfesional(sesion.getProfesional().getId());
            dto.setNombreProfesional(sesion.getProfesional().getUsuario().getNombre() + " " +
                                   sesion.getProfesional().getUsuario().getApellidos());
        }

        dto.setFechaInicio(sesion.getFechaInicio());
        dto.setFechaFin(sesion.getFechaFin());
        dto.setDuracionSegundos(sesion.getDuracionSegundos());
        dto.setTipoSesion(sesion.getTipoSesion());
        dto.setEstado(sesion.getEstado());
        dto.setNumeroMensajes(sesion.getNumeroMensajes());
        dto.setSatisfaccion(sesion.getSatisfaccion());
        dto.setNotasProfesional(sesion.getNotasProfesional());
        dto.setEstadoEmocionalDetectado(sesion.getEstadoEmocionalDetectado());

        if (sesion.getTemasConversados() != null) {
            dto.setTemasConversados(Arrays.asList(sesion.getTemasConversados()));
        }

        if (sesion.getAlertasGeneradas() != null) {
            dto.setAlertasGeneradas(Arrays.asList(sesion.getAlertasGeneradas()));
        }

        return dto;
    }

    private MensajeIAResponseDTO convertirMensajeADTO(MensajeIA mensaje) {
        MensajeIAResponseDTO dto = new MensajeIAResponseDTO();
        dto.setId(mensaje.getId());
        dto.setIdSesion(mensaje.getSesion().getId());
        dto.setRol(mensaje.getRol());
        dto.setMensaje(mensaje.getMensaje());
        dto.setTimestampMensaje(mensaje.getTimestampMensaje());
        dto.setSentimientoDetectado(mensaje.getSentimientoDetectado());
        return dto;
    }

    // TEMPORAL: Métodos simulados hasta configurar API de IA real

    private String generarRespuestaSimulada(String mensajeUsuario) {
        // Respuesta temporal simulada
        return "Gracias por compartir eso conmigo. Entiendo que estás pasando por un momento difícil. " +
               "¿Podrías contarme más sobre cómo te has sentido últimamente?";
    }

    private SentimientoDetectado analizarSentimientoBasico(String mensaje) {
        String mensajeLower = mensaje.toLowerCase();

        // Análisis básico temporal
        if (mensajeLower.contains("feliz") || mensajeLower.contains("bien") || mensajeLower.contains("mejor")) {
            return SentimientoDetectado.POSITIVO;
        } else if (mensajeLower.contains("triste") || mensajeLower.contains("mal") ||
                   mensajeLower.contains("dolor") || mensajeLower.contains("sufr")) {
            return SentimientoDetectado.NEGATIVO;
        } else if (mensajeLower.contains("muy mal") || mensajeLower.contains("horrible") ||
                   mensajeLower.contains("terrible")) {
            return SentimientoDetectado.MUY_NEGATIVO;
        }

        return SentimientoDetectado.NEUTRO;
    }
}