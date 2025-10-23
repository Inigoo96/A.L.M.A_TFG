package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ActualizarMetaRequestDTO;
import com.alma.alma_backend.dto.EstadisticasMetasDTO;
import com.alma.alma_backend.dto.MetaDiariaRequestDTO;
import com.alma.alma_backend.dto.MetaDiariaResponseDTO;
import com.alma.alma_backend.entity.EstadoMeta;

import java.time.LocalDate;
import java.util.List;

public interface MetaDiariaService {

    /**
     * Crea una nueva meta diaria para un paciente
     */
    MetaDiariaResponseDTO crearMeta(MetaDiariaRequestDTO request);

    /**
     * Obtiene una meta por ID
     */
    MetaDiariaResponseDTO obtenerMetaPorId(Integer id);

    /**
     * Obtiene todas las metas de un paciente
     */
    List<MetaDiariaResponseDTO> obtenerMetasPorPaciente(Integer idPaciente);

    /**
     * Obtiene las metas de hoy para un paciente
     */
    List<MetaDiariaResponseDTO> obtenerMetasHoyPorPaciente(Integer idPaciente);

    /**
     * Obtiene las metas de un paciente en un rango de fechas
     */
    List<MetaDiariaResponseDTO> obtenerMetasEnRango(Integer idPaciente, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Obtiene las metas de un paciente filtradas por estado
     */
    List<MetaDiariaResponseDTO> obtenerMetasPorEstado(Integer idPaciente, EstadoMeta estado);

    /**
     * Actualiza el estado de una meta
     */
    MetaDiariaResponseDTO actualizarEstadoMeta(ActualizarMetaRequestDTO request);

    /**
     * Marca una meta como completada
     */
    MetaDiariaResponseDTO completarMeta(Integer idMeta, String notas);

    /**
     * Cancela una meta
     */
    MetaDiariaResponseDTO cancelarMeta(Integer idMeta, String notas);

    /**
     * Actualiza una meta existente
     */
    MetaDiariaResponseDTO actualizarMeta(Integer id, MetaDiariaRequestDTO request);

    /**
     * Elimina una meta
     */
    void eliminarMeta(Integer id);

    /**
     * Obtiene estadísticas de metas de un paciente
     */
    EstadisticasMetasDTO obtenerEstadisticasMetas(Integer idPaciente);
}