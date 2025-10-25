package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.MetaDiariaResponseDTO;
import com.alma.alma_backend.entity.MetaDiaria;

/**
 * Mapper for {@link MetaDiaria} entities.
 */
public final class MetaDiariaMapper {

    private MetaDiariaMapper() {
    }

    public static MetaDiariaResponseDTO toResponse(MetaDiaria metaDiaria) {
        if (metaDiaria == null) {
            return null;
        }

        MetaDiariaResponseDTO dto = new MetaDiariaResponseDTO();
        dto.setId(metaDiaria.getId());
        if (metaDiaria.getPaciente() != null) {
            dto.setIdPaciente(metaDiaria.getPaciente().getId());
            if (metaDiaria.getPaciente().getUsuario() != null) {
                dto.setNombrePaciente(metaDiaria.getPaciente().getUsuario().getNombre() + " " +
                        metaDiaria.getPaciente().getUsuario().getApellidos());
            }
        }
        dto.setTextoMeta(metaDiaria.getTextoMeta());
        dto.setFechaAsignada(metaDiaria.getFechaAsignada());
        dto.setEstado(metaDiaria.getEstado());
        dto.setNotas(metaDiaria.getNotas());
        dto.setFechaCompletada(metaDiaria.getFechaCompletada());
        dto.setFechaCreacion(metaDiaria.getFechaCreacion());
        return dto;
    }
}
