package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ProfesionalDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalEstadisticasDTO;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.repository.ProfesionalRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalServiceImpl implements ProfesionalService {

    private static final Logger logger = LoggerFactory.getLogger(ProfesionalServiceImpl.class);

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    public Optional<Profesional> findById(Integer id) {
        return profesionalRepository.findById(id);
    }

    @Override
    public Optional<Profesional> findByUsuarioId(Integer usuarioId) {
        return profesionalRepository.findByUsuario_Id(usuarioId);
    }

    @Override
    public List<Profesional> findAll() {
        return profesionalRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        profesionalRepository.deleteById(id);
    }

    @Override
    public Profesional updateProfesional(Integer id, Profesional profesionalDetails) {
        Profesional profesional = profesionalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Profesional no encontrado con id: " + id));

        profesional.setNumeroColegiado(profesionalDetails.getNumeroColegiado());
        profesional.setEspecialidad(profesionalDetails.getEspecialidad());

        return profesionalRepository.save(profesional);
    }

    @Override
    public Profesional save(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfesionalDetalleDTO> findDetalleById(Integer idProfesional) {
        logger.debug("Obteniendo detalle del profesional con ID: {}", idProfesional);
        return profesionalRepository.findDetalleById(idProfesional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalDetalleDTO> findDetalleByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo profesionales de la organización ID: {}", idOrganizacion);
        return profesionalRepository.findDetalleByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalDetalleDTO> findActivosByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo profesionales activos de la organización ID: {}", idOrganizacion);
        return profesionalRepository.findActivosByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalEstadisticasDTO> findEstadisticasByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo estadísticas de profesionales de la organización ID: {}", idOrganizacion);
        return profesionalRepository.findEstadisticasByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ProfesionalEstadisticasDTO> findEstadisticasById(Integer idProfesional) {
        logger.debug("Obteniendo estadísticas del profesional ID: {}", idProfesional);
        return profesionalRepository.findEstadisticasById(idProfesional);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProfesionalDetalleDTO> findByEspecialidadAndOrganizacion(String especialidad, Integer idOrganizacion) {
        logger.debug("Buscando profesionales por especialidad '{}' en organización ID: {}", especialidad, idOrganizacion);
        return profesionalRepository.findByEspecialidadAndOrganizacion(especialidad, idOrganizacion);
    }
}
