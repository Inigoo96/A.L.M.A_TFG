package com.alma.alma_backend.controller;

import com.alma.alma_backend.dto.PacienteDTO;
import com.alma.alma_backend.dto.PacienteDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalDetalleDTO;
import com.alma.alma_backend.dto.ProfesionalEstadisticasDTO;
import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import com.alma.alma_backend.entity.Profesional;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.exceptions.ResourceNotFoundException;
import com.alma.alma_backend.repository.ProfesionalRepository;
import com.alma.alma_backend.service.PacienteService;
import com.alma.alma_backend.service.ProfesionalService;
import com.alma.alma_backend.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profesional")
@PreAuthorize("hasRole('PROFESIONAL')")
public class ProfesionalController {

    private static final Logger logger = LoggerFactory.getLogger(ProfesionalController.class);

    private final ProfesionalRepository profesionalRepository;
    private final UsuarioService usuarioService;
    private final ProfesionalService profesionalService;
    private final PacienteService pacienteService;

    @Autowired
    public ProfesionalController(ProfesionalRepository profesionalRepository,
                                UsuarioService usuarioService,
                                ProfesionalService profesionalService,
                                PacienteService pacienteService) {
        this.profesionalRepository = profesionalRepository;
        this.usuarioService = usuarioService;
        this.profesionalService = profesionalService;
        this.pacienteService = pacienteService;
    }

