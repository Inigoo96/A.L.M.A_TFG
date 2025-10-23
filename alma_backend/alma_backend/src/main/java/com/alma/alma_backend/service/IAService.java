package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.*;

import java.util.List;

public interface IAService {

    /**
     * Inicia una nueva sesión de conversación con la IA
     */
    SesionInteraccionResponseDTO iniciarSesion(IniciarSesionIARequestDTO request);

    /**
     * Envía un mensaje a la IA y obtiene la respuesta
     */
    MensajeIAResponseDTO enviarMensaje(EnviarMensajeIARequestDTO request);

    /**
     * Finaliza una sesión de conversación con la IA
     */
    SesionInteraccionResponseDTO finalizarSesion(FinalizarSesionIARequestDTO request);

    /**
     * Obtiene una sesión de interacción por ID
     */
    SesionInteraccionResponseDTO obtenerSesionPorId(Integer id);

    /**
     * Obtiene todas las sesiones de un paciente
     */
    List<SesionInteraccionResponseDTO> obtenerSesionesPorPaciente(Integer idPaciente);

    /**
     * Obtiene todos los mensajes de una sesión
     */
    List<MensajeIAResponseDTO> obtenerMensajesPorSesion(Integer idSesion);

    /**
     * Obtiene las sesiones con alertas generadas para un profesional
     */
    List<SesionInteraccionResponseDTO> obtenerSesionesConAlertasPorProfesional(Integer idProfesional);

    /**
     * Agrega notas del profesional a una sesión
     */
    SesionInteraccionResponseDTO agregarNotasProfesional(Integer idSesion, String notas);
}