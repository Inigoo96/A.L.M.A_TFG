package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ActualizarMetaRequestDTO;
import com.alma.alma_backend.dto.EstadisticasMetasDTO;
import com.alma.alma_backend.dto.MetaDiariaRequestDTO;
import com.alma.alma_backend.dto.MetaDiariaResponseDTO;
import com.alma.alma_backend.entity.EstadoMeta;
import com.alma.alma_backend.entity.MetaDiaria;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.MetaDiariaRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MetaDiariaServiceImpl implements MetaDiariaService {

    @Autowired
    private MetaDiariaRepository metaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    @Transactional
    public MetaDiariaResponseDTO crearMeta(MetaDiariaRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + request.getIdPaciente()));

        LocalDate fechaAsignada = request.getFechaAsignada() != null ? request.getFechaAsignada() : LocalDate.now();

        // Validar que no exista una meta duplicada
        List<MetaDiaria> metasDuplicadas = metaRepository.findMetaDuplicada(
                request.getIdPaciente(),
                fechaAsignada,
                request.getTextoMeta()
        );

        if (!metasDuplicadas.isEmpty()) {
            throw new IllegalArgumentException("Ya existe una meta similar para esta fecha");
        }

        MetaDiaria meta = new MetaDiaria();
        meta.setPaciente(paciente);
        meta.setTextoMeta(request.getTextoMeta());
        meta.setFechaAsignada(fechaAsignada);
        meta.setEstado(EstadoMeta.PENDIENTE);
        meta.setNotas(request.getNotas());

        MetaDiaria metaGuardada = metaRepository.save(meta);
        return convertirADTO(metaGuardada);
    }

    @Override
    public MetaDiariaResponseDTO obtenerMetaPorId(Integer id) {
        MetaDiaria meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + id));
        return convertirADTO(meta);
    }

    @Override
    public List<MetaDiariaResponseDTO> obtenerMetasPorPaciente(Integer idPaciente) {
        List<MetaDiaria> metas = metaRepository.findByPacienteIdOrderByFechaAsignadaDesc(idPaciente);
        return metas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<MetaDiariaResponseDTO> obtenerMetasHoyPorPaciente(Integer idPaciente) {
        List<MetaDiaria> metas = metaRepository.findMetasHoyPorPaciente(idPaciente);
        return metas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<MetaDiariaResponseDTO> obtenerMetasEnRango(Integer idPaciente, LocalDate fechaInicio, LocalDate fechaFin) {
        List<MetaDiaria> metas = metaRepository.findMetasEnRango(idPaciente, fechaInicio, fechaFin);
        return metas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<MetaDiariaResponseDTO> obtenerMetasPorEstado(Integer idPaciente, EstadoMeta estado) {
        List<MetaDiaria> metas = metaRepository.findByPacienteIdAndEstadoOrderByFechaAsignadaDesc(idPaciente, estado);
        return metas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public MetaDiariaResponseDTO actualizarEstadoMeta(ActualizarMetaRequestDTO request) {
        MetaDiaria meta = metaRepository.findById(request.getIdMeta())
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + request.getIdMeta()));

        meta.setEstado(request.getEstado());

        if (request.getNotas() != null) {
            meta.setNotas(request.getNotas());
        }

        MetaDiaria metaGuardada = metaRepository.save(meta);
        return convertirADTO(metaGuardada);
    }

    @Override
    @Transactional
    public MetaDiariaResponseDTO completarMeta(Integer idMeta, String notas) {
        MetaDiaria meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + idMeta));

        if (meta.getEstado() == EstadoMeta.COMPLETADA) {
            throw new IllegalStateException("La meta ya está completada");
        }

        meta.setEstado(EstadoMeta.COMPLETADA);
        meta.setFechaCompletada(LocalDateTime.now()); // Asignar la fecha aquí

        if (notas != null) {
            meta.setNotas(notas);
        }

        MetaDiaria metaGuardada = metaRepository.save(meta);
        return convertirADTO(metaGuardada);
    }

    @Override
    @Transactional
    public MetaDiariaResponseDTO cancelarMeta(Integer idMeta, String notas) {
        MetaDiaria meta = metaRepository.findById(idMeta)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + idMeta));

        meta.setEstado(EstadoMeta.CANCELADA);

        if (notas != null) {
            meta.setNotas(notas);
        }

        MetaDiaria metaGuardada = metaRepository.save(meta);
        return convertirADTO(metaGuardada);
    }

    @Override
    @Transactional
    public MetaDiariaResponseDTO actualizarMeta(Integer id, MetaDiariaRequestDTO request) {
        MetaDiaria meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + id));

        if (request.getTextoMeta() != null) {
            meta.setTextoMeta(request.getTextoMeta());
        }

        if (request.getFechaAsignada() != null) {
            meta.setFechaAsignada(request.getFechaAsignada());
        }

        if (request.getNotas() != null) {
            meta.setNotas(request.getNotas());
        }

        MetaDiaria metaGuardada = metaRepository.save(meta);
        return convertirADTO(metaGuardada);
    }

    @Override
    @Transactional
    public void eliminarMeta(Integer id) {
        MetaDiaria meta = metaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Meta no encontrada con id: " + id));
        metaRepository.delete(meta);
    }

    @Override
    public EstadisticasMetasDTO obtenerEstadisticasMetas(Integer idPaciente) {
        Paciente paciente = pacienteRepository.findById(idPaciente)
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + idPaciente));

        Long totalMetas = metaRepository.countByPacienteIdAndEstado(idPaciente, null);
        Long metasCompletadas = metaRepository.countByPacienteIdAndEstado(idPaciente, EstadoMeta.COMPLETADA);
        Long metasPendientes = metaRepository.countByPacienteIdAndEstado(idPaciente, EstadoMeta.PENDIENTE);
        Long metasCanceladas = metaRepository.countByPacienteIdAndEstado(idPaciente, EstadoMeta.CANCELADA);

        // Calcular todas las metas para el total
        List<MetaDiaria> todasMetas = metaRepository.findByPacienteIdOrderByFechaAsignadaDesc(idPaciente);
        totalMetas = (long) todasMetas.size();

        double porcentajeCompletado = totalMetas > 0 ? (metasCompletadas * 100.0) / totalMetas : 0.0;

        String nombrePaciente = paciente.getUsuario().getNombre() + " " + paciente.getUsuario().getApellidos();

        return new EstadisticasMetasDTO(
                idPaciente,
                nombrePaciente,
                totalMetas,
                metasCompletadas,
                metasPendientes,
                metasCanceladas,
                Math.round(porcentajeCompletado * 100.0) / 100.0
        );
    }

    // Método auxiliar privado

    private MetaDiariaResponseDTO convertirADTO(MetaDiaria meta) {
        MetaDiariaResponseDTO dto = new MetaDiariaResponseDTO();
        dto.setId(meta.getId());
        dto.setIdPaciente(meta.getPaciente().getId());
        dto.setNombrePaciente(meta.getPaciente().getUsuario().getNombre() + " " +
                             meta.getPaciente().getUsuario().getApellidos());
        dto.setTextoMeta(meta.getTextoMeta());
        dto.setFechaAsignada(meta.getFechaAsignada());
        dto.setEstado(meta.getEstado());
        dto.setNotas(meta.getNotas());
        dto.setFechaCompletada(meta.getFechaCompletada());
        dto.setFechaCreacion(meta.getFechaCreacion());
        return dto;
    }
}
