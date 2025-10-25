package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ProgresoDueloRequestDTO;
import com.alma.alma_backend.entity.FaseDuelo;
import com.alma.alma_backend.entity.ProgresoDuelo;

import java.time.LocalDateTime;
import java.util.List;

public interface ProgresoDueloService {

    ProgresoDuelo registrarProgreso(ProgresoDueloRequestDTO request);

    ProgresoDuelo obtenerProgresoPorId(Integer id);

    List<ProgresoDuelo> obtenerProgresosPorPaciente(Integer idPaciente);

    List<ProgresoDuelo> obtenerProgresosPorProfesional(Integer idProfesional);

    List<ProgresoDuelo> obtenerProgresosPorPacienteYRangoFecha(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    ProgresoDuelo actualizarProgreso(Integer id, ProgresoDueloRequestDTO request);

    void eliminarProgreso(Integer id);

    List<FaseDuelo> obtenerTodasLasFases();
}
