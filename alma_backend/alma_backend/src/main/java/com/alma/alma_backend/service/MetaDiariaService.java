package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ActualizarMetaRequestDTO;
import com.alma.alma_backend.dto.EstadisticasMetasDTO;
import com.alma.alma_backend.dto.MetaDiariaRequestDTO;
import com.alma.alma_backend.entity.EstadoMeta;
import com.alma.alma_backend.entity.MetaDiaria;

import java.time.LocalDate;
import java.util.List;

public interface MetaDiariaService {

    /**
     * Crea una nueva meta diaria para un paciente
     */
    MetaDiaria crearMeta(MetaDiariaRequestDTO request);

    /**
     * Obtiene una meta por ID
     */
    MetaDiaria obtenerMetaPorId(Integer id);

    /**
     * Obtiene todas las metas de un paciente
     */
    List<MetaDiaria> obtenerMetasPorPaciente(Integer idPaciente);

    /**
     * Obtiene las metas de hoy para un paciente
     */
    List<MetaDiaria> obtenerMetasHoyPorPaciente(Integer idPaciente);

    /**
     * Obtiene las metas de un paciente en un rango de fechas
     */
    List<MetaDiaria> obtenerMetasEnRango(Integer idPaciente, LocalDate fechaInicio, LocalDate fechaFin);

    /**
     * Obtiene las metas de un paciente filtradas por estado
     */
    List<MetaDiaria> obtenerMetasPorEstado(Integer idPaciente, EstadoMeta estado);

    /**
     * Actualiza el estado de una meta
     */
    MetaDiaria actualizarEstadoMeta(ActualizarMetaRequestDTO request);

    /**
     * Marca una meta como completada
     */
    MetaDiaria completarMeta(Integer idMeta, String notas);

    /**
     * Cancela una meta
     */
    MetaDiaria cancelarMeta(Integer idMeta, String notas);

    /**
     * Actualiza una meta existente
     */
    MetaDiaria actualizarMeta(Integer id, MetaDiariaRequestDTO request);

    /**
     * Elimina una meta
     */
    void eliminarMeta(Integer id);

    /**
     * Obtiene estadísticas de metas de un paciente
     */
    EstadisticasMetasDTO obtenerEstadisticasMetas(Integer idPaciente);
}