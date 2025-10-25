package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.AuthenticationRequest;
import com.alma.alma_backend.dto.AuthenticationResponse;
import com.alma.alma_backend.dto.OrganizacionRegistroDTO;
import com.alma.alma_backend.dto.PacienteRegistroDTO;
import com.alma.alma_backend.dto.ProfesionalRegistroDTO;
import com.alma.alma_backend.entity.Usuario;

public interface AuthService {

    Usuario registrarOrganizacionYAdmin(OrganizacionRegistroDTO registroDTO);

    Usuario registrarProfesional(ProfesionalRegistroDTO registroDTO, Integer organizacionId);

    Usuario registrarPaciente(PacienteRegistroDTO registroDTO, Integer organizacionId);

    AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest);

}
