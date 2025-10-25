package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.InformeEmocionalRequestDTO;
import com.alma.alma_backend.entity.InformeEmocional;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface InformeEmocionalService {

    InformeEmocional generarInformeManual(InformeEmocionalRequestDTO request, Authentication authentication);

    List<InformeEmocional> findInformesByPaciente(Integer pacienteId, Authentication authentication);

    List<InformeEmocional> findInformesByProfesional(Integer profesionalId, Authentication authentication);

}
