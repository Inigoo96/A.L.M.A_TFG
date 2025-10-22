package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.CambioEstadoOrganizacionDTO;
import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.entity.EstadoOrganizacion;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.TipoAccionAuditoria;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.OrganizacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class OrganizacionServiceImpl implements OrganizacionService {

    private static final Logger logger = LoggerFactory.getLogger(OrganizacionServiceImpl.class);

    @Autowired
    private OrganizacionRepository organizacionRepository;

    @Autowired
    private AuditoriaAdminService auditoriaAdminService;

    @Override
    public Organizacion save(Organizacion organizacion) {
        return organizacionRepository.save(organizacion);
    }

    @Override
    public Optional<Organizacion> findById(Integer id) {
        return organizacionRepository.findById(id);
    }

    @Override
    public Optional<Organizacion> findByCif(String cif) {
        return organizacionRepository.findByCif(cif);
    }

    @Override
    public List<Organizacion> findAll() {
        return organizacionRepository.findAll();
    }

    @Override
    public void deleteById(Integer id) {
        organizacionRepository.deleteById(id);
    }

    @Override
    public Organizacion updateOrganizacion(Integer id, Organizacion organizacionDetails) {
        Organizacion organizacion = organizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Organizacion no encontrada con id: " + id));

        organizacion.setNombreOficial(organizacionDetails.getNombreOficial());
        organizacion.setCif(organizacionDetails.getCif());
        organizacion.setEmailCorporativo(organizacionDetails.getEmailCorporativo());
        organizacion.setTelefonoContacto(organizacionDetails.getTelefonoContacto());
        organizacion.setDireccion(organizacionDetails.getDireccion());
        organizacion.setCodigoRegcess(organizacionDetails.getCodigoRegcess());
        organizacion.setNumeroSeguridadSocial(organizacionDetails.getNumeroSeguridadSocial());

        return organizacionRepository.save(organizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones() {
        logger.debug("Obteniendo estadísticas de todas las organizaciones");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasOrganizaciones();
            logger.info("Se obtuvieron estadísticas de {} organizaciones", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones", e);
            throw new RuntimeException("Error al obtener estadísticas de organizaciones", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(Integer idOrganizacion) {
        logger.debug("Obteniendo estadísticas de la organización con ID: {}", idOrganizacion);
        try {
            Optional<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasPorOrganizacion(idOrganizacion);
            if (estadisticas.isPresent()) {
                logger.info("Estadísticas obtenidas para la organización ID: {}", idOrganizacion);
            } else {
                logger.warn("No se encontraron estadísticas para la organización ID: {}", idOrganizacion);
            }
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de la organización ID: {}", idOrganizacion, e);
            throw new RuntimeException("Error al obtener estadísticas de la organización", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas() {
        logger.debug("Obteniendo estadísticas de organizaciones activas");
        try {
            List<OrganizacionEstadisticasDTO> estadisticas = organizacionRepository.obtenerEstadisticasOrganizacionesActivas();
            logger.info("Se obtuvieron estadísticas de {} organizaciones activas", estadisticas.size());
            return estadisticas;
        } catch (Exception e) {
            logger.error("Error al obtener estadísticas de organizaciones activas", e);
            throw new RuntimeException("Error al obtener estadísticas de organizaciones activas", e);
        }
    }

    // =====================================================
    // MÉTODOS DE GESTIÓN DE ESTADO (FASE 1)
    // =====================================================

    @Override
    @Transactional
    public Organizacion cambiarEstadoOrganizacion(Integer idOrganizacion, CambioEstadoOrganizacionDTO cambioEstadoDTO,
                                                   Usuario usuarioAdmin, String ipOrigen) {
        logger.info("Cambiando estado de organización ID: {} a {} por usuario: {}",
                idOrganizacion, cambioEstadoDTO.getNuevoEstado(), usuarioAdmin.getEmail());

        // Buscar la organización
        Organizacion organizacion = organizacionRepository.findById(idOrganizacion)
                .orElseThrow(() -> new RuntimeException("Organización no encontrada con ID: " + idOrganizacion));

        // Guardar estado anterior para auditoría
        EstadoOrganizacion estadoAnterior = organizacion.getEstado();

        // Preparar datos para auditoría
        Map<String, Object> datosAnteriores = new HashMap<>();
        datosAnteriores.put("ESTADO", estadoAnterior.name());
        datosAnteriores.put("NOMBRE_OFICIAL", organizacion.getNombreOficial());
        datosAnteriores.put("CIF", organizacion.getCif());

        // Cambiar estado
        organizacion.setEstado(cambioEstadoDTO.getNuevoEstado());

        // Guardar cambios
        Organizacion organizacionActualizada = organizacionRepository.save(organizacion);

        // Preparar datos nuevos para auditoría
        Map<String, Object> datosNuevos = new HashMap<>();
        datosNuevos.put("ESTADO", organizacionActualizada.getEstado().name());
        datosNuevos.put("NOMBRE_OFICIAL", organizacionActualizada.getNombreOficial());
        datosNuevos.put("CIF", organizacionActualizada.getCif());

        // Determinar tipo de acción para auditoría
        TipoAccionAuditoria tipoAccion = determinarTipoAccion(cambioEstadoDTO.getNuevoEstado());

        // Registrar en auditoría
        auditoriaAdminService.registrarAccion(
                usuarioAdmin,
                tipoAccion,
                "ORGANIZACION",
                idOrganizacion,
                datosAnteriores,
                datosNuevos,
                cambioEstadoDTO.getMotivo(),
                ipOrigen
        );

        logger.info("Estado de organización ID: {} cambiado exitosamente de {} a {}",
                idOrganizacion, estadoAnterior, cambioEstadoDTO.getNuevoEstado());

        return organizacionActualizada;
    }

    @Override
    @Transactional
    public Organizacion suspenderOrganizacion(Integer idOrganizacion, String motivo,
                                              Usuario usuarioAdmin, String ipOrigen) {
        logger.info("Suspendiendo organización ID: {} por usuario: {}", idOrganizacion, usuarioAdmin.getEmail());

        CambioEstadoOrganizacionDTO cambioEstado = new CambioEstadoOrganizacionDTO();
        cambioEstado.setNuevoEstado(EstadoOrganizacion.SUSPENDIDA);
        cambioEstado.setMotivo(motivo);

        return cambiarEstadoOrganizacion(idOrganizacion, cambioEstado, usuarioAdmin, ipOrigen);
    }

    @Override
    @Transactional
    public Organizacion activarOrganizacion(Integer idOrganizacion, String motivo,
                                            Usuario usuarioAdmin, String ipOrigen) {
        logger.info("Activando organización ID: {} por usuario: {}", idOrganizacion, usuarioAdmin.getEmail());

        CambioEstadoOrganizacionDTO cambioEstado = new CambioEstadoOrganizacionDTO();
        cambioEstado.setNuevoEstado(EstadoOrganizacion.ACTIVA);
        cambioEstado.setMotivo(motivo);

        return cambiarEstadoOrganizacion(idOrganizacion, cambioEstado, usuarioAdmin, ipOrigen);
    }

    @Override
    @Transactional
    public Organizacion darDeBajaOrganizacion(Integer idOrganizacion, String motivo,
                                              Usuario usuarioAdmin, String ipOrigen) {
        logger.warn("Dando de baja organización ID: {} por usuario: {}. IMPORTANTE: NO se elimina de BD.",
                idOrganizacion, usuarioAdmin.getEmail());

        CambioEstadoOrganizacionDTO cambioEstado = new CambioEstadoOrganizacionDTO();
        cambioEstado.setNuevoEstado(EstadoOrganizacion.BAJA);
        cambioEstado.setMotivo(motivo);

        return cambiarEstadoOrganizacion(idOrganizacion, cambioEstado, usuarioAdmin, ipOrigen);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Organizacion> findByEstado(EstadoOrganizacion estado) {
        logger.debug("Buscando organizaciones con estado: {}", estado);
        return organizacionRepository.findByEstado(estado);
    }

    /**
     * Método privado para determinar el tipo de acción según el nuevo estado.
     */
    private TipoAccionAuditoria determinarTipoAccion(EstadoOrganizacion nuevoEstado) {
        return switch (nuevoEstado) {
            case ACTIVA -> TipoAccionAuditoria.ACTIVAR_ORGANIZACION;
            case SUSPENDIDA -> TipoAccionAuditoria.SUSPENDER_ORGANIZACION;
            case BAJA -> TipoAccionAuditoria.DAR_BAJA_ORGANIZACION;
        };
    }
}
