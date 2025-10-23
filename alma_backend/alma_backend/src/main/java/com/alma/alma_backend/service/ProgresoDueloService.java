package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.FaseDueloDTO;
import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.dto.ProgresoDueloResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ProgresoDueloService {

    ProgresoDueloResponseDTO registrarProgreso(ProgresoDueloRequestDTO request);

    ProgresoDueloResponseDTO obtenerProgresoPorId(Integer id);

    List<ProgresoDueloResponseDTO> obtenerProgresosPorPaciente(Integer idPaciente);

    List<ProgresoDueloResponseDTO> obtenerProgresosPorProfesional(Integer idProfesional);

    List<ProgresoDueloResponseDTO> obtenerProgresosPorPacienteYRangoFecha(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    ProgresoDueloResponseDTO actualizarProgreso(Integer id, ProgresoDueloRequestDTO request);

    void eliminarProgreso(Integer id);

    List<FaseDueloDTO> obtenerTodasLasFases();
}
