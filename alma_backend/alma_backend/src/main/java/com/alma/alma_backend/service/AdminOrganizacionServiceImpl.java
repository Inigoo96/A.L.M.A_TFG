package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AdminOrganizacion;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.AdminOrganizacionRepository;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AdminOrganizacionServiceImpl implements AdminOrganizacionService {

    @Autowired
    private AdminOrganizacionRepository adminOrganizacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Override
    @Transactional
    public AdminOrganizacion save(AdminOrganizacion adminRequest) {
        if (adminRequest.getUsuario() == null || adminRequest.getUsuario().getId() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }
        if (adminRequest.getOrganizacion() == null || adminRequest.getOrganizacion().getId() == null) {
            throw new RuntimeException("El ID de la organización es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(adminRequest.getUsuario().getId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + adminRequest.getUsuario().getId()));

        Organizacion organizacion = organizacionRepository.findById(adminRequest.getOrganizacion().getId())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con id: " + adminRequest.getOrganizacion().getId()));

        AdminOrganizacion nuevoAdmin = new AdminOrganizacion();
        nuevoAdmin.setUsuario(usuario);
        nuevoAdmin.setOrganizacion(organizacion);

        return adminOrganizacionRepository.save(nuevoAdmin);
    }

    @Override
    public Optional<AdminOrganizacion> findById(Integer id) {
        return adminOrganizacionRepository.findById(id);
    }

    @Override
    public Optional<AdminOrganizacion> findByUsuarioId(Integer usuarioId) {
        return adminOrganizacionRepository.findByUsuario_Id(usuarioId);
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
