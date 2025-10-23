package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ActualizarEstadoCitaDTO;
import com.alma.alma_backend.dto.CitaRequestDTO;
import com.alma.alma_backend.dto.CitaResponseDTO;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.CitaRepository;
import com.alma.alma_backend.repository.PacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CitaServiceImpl implements CitaService {

    @Autowired
    private CitaRepository citaRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Autowired
    private ProfesionalRepository profesionalRepository;

    @Override
    @Transactional
    public CitaResponseDTO crearCita(CitaRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getIdPaciente())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + request.getIdPaciente()));

        Profesional profesional = profesionalRepository.findById(request.getIdProfesional())
                .orElseThrow(() -> new ResourceNotFoundException("Profesional no encontrado con id: " + request.getIdProfesional()));

        // Validar que la fecha de la cita sea futura
        if (request.getFechaHora().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("La fecha de la cita debe ser futura");
        }

        Cita cita = new Cita();
        cita.setPaciente(paciente);
        cita.setProfesional(profesional);
        cita.setFechaHora(request.getFechaHora());
        cita.setDuracionMinutos(request.getDuracionMinutos() != null ? request.getDuracionMinutos() : 60);
        cita.setTipoCita(request.getTipoCita() != null ? request.getTipoCita() : TipoCita.CONSULTA);
        cita.setEstado(EstadoCita.PROGRAMADA);
        cita.setMotivo(request.getMotivo());

        Cita citaGuardada = citaRepository.save(cita);
        return convertirADTO(citaGuardada);
    }

    @Override
    public CitaResponseDTO obtenerCitaPorId(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));
        return convertirADTO(cita);
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasPorPaciente(Integer idPaciente) {
        List<Cita> citas = citaRepository.findByPaciente_IdOrderByFechaHoraAsc(idPaciente);
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasPorProfesional(Integer idProfesional) {
        List<Cita> citas = citaRepository.findByProfesional_IdOrderByFechaHoraAsc(idProfesional);
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasProximasPorPaciente(Integer idPaciente) {
        List<Cita> citas = citaRepository.findCitasProximasPaciente(idPaciente, LocalDateTime.now());
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasProximasPorProfesional(Integer idProfesional) {
        List<Cita> citas = citaRepository.findCitasProximasProfesional(idProfesional, LocalDateTime.now());
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    public List<CitaResponseDTO> obtenerCitasPorEstado(EstadoCita estado) {
        List<Cita> citas = citaRepository.findByEstadoOrderByFechaHoraAsc(estado);
        return citas.stream().map(this::convertirADTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CitaResponseDTO actualizarEstadoCita(Integer id, ActualizarEstadoCitaDTO request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        cita.setEstado(request.getEstado());

        if (request.getNotasSesion() != null) {
            cita.setNotasSesion(request.getNotasSesion());
        }

        Cita citaActualizada = citaRepository.save(cita);
        return convertirADTO(citaActualizada);
    }

    @Override
    @Transactional
    public CitaResponseDTO actualizarCita(Integer id, CitaRequestDTO request) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        if (request.getFechaHora() != null) {
            cita.setFechaHora(request.getFechaHora());
        }

        if (request.getDuracionMinutos() != null) {
            cita.setDuracionMinutos(request.getDuracionMinutos());
        }

        if (request.getTipoCita() != null) {
            cita.setTipoCita(request.getTipoCita());
        }

        if (request.getMotivo() != null) {
            cita.setMotivo(request.getMotivo());
        }

        Cita citaActualizada = citaRepository.save(cita);
        return convertirADTO(citaActualizada);
    }

    @Override
    @Transactional
    public void cancelarCita(Integer id) {
        Cita cita = citaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cita no encontrada con id: " + id));

        cita.setEstado(EstadoCita.CANCELADA);
        citaRepository.save(cita);
    }

    @Override
    @Transactional
    public void eliminarCita(Integer id) {
        if (!citaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cita no encontrada con id: " + id);
        }
        citaRepository.deleteById(id);
    }

    private CitaResponseDTO convertirADTO(Cita cita) {
        CitaResponseDTO dto = new CitaResponseDTO();
        dto.setId(cita.getId());
        dto.setIdPaciente(cita.getPaciente().getId());
        dto.setNombrePaciente(cita.getPaciente().getUsuario().getNombre() + " " +
                cita.getPaciente().getUsuario().getApellidos());
        dto.setIdProfesional(cita.getProfesional().getId());
        dto.setNombreProfesional(cita.getProfesional().getUsuario().getNombre() + " " +
                cita.getProfesional().getUsuario().getApellidos());
        dto.setFechaHora(cita.getFechaHora());
        dto.setDuracionMinutos(cita.getDuracionMinutos());
        dto.setTipoCita(cita.getTipoCita());
        dto.setEstado(cita.getEstado());
        dto.setMotivo(cita.getMotivo());
        dto.setNotasSesion(cita.getNotasSesion());
        dto.setFechaCreacion(cita.getFechaCreacion());

        return dto;
    }
}