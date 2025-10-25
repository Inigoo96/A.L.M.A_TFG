package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.PacienteResponseDTO;
import com.alma.alma_backend.dto.PacienteUpdateRequestDTO;
import com.alma.alma_backend.entity.Paciente;
import com.alma.alma_backend.entity.TipoUsuario;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.AsignacionProfesionalPacienteRepository;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.mapper.PacienteMapper;
import com.alma.alma_backend.service.PacienteService;
import com.alma.alma_backend.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pacientes")
@PreAuthorize("hasAnyRole('ADMIN_ORGANIZACION', 'PROFESIONAL', 'SUPER_ADMIN')")
@RequiredArgsConstructor
public class PacienteController {

    private final PacienteService pacienteService;
    private final UsuarioService usuarioService;
    private final ProfesionalRepository profesionalRepository;
    private final AsignacionProfesionalPacienteRepository asignacionRepository;

    @GetMapping
    @Operation(summary = "Listar pacientes visibles para el usuario autenticado")
    @ApiResponse(responseCode = "200", description = "Pacientes recuperados correctamente")
    public ResponseEntity<List<PacienteResponseDTO>> getAllPacientes(Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        List<Paciente> pacientesDeLaOrganizacion = pacienteService.findByOrganizacionId(userOrgId);

        // Convertir las entidades a DTOs seguros
        List<PacienteResponseDTO> pacientesDTO = pacientesDeLaOrganizacion.stream()
                .map(PacienteMapper::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(pacientesDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un paciente por ID si pertenece a la organización")
    public ResponseEntity<PacienteResponseDTO> getPacienteById(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        return pacienteService.findById(id)
                .map(paciente -> {
                    if (!Objects.equals(paciente.getUsuario().getOrganizacion().getId(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build();
                    }

                    if (currentUser.getTipoUsuario() == TipoUsuario.ADMIN_ORGANIZACION || currentUser.getTipoUsuario() == TipoUsuario.SUPER_ADMIN) {
                        return ResponseEntity.ok(PacienteMapper.toResponse(paciente));
                    }

                    if (currentUser.getTipoUsuario() == TipoUsuario.PROFESIONAL) {
                        return profesionalRepository.findByUsuario_Id(currentUser.getId()).flatMap(profesional -> {
                            if (asignacionRepository.existeAsignacionActiva(profesional.getId(), paciente.getId())) {
                                return Optional.of(ResponseEntity.ok(PacienteMapper.toResponse(paciente)));
                            }
                            return Optional.of(ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build());
                        }).orElse(ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build());
                    }

                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build();
                })
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Obtener un paciente por el ID de usuario asociado")
    public ResponseEntity<PacienteResponseDTO> getPacienteByUsuarioId(@PathVariable Integer usuarioId, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        return pacienteService.findByUsuarioId(usuarioId)
                .map(paciente -> {
                    if (!Objects.equals(paciente.getUsuario().getOrganizacion().getId(), userOrgId)) {
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build();
                    }
                    return ResponseEntity.ok(PacienteMapper.toResponse(paciente));
                })
                .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado para el usuario con id: " + usuarioId));
    }

    // CORREGIDO: Se elimina el try-catch para dejar que el GlobalExceptionHandler actúe.
    @PutMapping("/{id}")
    @Operation(summary = "Actualizar parcialmente datos de un paciente")
    @ApiResponse(responseCode = "200", description = "Paciente actualizado correctamente")
    public ResponseEntity<PacienteResponseDTO> updatePaciente(@PathVariable Integer id,
                                                              @Valid @RequestBody PacienteUpdateRequestDTO pacienteDetails,
                                                              Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        return pacienteService.findById(id)
            .map(pacienteExistente -> {
                if (!Objects.equals(pacienteExistente.getUsuario().getOrganizacion().getId(), userOrgId)) {
                     return ResponseEntity.status(HttpStatus.FORBIDDEN).<PacienteResponseDTO>build();
                }
                Paciente updatedPaciente = pacienteService.updatePaciente(id, pacienteDetails);
                return ResponseEntity.ok(PacienteMapper.toResponse(updatedPaciente));
            })
            .orElseThrow(() -> new ResourceNotFoundException("Paciente no encontrado con id: " + id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePaciente(@PathVariable Integer id, Authentication authentication) {
        Usuario currentUser = usuarioService.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("Usuario autenticado no encontrado"));
        Integer userOrgId = currentUser.getOrganizacion().getId();

        pacienteService.findById(id).ifPresent(paciente -> {
            if (Objects.equals(paciente.getUsuario().getOrganizacion().getId(), userOrgId)) {
                pacienteService.deleteById(id);
            }
        });

        return ResponseEntity.noContent().build();
    }
}
