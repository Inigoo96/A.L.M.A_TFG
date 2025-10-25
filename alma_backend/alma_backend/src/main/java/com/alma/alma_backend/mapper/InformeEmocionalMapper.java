package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.InformeEmocionalResponseDTO;
import com.alma.alma_backend.entity.InformeEmocional;

/**
 * Mapper for {@link InformeEmocional} entities.
 */
public final class InformeEmocionalMapper {

    private InformeEmocionalMapper() {
    }

    public static InformeEmocionalResponseDTO toResponse(InformeEmocional informe) {
        if (informe == null) {
            return null;
        }

        InformeEmocionalResponseDTO dto = new InformeEmocionalResponseDTO();
        dto.setId(informe.getId());
        if (informe.getPaciente() != null) {
            dto.setPacienteId(informe.getPaciente().getId());
            if (informe.getPaciente().getUsuario() != null) {
                dto.setNombrePaciente(informe.getPaciente().getUsuario().getNombre());
            }
        }
        if (informe.getProfesional() != null) {
            dto.setProfesionalId(informe.getProfesional().getId());
            if (informe.getProfesional().getUsuario() != null) {
                dto.setNombreProfesional(informe.getProfesional().getUsuario().getNombre());
            }
        }
        dto.setPeriodoInicio(informe.getPeriodoInicio());
        dto.setPeriodoFin(informe.getPeriodoFin());
        dto.setContenidoInforme(informe.getContenidoInforme());
        dto.setGeneradoPor(informe.getGeneradoPor());
        dto.setFechaGeneracion(informe.getFechaGeneracion());
        return dto;
    }
}
