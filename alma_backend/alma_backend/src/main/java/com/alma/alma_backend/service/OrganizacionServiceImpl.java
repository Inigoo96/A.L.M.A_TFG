package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.repository.OrganizacionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizacionServiceImpl implements OrganizacionService {

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Override
    public Organizacion save(Organizacion organizacion) {
        return organizacionRepository.save(organizacion);
    }

    @Override
    public Optional<Organizacion> findById(Integer id) {
        return organizacionRepository.findById(id);
    }

    @Override
    public Optional<Organizacion> findByCif(String cif) {
        return organizacionRepository.findByCif(cif);
    }

    @Override
    public List<Organizacion> findAll() {
        return organizacionRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        organizacionRepository.deleteById(id);
    }

    @Override
    public Organizacion updateOrganizacion(Integer id, Organizacion organizacionDetails) {
        Organizacion organizacion = organizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizacion no encontrada con id: " + id));

        organizacion.setNombreOrganizacion(organizacionDetails.getNombreOrganizacion());
        organizacion.setCif(organizacionDetails.getCif());
        organizacion.setEmailContacto(organizacionDetails.getEmailContacto());
        organizacion.setTelefonoContacto(organizacionDetails.getTelefonoContacto());
        organizacion.setActiva(organizacionDetails.getActiva());

        return organizacionRepository.save(organizacion);
    }
}
