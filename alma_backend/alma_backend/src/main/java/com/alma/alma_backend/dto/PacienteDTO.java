package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Paciente;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para exponer la información de un Paciente a un Profesional.
 */
@Data
public class PacienteDTO {

    private Integer id;
    private String nombre;
    private String apellidos;
    private String email;
    private LocalDate fechaNacimiento;

    public static PacienteDTO fromPaciente(Paciente paciente) {
        PacienteDTO dto = new PacienteDTO();
        dto.setId(paciente.getIdPaciente());
        dto.setNombre(paciente.getUsuario().getNombre());
        dto.setApellidos(paciente.getUsuario().getApellidos());
        dto.setEmail(paciente.getUsuario().getEmail());
        dto.setFechaNacimiento(paciente.getFechaNacimiento());
        return dto;
    }
}
