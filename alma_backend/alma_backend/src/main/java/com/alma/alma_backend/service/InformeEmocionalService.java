package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.InformeEmocionalRequestDTO;
import com.alma.alma_backend.dto.InformeEmocionalResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface InformeEmocionalService {

    InformeEmocionalResponseDTO generarInformeManual(InformeEmocionalRequestDTO request, Authentication authentication);

    List<InformeEmocionalResponseDTO> findInformesByPaciente(Integer pacienteId, Authentication authentication);

    List<InformeEmocionalResponseDTO> findInformesByProfesional(Integer profesionalId, Authentication authentication);

}
