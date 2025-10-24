package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.RecursoDTO;
import com.alma.alma_backend.dto.UsoRecursoRequestDTO;
import com.alma.alma_backend.dto.UsoRecursoResponseDTO;

import java.util.List;

public interface RecursoService {

    List<RecursoDTO> findAllRecursos();

    List<RecursoDTO> findRecursosRecomendados(Integer pacienteId);

    UsoRecursoResponseDTO registrarUso(UsoRecursoRequestDTO usoRecursoRequestDTO);

}
