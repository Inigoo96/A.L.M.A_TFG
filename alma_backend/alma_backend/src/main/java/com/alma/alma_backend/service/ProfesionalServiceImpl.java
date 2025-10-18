package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProfesionalServiceImpl implements ProfesionalService {

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Override
    @Transactional
    public Profesional save(Profesional profesionalRequest) {
        // 1. Validar que los IDs vienen en la petición
        if (profesionalRequest.getUsuario() == null || profesionalRequest.getUsuario().getIdUsuario() == null) {
            throw new RuntimeException("El ID del usuario es obligatorio");
        }
        if (profesionalRequest.getOrganizacion() == null || profesionalRequest.getOrganizacion().getIdOrganizacion() == null) {
            throw new RuntimeException("El ID de la organización es obligatorio");
        }

        // 2. Cargar las entidades REALES desde la base de datos
        Usuario usuario = usuarioRepository.findById(profesionalRequest.getUsuario().getIdUsuario())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + profesionalRequest.getUsuario().getIdUsuario()));

        Organizacion organizacion = organizacionRepository.findById(profesionalRequest.getOrganizacion().getIdOrganizacion())
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con id: " + profesionalRequest.getOrganizacion().getIdOrganizacion()));

        // 3. Crear y ensamblar la nueva entidad Profesional
        Profesional nuevoProfesional = new Profesional();
        nuevoProfesional.setUsuario(usuario); // Entidad real, no el 'fantasma'
        nuevoProfesional.setOrganizacion(organizacion); // Entidad real, no el 'fantasma'
        nuevoProfesional.setNumeroColegiado(profesionalRequest.getNumeroColegiado());
        nuevoProfesional.setEspecialidad(profesionalRequest.getEspecialidad());

        // 4. Guardar la entidad Profesional correctamente ensamblada
        return profesionalRepository.save(nuevoProfesional);
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
