package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
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

    /**
     * Obtiene las estadísticas de usuarios para todas las organizaciones.
     *
     * @return Lista de DTOs con las estadísticas agregadas de cada organización
     */
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones();

    /**
     * Obtiene las estadísticas de usuarios para una organización específica.
     *
     * @param idOrganizacion ID de la organización
     * @return Optional con el DTO de estadísticas
     */
    Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(Integer idOrganizacion);

    /**
     * Obtiene las estadísticas de organizaciones activas únicamente.
     *
     * @return Lista de DTOs con las estadísticas de organizaciones activas
     */
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas();
}
