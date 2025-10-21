package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.OrganizacionRegistroDTO;
import com.alma.alma_backend.dto.PacienteRegistroDTO;
import com.alma.alma_backend.dto.ProfesionalRegistroDTO;
import com.alma.alma_backend.dto.UsuarioResponseDTO;
import com.alma.alma_backend.entity.Usuario;

public interface AuthService {

    UsuarioResponseDTO registrarOrganizacionYAdmin(OrganizacionRegistroDTO registroDTO);

    UsuarioResponseDTO registrarProfesional(ProfesionalRegistroDTO registroDTO, Integer organizacionId);

    UsuarioResponseDTO registrarPaciente(PacienteRegistroDTO registroDTO, Integer organizacionId);

}
