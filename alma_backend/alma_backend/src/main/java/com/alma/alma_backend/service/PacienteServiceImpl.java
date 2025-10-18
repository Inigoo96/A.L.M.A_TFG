package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PacienteServiceImpl implements PacienteService {

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Override
    @Transactional
    public Paciente save(Paciente pacienteRequest) {
        if (pacienteRequest.getUsuario() == null || pacienteRequest.getUsuario().getIdUsuario() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }
        if (pacienteRequest.getOrganizacion() == null || pacienteRequest.getOrganizacion().getIdOrganizacion() == null) {
            throw new RuntimeException("El ID de la organización es obligatorio");
        }

        Usuario usuario = usuarioRepository.findById(pacienteRequest.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + pacienteRequest.getUsuario().getIdUsuario()));

        Organizacion organizacion = organizacionRepository.findById(pacienteRequest.getOrganizacion().getIdOrganizacion())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con id: " + pacienteRequest.getOrganizacion().getIdOrganizacion()));

        Paciente nuevoPaciente = new Paciente();
        nuevoPaciente.setUsuario(usuario);
        nuevoPaciente.setOrganizacion(organizacion);
        nuevoPaciente.setFechaNacimiento(pacienteRequest.getFechaNacimiento());
        nuevoPaciente.setGenero(pacienteRequest.getGenero());

        return pacienteRepository.save(nuevoPaciente);
    }

    @Override
    public Optional<Paciente> findById(Integer id) {
        return pacienteRepository.findById(id);
    }

    @Override
    public Optional<Paciente> findByUsuarioId(Integer usuarioId) {
        return pacienteRepository.findByUsuario_IdUsuario(usuarioId);
    }

    @Override
    public List<Paciente> findByOrganizacionId(Integer organizacionId) {
        return pacienteRepository.findByOrganizacion_IdOrganizacion(organizacionId);
    }

    @Override
    public List<Paciente> findAll() {
        return pacienteRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        pacienteRepository.deleteById(id);
    }

    @Override
    public Paciente updatePaciente(Integer id, Paciente pacienteDetails) {
        Paciente paciente = pacienteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paciente no encontrado con id: " + id));

        paciente.setFechaNacimiento(pacienteDetails.getFechaNacimiento());
        paciente.setGenero(pacienteDetails.getGenero());

        return pacienteRepository.save(paciente);
    }
}
