package com.alma.alma_backend.mapper;

import com.alma.alma_backend.dto.OrganizacionSummaryDTO;
import com.alma.alma_backend.dto.UsuarioResponseDTO;
import com.alma.alma_backend.dto.UsuarioUpdateRequestDTO;
import com.alma.alma_backend.entity.Usuario;

/**
 * Mapper utilities for user domain conversions.
 */
public final class UsuarioMapper {

    private UsuarioMapper() {
    }

    public static UsuarioResponseDTO toResponse(Usuario usuario) {
        if (usuario == null) {
            return null;
        }
        UsuarioResponseDTO dto = new UsuarioResponseDTO();
        dto.setId(usuario.getId());
        dto.setDni(usuario.getDni());
        dto.setEmail(usuario.getEmail());
        dto.setNombre(usuario.getNombre());
        dto.setApellidos(usuario.getApellidos());
        dto.setTelefono(usuario.getTelefono());
        dto.setTipoUsuario(usuario.getTipoUsuario());
        dto.setActivo(Boolean.TRUE.equals(usuario.getActivo()));
        dto.setFechaRegistro(usuario.getFechaRegistro());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setPasswordTemporal(Boolean.TRUE.equals(usuario.getPasswordTemporal()));
        dto.setCargo(usuario.getCargo());
        dto.setDocumentoCargoUrl(usuario.getDocumentoCargoUrl());
        dto.setOrganizacion(OrganizacionMapper.toSummary(usuario.getOrganizacion()));
        return dto;
    }

    public static void updateEntity(Usuario entity, UsuarioUpdateRequestDTO request) {
        if (entity == null || request == null) {
            return;
        }
        if (request.getNombre() != null) {
            entity.setNombre(request.getNombre());
        }
        if (request.getApellidos() != null) {
            entity.setApellidos(request.getApellidos());
        }
        if (request.getEmail() != null) {
            entity.setEmail(request.getEmail());
        }
        if (request.getTelefono() != null) {
            entity.setTelefono(request.getTelefono());
        }
        if (request.getDni() != null) {
            entity.setDni(request.getDni());
        }
        if (request.getCargo() != null) {
            entity.setCargo(request.getCargo());
        }
        if (request.getDocumentoCargoUrl() != null) {
            entity.setDocumentoCargoUrl(request.getDocumentoCargoUrl());
        }
        if (request.getActivo() != null) {
            entity.setActivo(request.getActivo());
        }
    }
}
