package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoOrganizacion;
import lombok.Data;

/**
 * Lightweight representation of an organization to embed in other DTOs without exposing the full entity graph.
 */
@Data
public class OrganizacionSummaryDTO {
    private Integer id;
    private String nombreOficial;
    private String cif;
    private EstadoOrganizacion estado;
}
