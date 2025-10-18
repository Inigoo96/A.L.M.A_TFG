package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AdminOrganizacion;

import java.util.List;
import java.util.Optional;

public interface AdminOrganizacionService {

    AdminOrganizacion save(AdminOrganizacion adminOrganizacion);

    Optional<AdminOrganizacion> findById(Integer id);

    Optional<AdminOrganizacion> findByUsuarioId(Integer usuarioId);

    List<AdminOrganizacion> findAll();

    void deleteById(Integer id);

    // Los administradores de organización no suelen tener campos propios que actualizar,
    // pero dejamos el método por coherencia.
    AdminOrganizacion updateAdminOrganizacion(Integer id, AdminOrganizacion adminDetails);
}
