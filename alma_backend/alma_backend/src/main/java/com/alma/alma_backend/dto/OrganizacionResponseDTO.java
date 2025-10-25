package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.EstadoOrganizacion;
import com.alma.alma_backend.entity.EstadoVerificacion;
import lombok.Data;

/**
 * DTO used to expose organization data safely to API clients.
 */
@Data
public class OrganizacionResponseDTO {
    private Integer id;
    private String cif;
    private String numeroSeguridadSocial;
    private String nombreOficial;
    private String direccion;
    private String codigoRegcess;
    private String emailCorporativo;
    private String telefonoContacto;
    private String documentoCifUrl;
    private String documentoSeguridadSocialUrl;
    private EstadoVerificacion estadoVerificacion;
    private EstadoOrganizacion estado;
}
