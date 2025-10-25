package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.OrganizacionRequestDTO;
import com.alma.alma_backend.dto.OrganizacionResponseDTO;
import com.alma.alma_backend.dto.OrganizacionSummaryDTO;
import com.alma.alma_backend.entity.Organizacion;

/**
 * Mapper utilities for converting organization entities to request/response DTOs.
 */
public final class OrganizacionMapper {

    private OrganizacionMapper() {
    }

    public static OrganizacionResponseDTO toResponse(Organizacion entity) {
        if (entity == null) {
            return null;
        }
        OrganizacionResponseDTO dto = new OrganizacionResponseDTO();
        dto.setId(entity.getId());
        dto.setCif(entity.getCif());
        dto.setNumeroSeguridadSocial(entity.getNumeroSeguridadSocial());
        dto.setNombreOficial(entity.getNombreOficial());
        dto.setDireccion(entity.getDireccion());
        dto.setCodigoRegcess(entity.getCodigoRegcess());
        dto.setEmailCorporativo(entity.getEmailCorporativo());
        dto.setTelefonoContacto(entity.getTelefonoContacto());
        dto.setDocumentoCifUrl(entity.getDocumentoCifUrl());
        dto.setDocumentoSeguridadSocialUrl(entity.getDocumentoSeguridadSocialUrl());
        dto.setEstadoVerificacion(entity.getEstadoVerificacion());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public static OrganizacionSummaryDTO toSummary(Organizacion entity) {
        if (entity == null) {
            return null;
        }
        OrganizacionSummaryDTO dto = new OrganizacionSummaryDTO();
        dto.setId(entity.getId());
        dto.setNombreOficial(entity.getNombreOficial());
        dto.setCif(entity.getCif());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public static void updateEntity(Organizacion entity, OrganizacionRequestDTO request) {
        if (request == null || entity == null) {
            return;
        }
        entity.setCif(request.getCif());
        entity.setNumeroSeguridadSocial(request.getNumeroSeguridadSocial());
        entity.setNombreOficial(request.getNombreOficial());
        entity.setDireccion(request.getDireccion());
        entity.setCodigoRegcess(request.getCodigoRegcess());
        entity.setEmailCorporativo(request.getEmailCorporativo());
        entity.setTelefonoContacto(request.getTelefonoContacto());
        entity.setDocumentoCifUrl(request.getDocumentoCifUrl());
        entity.setDocumentoSeguridadSocialUrl(request.getDocumentoSeguridadSocialUrl());
    }

    public static Organizacion fromRequest(OrganizacionRequestDTO request) {
        if (request == null) {
            return null;
        }
        Organizacion entity = new Organizacion();
        updateEntity(entity, request);
        return entity;
    }
}
