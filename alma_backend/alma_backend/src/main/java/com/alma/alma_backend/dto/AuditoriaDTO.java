package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoAccionAuditoria;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * DTO para representar un registro de auditoría.
 *
 * Se utiliza para retornar información de auditoría al frontend
 * sin exponer la entidad completa.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaDTO {

    private Integer id;

    private Integer idUsuarioAdmin;
    private String emailAdmin;
    private String nombreAdmin;
    private String apellidosAdmin;

    private TipoAccionAuditoria tipoAccion;

    private String tablaAfectada;
    private Integer idRegistroAfectado;

    private Map<String, Object> datosAnteriores;
    private Map<String, Object> datosNuevos;

    private String motivo;
    private String ipOrigen;

    private LocalDateTime fechaAccion;

    /**
     * Constructor simplificado para crear DTO desde entidad AuditoriaAdmin.
     */
    public AuditoriaDTO(Integer id, TipoAccionAuditoria tipoAccion, String tablaAfectada,
                        Integer idRegistroAfectado, Map<String, Object> datosAnteriores,
                        Map<String, Object> datosNuevos, String motivo, String ipOrigen,
                        LocalDateTime fechaAccion) {
        this.id = id;
        this.tipoAccion = tipoAccion;
        this.tablaAfectada = tablaAfectada;
        this.idRegistroAfectado = idRegistroAfectado;
        this.datosAnteriores = datosAnteriores;
        this.datosNuevos = datosNuevos;
        this.motivo = motivo;
        this.ipOrigen = ipOrigen;
        this.fechaAccion = fechaAccion;
    }
}