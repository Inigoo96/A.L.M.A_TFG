package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoSesion;
import com.alma.alma_backend.entity.TipoSesion;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class SesionInteraccionResponseDTO {
    private Integer id;
    private Integer idPaciente;
    private String nombrePaciente;
    private Integer idProfesional;
    private String nombreProfesional;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private Integer duracionSegundos;
    private TipoSesion tipoSesion;
    private EstadoSesion estado;
    private Integer numeroMensajes;
    private Integer satisfaccion;
    private String notasProfesional;
    private String estadoEmocionalDetectado;
    private List<String> temasConversados;
    private List<String> alertasGeneradas;
}