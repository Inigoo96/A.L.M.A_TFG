package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalServiceImpl implements ProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    public Profesional save(Profesional profesional) {
        return profesionalRepository.save(profesional);
    }

    @Override
    public Optional<Profesional> findById(Integer id) {
        return profesionalRepository.findById(id);
    }

    @Override
    public Optional<Profesional> findByUsuarioId(Integer usuarioId) {
        return profesionalRepository.findByUsuario_IdUsuario(usuarioId);
    }

    @Override
    public List<Profesional> findByOrganizacionId(Integer organizacionId) {
        return profesionalRepository.findByOrganizacion_IdOrganizacion(organizacionId);
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
        // El usuario y la organización no deberían cambiarse en una actualización simple

        return profesionalRepository.save(profesional);
    }
}
