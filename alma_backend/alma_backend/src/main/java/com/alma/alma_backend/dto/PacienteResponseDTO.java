package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.mapper.UsuarioMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO de respuesta seguro para Paciente.
 * No expone información sensible como el passwordHash del usuario.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PacienteResponseDTO {

    private Integer id;
    private UsuarioResponseDTO usuario;
    private String tarjetaSanitaria;
    private LocalDate fechaNacimiento;
    private Genero genero;

    /**
     * Método estático para convertir una entidad Paciente a PacienteResponseDTO.
     *
     * @param paciente La entidad Paciente a convertir
     * @return PacienteResponseDTO con datos seguros (sin passwordHash)
     */
    public static PacienteResponseDTO fromPaciente(Paciente paciente) {
        if (paciente == null) {
            return null;
        }

        PacienteResponseDTO dto = new PacienteResponseDTO();
        dto.setId(paciente.getId());
        dto.setTarjetaSanitaria(paciente.getTarjetaSanitaria());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        dto.setGenero(paciente.getGenero());

        // Convertir Usuario a UsuarioResponseDTO (que no contiene passwordHash)
        dto.setUsuario(UsuarioMapper.toResponse(paciente.getUsuario()));

        return dto;
    }
}