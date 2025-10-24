package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ForoDTO;
import com.alma.alma_backend.dto.MensajeForoRequestDTO;
import com.alma.alma_backend.dto.MensajeForoResponseDTO;
import org.springframework.security.core.Authentication;

import java.util.List;

public interface ForoService {

    List<ForoDTO> findAllForos();

    List<MensajeForoResponseDTO> findMensajesByForoId(Integer foroId);

    MensajeForoResponseDTO postMensaje(MensajeForoRequestDTO mensajeForoRequestDTO, Authentication authentication);

}
