package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.repository.OrganizacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrganizacionServiceImpl implements OrganizacionService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacionServiceImpl.class);

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

    @Override
    @Transactional(readOnly = true)
    public List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones() {
        logger.debug("Obteniendo estadísticas de todas las organizaciones");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasOrganizaciones();
            logger.info("Se obtuvieron estadísticas de {} organizaciones", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones", e);
            throw new RuntimeException("Error al obtener estadísticas de organizaciones", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo estadísticas de la organización con ID: {}", idOrganizacion);
        try {
            Optional<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasPorOrganizacion(idOrganizacion);
            if (estadisticas.isPresent()) {
                logger.info("Estadísticas obtenidas para la organización ID: {}", idOrganizacion);
            } else {
                logger.warn("No se encontraron estadísticas para la organización ID: {}", idOrganizacion);
            }
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de la organización ID: {}", idOrganizacion, e);
            throw new RuntimeException("Error al obtener estadísticas de la organización", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas() {
        logger.debug("Obteniendo estadísticas de organizaciones activas");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasOrganizacionesActivas();
            logger.info("Se obtuvieron estadísticas de {} organizaciones activas", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones activas", e);
            throw new RuntimeException("Error al obtener estadísticas de organizaciones activas", e);
        }
    }
}
