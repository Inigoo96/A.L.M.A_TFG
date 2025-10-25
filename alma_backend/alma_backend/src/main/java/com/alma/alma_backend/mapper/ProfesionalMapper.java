package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.ProfesionalResponseDTO;
import com.alma.alma_backend.entity.Profesional;

/**
 * Mapper for {@link Profesional} entities.
 */
public final class ProfesionalMapper {

    private ProfesionalMapper() {
    }

    public static ProfesionalResponseDTO toResponse(Profesional profesional) {
        if (profesional == null) {
            return null;
        }

        ProfesionalResponseDTO dto = new ProfesionalResponseDTO();
        dto.setId(profesional.getId());
        dto.setNumeroColegiado(profesional.getNumeroColegiado());
        dto.setEspecialidad(profesional.getEspecialidad());
        dto.setCentroSalud(profesional.getCentroSalud());
        dto.setUsuario(UsuarioMapper.toResponse(profesional.getUsuario()));
        return dto;
    }
}
