package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.PacienteDetalleDTO;
import com.alma.alma_backend.dto.PacienteUpdateRequestDTO;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.PacienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl extends BaseService<Paciente, Integer> implements PacienteService {

    private static final Logger logger = LoggerFactory.getLogger(PacienteServiceImpl.class);

    private final PacienteRepository pacienteRepository;

    public PacienteServiceImpl(PacienteRepository pacienteRepository) {
        super(pacienteRepository);
        this.pacienteRepository = pacienteRepository;
    }

    @Override
    public Optional<Paciente> findById(Integer id) {
        return super.findById(id);
    }

    @Override
    public Optional<Paciente> findByUsuarioId(Integer usuarioId) {
        return pacienteRepository.findByUsuario_Id(usuarioId);
    }

    @Override
    public List<Paciente> findAll() {
        return super.findAll();
    }

    @Override
    public List<Paciente> findByOrganizacionId(Integer organizacionId) {
        return pacienteRepository.findByUsuario_Organizacion_Id(organizacionId);
    }

    @Override
    public void deleteById(Integer id) {
        super.deleteById(id);
    }

    @Override
    public Paciente updatePaciente(Integer id, PacienteUpdateRequestDTO pacienteDetails) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));

        if (pacienteDetails.getFechaNacimiento() != null) {
            paciente.setFechaNacimiento(pacienteDetails.getFechaNacimiento());
        }
        if (pacienteDetails.getGenero() != null) {
            paciente.setGenero(pacienteDetails.getGenero());
        }
        if (pacienteDetails.getTarjetaSanitaria() != null && !pacienteDetails.getTarjetaSanitaria().isEmpty()) {
            paciente.setTarjetaSanitaria(pacienteDetails.getTarjetaSanitaria());
        }

        return pacienteRepository.save(paciente);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PacienteDetalleDTO> findDetalleById(Integer idPaciente) {
        logger.debug("Obteniendo detalle del paciente con ID: {}", idPaciente);
        return pacienteRepository.findDetalleById(idPaciente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDetalleDTO> findDetalleByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo pacientes de la organización ID: {}", idOrganizacion);
        return pacienteRepository.findDetalleByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDetalleDTO> findActivosByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo pacientes activos de la organización ID: {}", idOrganizacion);
        return pacienteRepository.findActivosByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDetalleDTO> findPacientesByProfesional(Integer idProfesional, boolean soloActivos) {
        logger.debug("Obteniendo pacientes del profesional ID: {} (soloActivos: {})", idProfesional, soloActivos);
        return pacienteRepository.findPacientesByProfesional(idProfesional, soloActivos);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDetalleDTO> findSinAsignarByOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo pacientes sin asignar de la organización ID: {}", idOrganizacion);
        return pacienteRepository.findSinAsignarByOrganizacion(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PacienteDetalleDTO> searchByNombreAndOrganizacion(String searchTerm, Integer idOrganizacion) {
        logger.debug("Buscando pacientes por término '{}' en organización ID: {}", searchTerm, idOrganizacion);
        return pacienteRepository.searchByNombreAndOrganizacion(searchTerm, idOrganizacion);
    }
}
