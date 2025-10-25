package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.AsignacionResponseDTO;
import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;

/**
 * Mapper for {@link AsignacionProfesionalPaciente} entities.
 */
public final class AsignacionMapper {

    private AsignacionMapper() {
    }

    public static AsignacionResponseDTO toResponse(AsignacionProfesionalPaciente asignacion) {
        if (asignacion == null) {
            return null;
        }

        AsignacionResponseDTO dto = new AsignacionResponseDTO();
        dto.setId(asignacion.getIdAsignacion());
        dto.setProfesional(ProfesionalMapper.toResponse(asignacion.getProfesional()));
        dto.setPaciente(PacienteMapper.toResponse(asignacion.getPaciente()));
        dto.setFechaAsignacion(asignacion.getFechaAsignacion());
        dto.setActiva(asignacion.getActivo());
        dto.setEsPrincipal(asignacion.getEsPrincipal());
        return dto;
    }
}