    /**
     * Endpoint para que un profesional obtenga la lista de sus pacientes asignados.
     *
     * @deprecated Usar /mis-pacientes-detalle en su lugar para mejor rendimiento
     */
    @GetMapping("/mis-pacientes")
    public ResponseEntity<List<PacienteDTO>> getMisPacientes(Authentication authentication) {
        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado con email: " + userEmail));

        Profesional profesional = profesionalRepository.findByUsuario_Id(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Perfil de profesional no encontrado para el usuario: " + userEmail));

        logger.info("El profesional '{}' está consultando su lista de pacientes.", userEmail);

        List<PacienteDTO> pacientes = profesional.getAsignaciones().stream()
            .map(AsignacionProfesionalPaciente::getPaciente)
            .map(PacienteDTO::fromPaciente)
            .collect(Collectors.toList());

        return ResponseEntity.ok(pacientes);
    }

    /**
     * Obtiene la lista optimizada de pacientes asignados al profesional autenticado.
     *
     * Utiliza una query optimizada que evita N+1 queries y problemas de lazy loading.
     *
     * @param authentication Autenticación del usuario
     * @param soloActivos Si es true, solo devuelve pacientes con asignación activa
     * @return Lista de pacientes con información completa
     */
    @GetMapping("/mis-pacientes-detalle")
    public ResponseEntity<List<PacienteDetalleDTO>> getMisPacientesDetalle(
            Authentication authentication,
            @RequestParam(defaultValue = "true") boolean soloActivos) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado con email: " + userEmail));

        Profesional profesional = profesionalRepository.findByUsuario_Id(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Perfil de profesional no encontrado para el usuario: " + userEmail));

        logger.info("El profesional '{}' está consultando su lista de pacientes (soloActivos: {})", userEmail, soloActivos);

        List<PacienteDetalleDTO> pacientes = pacienteService.findPacientesByProfesional(
            profesional.getId(), soloActivos);

        return ResponseEntity.ok(pacientes);
    }

    /**
     * Obtiene la información detallada del profesional autenticado.
     *
     * @param authentication Autenticación del usuario
     * @return Detalle completo del profesional
     */
    @GetMapping("/mi-perfil")
    public ResponseEntity<ProfesionalDetalleDTO> getMiPerfil(Authentication authentication) {
        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado con email: " + userEmail));

        Profesional profesional = profesionalRepository.findByUsuario_Id(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Perfil de profesional no encontrado para el usuario: " + userEmail));

        logger.info("El profesional '{}' está consultando su perfil", userEmail);

        return profesionalService.findDetalleById(profesional.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las estadísticas de carga de trabajo del profesional autenticado.
     *
     * Incluye conteo de pacientes totales, activos, inactivos y asignaciones principales.
     *
     * @param authentication Autenticación del usuario
     * @return Estadísticas del profesional
     */
    @GetMapping("/mis-estadisticas")
    public ResponseEntity<ProfesionalEstadisticasDTO> getMisEstadisticas(Authentication authentication) {
        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado con email: " + userEmail));

        Profesional profesional = profesionalRepository.findByUsuario_Id(usuario.getId())
            .orElseThrow(() -> new ResourceNotFoundException("Perfil de profesional no encontrado para el usuario: " + userEmail));

        logger.info("El profesional '{}' está consultando sus estadísticas", userEmail);

        return profesionalService.findEstadisticasById(profesional.getId())
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    // ========== ENDPOINTS PARA ADMIN_ORGANIZACION ==========

    /**
     * Obtiene todos los profesionales de la organización del admin autenticado.
     *
     * Solo accesible por ADMIN_ORGANIZACION.
     *
     * @param authentication Autenticación del usuario
     * @param soloActivos Si es true, solo devuelve profesionales activos
     * @return Lista de profesionales de la organización
     */
    @GetMapping("/organizacion/todos")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProfesionalDetalleDTO>> getProfesionalesDeOrganizacion(
            Authentication authentication,
            @RequestParam(defaultValue = "true") boolean soloActivos) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Integer idOrganizacion = usuario.getOrganizacion().getId();

        logger.info("Admin '{}' consultando profesionales de organización ID: {} (soloActivos: {})",
                    userEmail, idOrganizacion, soloActivos);

        List<ProfesionalDetalleDTO> profesionales = soloActivos
            ? profesionalService.findActivosByOrganizacion(idOrganizacion)
            : profesionalService.findDetalleByOrganizacion(idOrganizacion);

        return ResponseEntity.ok(profesionales);
    }

    /**
     * Obtiene las estadísticas de todos los profesionales de la organización.
     *
     * Solo accesible por ADMIN_ORGANIZACION.
     * Útil para dashboards de gestión y equilibrio de cargas.
     *
     * @param authentication Autenticación del usuario
     * @return Lista de estadísticas de profesionales
     */
    @GetMapping("/organizacion/estadisticas")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProfesionalEstadisticasDTO>> getEstadisticasProfesionales(
            Authentication authentication) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Integer idOrganizacion = usuario.getOrganizacion().getId();

        logger.info("Admin '{}' consultando estadísticas de profesionales de organización ID: {}",
                    userEmail, idOrganizacion);

        List<ProfesionalEstadisticasDTO> estadisticas =
            profesionalService.findEstadisticasByOrganizacion(idOrganizacion);

        return ResponseEntity.ok(estadisticas);
    }

    /**
     * Busca profesionales por especialidad dentro de la organización.
     *
     * Solo accesible por ADMIN_ORGANIZACION.
     * Búsqueda case-insensitive y parcial.
     *
     * @param authentication Autenticación del usuario
     * @param especialidad Término de búsqueda
     * @return Lista de profesionales que coinciden con la especialidad
     */
    @GetMapping("/organizacion/buscar-especialidad")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<List<ProfesionalDetalleDTO>> buscarPorEspecialidad(
            Authentication authentication,
            @RequestParam String especialidad) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Integer idOrganizacion = usuario.getOrganizacion().getId();

        logger.info("Admin '{}' buscando profesionales por especialidad '{}' en organización ID: {}",
                    userEmail, especialidad, idOrganizacion);

        List<ProfesionalDetalleDTO> profesionales =
            profesionalService.findByEspecialidadAndOrganizacion(especialidad, idOrganizacion);

        return ResponseEntity.ok(profesionales);
    }

    /**
     * Obtiene el detalle de un profesional específico.
     *
     * Solo accesible por ADMIN_ORGANIZACION.
     * Verifica que el profesional pertenezca a la misma organización.
     *
     * @param authentication Autenticación del usuario
     * @param idProfesional ID del profesional
     * @return Detalle del profesional
     */
    @GetMapping("/{idProfesional}/detalle")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ProfesionalDetalleDTO> getDetalleProfesional(
            Authentication authentication,
            @PathVariable Integer idProfesional) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Integer idOrganizacion = usuario.getOrganizacion().getId();

        logger.info("Admin '{}' consultando detalle del profesional ID: {}", userEmail, idProfesional);

        return profesionalService.findDetalleById(idProfesional)
            .map(prof -> {
                // Verificar que pertenezca a la misma organización
                if (!prof.getIdOrganizacion().equals(idOrganizacion)) {
                    logger.warn("Admin '{}' intentó acceder a profesional de otra organización", userEmail);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<ProfesionalDetalleDTO>build();
                }
                return ResponseEntity.ok(prof);
            })
            .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtiene las estadísticas de un profesional específico.
     *
     * Solo accesible por ADMIN_ORGANIZACION.
     * Verifica que el profesional pertenezca a la misma organización.
     *
     * @param authentication Autenticación del usuario
     * @param idProfesional ID del profesional
     * @return Estadísticas del profesional
     */
    @GetMapping("/{idProfesional}/estadisticas")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<ProfesionalEstadisticasDTO> getEstadisticasProfesional(
            Authentication authentication,
            @PathVariable Integer idProfesional) {

        String userEmail = authentication.getName();
        Usuario usuario = usuarioService.findByEmail(userEmail)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));

        Integer idOrganizacion = usuario.getOrganizacion().getId();

        logger.info("Admin '{}' consultando estadísticas del profesional ID: {}", userEmail, idProfesional);

        return profesionalService.findEstadisticasById(idProfesional)
            .map(stats -> {
                // Verificar que pertenezca a la misma organización
                if (!stats.getIdOrganizacion().equals(idOrganizacion)) {
                    logger.warn("Admin '{}' intentó acceder a estadísticas de profesional de otra organización", userEmail);
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).<ProfesionalEstadisticasDTO>build();
                }
                return ResponseEntity.ok(stats);
            })
            .orElse(ResponseEntity.notFound().build());
    }
}
