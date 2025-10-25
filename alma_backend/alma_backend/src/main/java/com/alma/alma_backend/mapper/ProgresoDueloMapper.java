package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.FaseDueloDTO;
import com.alma.alma_backend.dto.ProgresoDueloResponseDTO;
import com.alma.alma_backend.entity.FaseDuelo;
import com.alma.alma_backend.entity.ProgresoDuelo;

/**
 * Mapper utilities for grief progress tracking entities.
 */
public final class ProgresoDueloMapper {

    private ProgresoDueloMapper() {
    }

    public static ProgresoDueloResponseDTO toResponse(ProgresoDuelo progreso) {
        if (progreso == null) {
            return null;
        }

        ProgresoDueloResponseDTO dto = new ProgresoDueloResponseDTO();
        dto.setId(progreso.getId());
        dto.setIdPaciente(progreso.getPaciente() != null ? progreso.getPaciente().getId() : null);
        dto.setIdProfesional(progreso.getProfesional() != null ? progreso.getProfesional().getId() : null);
        if (progreso.getFaseDuelo() != null) {
            dto.setNombreFaseDuelo(progreso.getFaseDuelo().getNombre());
        }
        if (progreso.getEstadoEmocional() != null) {
            dto.setEstadoEmocional(progreso.getEstadoEmocional().name());
        }
        dto.setNotas(progreso.getNotas());
        dto.setFechaRegistro(progreso.getFechaRegistro());
        return dto;
    }

    public static FaseDueloDTO toResponse(FaseDuelo fase) {
        if (fase == null) {
            return null;
        }

        FaseDueloDTO dto = new FaseDueloDTO();
        dto.setId(fase.getId());
        dto.setNombre(fase.getNombre());
        dto.setDescripcion(fase.getDescripcion());
        dto.setOrden(fase.getOrdenFase());
        return dto;
    }
}
