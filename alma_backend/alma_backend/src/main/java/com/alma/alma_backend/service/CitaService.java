package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ActualizarEstadoCitaDTO;
import com.alma.alma_backend.dto.CitaRequestDTO;
import com.alma.alma_backend.dto.CitaResponseDTO;
import com.alma.alma_backend.entity.EstadoCita;

import java.time.LocalDateTime;
import java.util.List;

public interface CitaService {

    CitaResponseDTO crearCita(CitaRequestDTO request);

    CitaResponseDTO obtenerCitaPorId(Integer id);

    List<CitaResponseDTO> obtenerCitasPorPaciente(Integer idPaciente);

    List<CitaResponseDTO> obtenerCitasPorProfesional(Integer idProfesional);

    List<CitaResponseDTO> obtenerCitasProximasPorPaciente(Integer idPaciente);

    List<CitaResponseDTO> obtenerCitasProximasPorProfesional(Integer idProfesional);

    List<CitaResponseDTO> obtenerCitasPorEstado(EstadoCita estado);

    CitaResponseDTO actualizarEstadoCita(Integer id, ActualizarEstadoCitaDTO request);

    CitaResponseDTO actualizarCita(Integer id, CitaRequestDTO request);

    void cancelarCita(Integer id);

    void eliminarCita(Integer id);
}