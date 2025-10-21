package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import com.alma.alma_backend.entity.Paciente;
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
        if (paciente.getUsuario() != null) {
            UsuarioResponseDTO usuarioDTO = new UsuarioResponseDTO();
            usuarioDTO.setId(paciente.getUsuario().getId());
            usuarioDTO.setDni(paciente.getUsuario().getDni());
            usuarioDTO.setEmail(paciente.getUsuario().getEmail());
            usuarioDTO.setNombre(paciente.getUsuario().getNombre());
            usuarioDTO.setApellidos(paciente.getUsuario().getApellidos());
            usuarioDTO.setTelefono(paciente.getUsuario().getTelefono());
            usuarioDTO.setTipoUsuario(paciente.getUsuario().getTipoUsuario());
            usuarioDTO.setOrganizacion(paciente.getUsuario().getOrganizacion());
            usuarioDTO.setActivo(paciente.getUsuario().getActivo());
            usuarioDTO.setFechaRegistro(paciente.getUsuario().getFechaRegistro());
            usuarioDTO.setUltimoAcceso(paciente.getUsuario().getUltimoAcceso());
            usuarioDTO.setPasswordTemporal(paciente.getUsuario().getPasswordTemporal());
            usuarioDTO.setCargo(paciente.getUsuario().getCargo());
            usuarioDTO.setDocumentoCargoUrl(paciente.getUsuario().getDocumentoCargoUrl());

            dto.setUsuario(usuarioDTO);
        }

        return dto;
    }
}