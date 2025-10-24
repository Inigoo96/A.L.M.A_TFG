package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.RecursoDTO;
import com.alma.alma_backend.dto.UsoRecursoRequestDTO;
import com.alma.alma_backend.dto.UsoRecursoResponseDTO;
import com.alma.alma_backend.entity.*;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class RecursoServiceImpl implements RecursoService {

    @Autowired
    private PodcastRepository podcastRepository;

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private MusicaRepository musicaRepository;

    @Autowired
    private UsoRecursoRepository usoRecursoRepository;

    @Autowired
    private PacienteRepository pacienteRepository;

    @Override
    public List<RecursoDTO> findAllRecursos() {
        Stream<RecursoDTO> podcasts = podcastRepository.findByActivoTrue().stream().map(this::mapToRecursoDTO);
        Stream<RecursoDTO> videos = videoRepository.findByActivoTrue().stream().map(this::mapToRecursoDTO);
        Stream<RecursoDTO> musicas = musicaRepository.findByActivoTrue().stream().map(this::mapToRecursoDTO);

        return Stream.concat(podcasts, Stream.concat(videos, musicas)).collect(Collectors.toList());
    }

    @Override
    public List<RecursoDTO> findRecursosRecomendados(Integer pacienteId) {
        // Lógica de recomendación (simplificada)
        return findAllRecursos();
    }

    @Override
    public UsoRecursoResponseDTO registrarUso(UsoRecursoRequestDTO request) {
        Paciente paciente = pacienteRepository.findById(request.getPacienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado"));

        UsoRecurso uso = new UsoRecurso();
        uso.setPaciente(paciente);
        uso.setTipoRecurso(request.getTipoRecurso());
        uso.setIdRecurso(request.getIdRecurso());
        uso.setTiempoConsumidoMinutos(request.getTiempoConsumidoMinutos());
        uso.setValoracion(request.getValoracion());

        UsoRecurso savedUso = usoRecursoRepository.save(uso);
        return mapToUsoRecursoResponseDTO(savedUso);
    }

    private RecursoDTO mapToRecursoDTO(Podcast podcast) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(podcast.getId());
        dto.setTitulo(podcast.getTitulo());
        dto.setDescripcion(podcast.getDescripcion());
        dto.setUrl(podcast.getUrl());
        dto.setDuracionMinutos(podcast.getDuracionMinutos());
        dto.setCategoria(podcast.getCategoria());
        dto.setTipoRecurso("PODCAST");
        return dto;
    }

    private RecursoDTO mapToRecursoDTO(Video video) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(video.getId());
        dto.setTitulo(video.getTitulo());
        dto.setDescripcion(video.getDescripcion());
        dto.setUrl(video.getUrl());
        dto.setDuracionMinutos(video.getDuracionMinutos());
        dto.setCategoria(video.getCategoria());
        dto.setTipoRecurso("VIDEO");
        return dto;
    }

    private RecursoDTO mapToRecursoDTO(Musica musica) {
        RecursoDTO dto = new RecursoDTO();
        dto.setId(musica.getId());
        dto.setTitulo(musica.getTitulo());
        dto.setDescripcion(musica.getArtista());
        dto.setUrl(musica.getUrl());
        dto.setDuracionMinutos(musica.getDuracionMinutos());
        dto.setCategoria(musica.getGenero());
        dto.setTipoRecurso("MUSICA");
        return dto;
    }

    private UsoRecursoResponseDTO mapToUsoRecursoResponseDTO(UsoRecurso uso) {
        UsoRecursoResponseDTO dto = new UsoRecursoResponseDTO();
        dto.setId(uso.getId());
        dto.setPacienteId(uso.getPaciente().getId());
        dto.setTipoRecurso(uso.getTipoRecurso());
        dto.setIdRecurso(uso.getIdRecurso());
        dto.setFechaUso(uso.getFechaUso());
        dto.setTiempoConsumidoMinutos(uso.getTiempoConsumidoMinutos());
        dto.setValoracion(uso.getValoracion());
        return dto;
    }
}
