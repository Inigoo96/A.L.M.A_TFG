package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AdminOrganizacion;
import com.alma.alma_backend.repository.AdminOrganizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminOrganizacionServiceImpl implements AdminOrganizacionService {

    @Autowired
    private AdminOrganizacionRepository adminOrganizacionRepository;

    @Override
    public AdminOrganizacion save(AdminOrganizacion adminOrganizacion) {
        return adminOrganizacionRepository.save(adminOrganizacion);
    }

    @Override
    public Optional<AdminOrganizacion> findById(Integer id) {
        return adminOrganizacionRepository.findById(id);
    }

    @Override
    public Optional<AdminOrganizacion> findByUsuarioId(Integer usuarioId) {
        return adminOrganizacionRepository.findByUsuario_IdUsuario(usuarioId);
    }

    @Override
    public List<AdminOrganizacion> findAll() {
        return adminOrganizacionRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        adminOrganizacionRepository.deleteById(id);
    }

    @Override
    public AdminOrganizacion updateAdminOrganizacion(Integer id, AdminOrganizacion adminDetails) {
        AdminOrganizacion admin = adminOrganizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Administrador no encontrado con id: " + id));
        // Esta entidad no tiene campos propios que se puedan actualizar.
        // Las actualizaciones se harían en el objeto Usuario asociado.
        return admin;
    }
}
