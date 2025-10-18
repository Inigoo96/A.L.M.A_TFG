package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Organizacion;

import java.util.List;
import java.util.Optional;

public interface OrganizacionService {

    Organizacion save(Organizacion organizacion);

    Optional<Organizacion> findById(Integer id);

    Optional<Organizacion> findByCif(String cif);

    List<Organizacion> findAll();

    void deleteById(Integer id);

    Organizacion updateOrganizacion(Integer id, Organizacion organizacionDetails);
}
