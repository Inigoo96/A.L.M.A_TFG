package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.ForoDTO;
import com.alma.alma_backend.dto.MensajeForoRequestDTO;
import com.alma.alma_backend.dto.MensajeForoResponseDTO;
import com.alma.alma_backend.entity.Foro;
import com.alma.alma_backend.entity.MensajeForo;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.ForoRepository;
import com.alma.alma_backend.repository.MensajeForoRepository;
import com.alma.alma_backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ForoServiceImpl implements ForoService {

    @Autowired
    private ForoRepository foroRepository;

    @Autowired
    private MensajeForoRepository mensajeForoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository; // Mantenido por si es útil para otros métodos

    @Autowired
    private UsuarioService usuarioService; // Inyectado para obtener el usuario autenticado

    @Override
    public List<ForoDTO> findAllForos() {
        return foroRepository.findByActivoTrueOrderByNombreAsc().stream()
                .map(this::mapToForoDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<MensajeForoResponseDTO> findMensajesByForoId(Integer foroId) {
        return mensajeForoRepository.findByForoIdOrderByFechaPublicacionAsc(foroId).stream()
                .map(this::mapToMensajeForoResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public MensajeForoResponseDTO postMensaje(MensajeForoRequestDTO request, Authentication authentication) {
        Foro foro = foroRepository.findById(request.getForoId())
                .orElseThrow(() -> new ResourceNotFoundException("Foro no encontrado"));
        
        // Obtener el usuario desde el token de seguridad, no desde el DTO
        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        MensajeForo mensajeForo = new MensajeForo();
        mensajeForo.setForo(foro);
        mensajeForo.setUsuario(usuario);
        mensajeForo.setMensaje(request.getMensaje());

        MensajeForo savedMensaje = mensajeForoRepository.save(mensajeForo);
        return mapToMensajeForoResponseDTO(savedMensaje);
    }

    private ForoDTO mapToForoDTO(Foro foro) {
        ForoDTO dto = new ForoDTO();
        dto.setId(foro.getId());
        dto.setNombre(foro.getNombre());
        dto.setDescripcion(foro.getDescripcion());
        if (foro.getFaseDuelo() != null) {
            dto.setFaseDueloId(foro.getFaseDuelo().getId());
            dto.setFaseDueloNombre(foro.getFaseDuelo().getNombre());
        }
        return dto;
    }

    private MensajeForoResponseDTO mapToMensajeForoResponseDTO(MensajeForo mensaje) {
        MensajeForoResponseDTO dto = new MensajeForoResponseDTO();
        dto.setId(mensaje.getId());
        dto.setForoId(mensaje.getForo().getId());
        dto.setUsuarioId(mensaje.getUsuario().getId());
        dto.setNombreUsuario(mensaje.getUsuario().getNombre());
        dto.setMensaje(mensaje.getMensaje());
        dto.setFechaPublicacion(mensaje.getFechaPublicacion());
        dto.setEditado(mensaje.getEditado());
        dto.setFechaEdicion(mensaje.getFechaEdicion());
        return dto;
    }
}
