package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoCita;
import com.alma.alma_backend.entity.TipoCita;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CitaResponseDTO {
    private Integer id;
    private Integer idPaciente;
    private String nombrePaciente;
    private Integer idProfesional;
    private String nombreProfesional;
    private LocalDateTime fechaHora;
    private Integer duracionMinutos;
    private TipoCita tipoCita;
    private EstadoCita estado;
    private String motivo;
    private String notasSesion;
    private LocalDateTime fechaCreacion;
}