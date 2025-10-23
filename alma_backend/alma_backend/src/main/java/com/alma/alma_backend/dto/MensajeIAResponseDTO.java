package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.RolMensajeIA;
import com.alma.alma_backend.entity.SentimientoDetectado;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MensajeIAResponseDTO {
    private Integer id;
    private Integer idSesion;
    private RolMensajeIA rol;
    private String mensaje;
    private LocalDateTime timestampMensaje;
    private SentimientoDetectado sentimientoDetectado;
}