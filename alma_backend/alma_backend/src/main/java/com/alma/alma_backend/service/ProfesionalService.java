package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Profesional;

import java.util.List;
import java.util.Optional;

public interface ProfesionalService {

    Profesional save(Profesional profesional);

    Optional<Profesional> findById(Integer id);

    Optional<Profesional> findByUsuarioId(Integer usuarioId);

    List<Profesional> findByOrganizacionId(Integer organizacionId);

    List<Profesional> findAll();

    void deleteById(Integer id);

    Profesional updateProfesional(Integer id, Profesional profesionalDetails);
}
