package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoSesionChat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SesionChatResponseDTO {
    private Integer id;
    private Integer idPaciente;
    private String nombrePaciente;
    private Integer idProfesional;
    private String nombreProfesional;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimaActividad;
    private EstadoSesionChat estado;
    private Long mensajesNoLeidos;
}