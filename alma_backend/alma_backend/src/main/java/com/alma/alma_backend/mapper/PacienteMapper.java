package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.PacienteDTO;
import com.alma.alma_backend.dto.PacienteResponseDTO;
import com.alma.alma_backend.dto.PacienteUpdateRequestDTO;
import com.alma.alma_backend.entity.Paciente;

/**
 * Utility mapper for converting {@link Paciente} entities into their DTO representations.
 * Centralising this logic avoids leaking entity details to the controller layer and keeps
 * the mapping consistent across the application.
 */
public final class PacienteMapper {

    private PacienteMapper() {
        // Utility class
    }

    public static PacienteResponseDTO toResponse(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setTarjetaSanitaria(paciente.getTarjetaSanitaria());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setGenero(paciente.getGenero());
        dto.setUsuario(UsuarioMapper.toResponse(paciente.getUsuario()));
        return dto;
    }

    public static PacienteDTO toPacienteDTO(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteDTO dto = new PacienteDTO();
        dto.setId(paciente.getId());
        if (paciente.getUsuario() != null) {
            dto.setNombre(paciente.getUsuario().getNombre());
            dto.setApellidos(paciente.getUsuario().getApellidos());
            dto.setEmail(paciente.getUsuario().getEmail());
        }
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        return dto;
    }

    public static void applyUpdates(Paciente paciente, PacienteUpdateRequestDTO update) {
        if (paciente == null || update == null) {
            return;
        }

        if (update.getFechaNacimiento() != null) {
            paciente.setFechaNacimiento(update.getFechaNacimiento());
        }
        if (update.getGenero() != null) {
            paciente.setGenero(update.getGenero());
        }
        if (update.getTarjetaSanitaria() != null && !update.getTarjetaSanitaria().isEmpty()) {
            paciente.setTarjetaSanitaria(update.getTarjetaSanitaria());
        }
    }
}
