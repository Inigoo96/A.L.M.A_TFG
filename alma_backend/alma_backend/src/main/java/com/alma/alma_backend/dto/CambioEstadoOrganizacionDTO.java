package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoOrganizacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para solicitar el cambio de estado de una organización.
 *
 * Este DTO se utiliza cuando el SUPER_ADMIN realiza acciones de gestión
 * como suspender, activar o dar de baja una organización.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CambioEstadoOrganizacionDTO {

    /**
     * Nuevo estado de la organización.
     */
    private EstadoOrganizacion nuevoEstado;

    /**
     * Motivo o justificación del cambio de estado.
     * Es obligatorio para trazabilidad y auditoría.
     */
    private String motivo;

    /**
     * Información adicional sobre el cambio (opcional).
     */
    private String observaciones;
}