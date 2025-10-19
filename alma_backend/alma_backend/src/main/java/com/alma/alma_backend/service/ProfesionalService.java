package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ProfesionalDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalEstadisticasDTO;
import com.alma.alma_backend.entity.Profesional;

import java.util.List;
import java.util.Optional;

public interface ProfesionalService {

    Profesional save(Profesional profesional);

    Optional<Profesional> findById(Integer id);

    Optional<Profesional> findByUsuarioId(Integer usuarioId);

    List<Profesional> findAll();

    void deleteById(Integer id);

    Profesional updateProfesional(Integer id, Profesional profesionalDetails);

    // Métodos con DTOs optimizados
    Optional<ProfesionalDetalleDTO> findDetalleById(Integer idProfesional);

    List<ProfesionalDetalleDTO> findDetalleByOrganizacion(Integer idOrganizacion);

    List<ProfesionalDetalleDTO> findActivosByOrganizacion(Integer idOrganizacion);

    List<ProfesionalEstadisticasDTO> findEstadisticasByOrganizacion(Integer idOrganizacion);

    Optional<ProfesionalEstadisticasDTO> findEstadisticasById(Integer idProfesional);

    List<ProfesionalDetalleDTO> findByEspecialidadAndOrganizacion(String especialidad, Integer idOrganizacion);
}
