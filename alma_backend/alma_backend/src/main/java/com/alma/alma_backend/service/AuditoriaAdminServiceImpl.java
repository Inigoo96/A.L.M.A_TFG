package com.alma.alma_backend.service;

import com.alma.alma_backend.entity.AuditoriaAdmin;
import com.alma.alma_backend.entity.TipoAccionAuditoria;
import com.alma.alma_backend.entity.Usuario;
import com.alma.alma_backend.repository.AuditoriaAdminRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Implementación del servicio de auditoría administrativa.
 *
 * Registra todas las acciones críticas realizadas por SUPER_ADMIN
 * para cumplir con requisitos de trazabilidad y cumplimiento normativo.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuditoriaAdminServiceImpl implements AuditoriaAdminService {

    private final AuditoriaAdminRepository auditoriaAdminRepository;

    @Override
    @Transactional
    public AuditoriaAdmin registrarAccion(
            Usuario usuarioAdmin,
            TipoAccionAuditoria tipoAccion,
            String tablaAfectada,
            Integer idRegistroAfectado,
            Map<String, Object> datosAnteriores,
            Map<String, Object> datosNuevos,
            String motivo,
            String ipOrigen) {

        log.info("Registrando acción de auditoría: {} por usuario ID: {} en tabla: {} (ID: {}) con motivo: {}",
                tipoAccion, usuarioAdmin.getId(), tablaAfectada, idRegistroAfectado, motivo);

        // Usar setters para evitar ambigüedad del constructor y asegurar que el motivo se asigna
        AuditoriaAdmin auditoria = new AuditoriaAdmin();
        auditoria.setUsuarioAdmin(usuarioAdmin);
        auditoria.setTipoAccion(tipoAccion);
        auditoria.setTablaAfectada(tablaAfectada);
        auditoria.setIdRegistroAfectado(idRegistroAfectado);
        auditoria.setDatosAnteriores(datosAnteriores);
        auditoria.setDatosNuevos(datosNuevos);
        auditoria.setMotivo(motivo); // Asignación explícita del motivo
        auditoria.setIpOrigen(ipOrigen);
        auditoria.setFechaAccion(LocalDateTime.now());


        AuditoriaAdmin auditoriaGuardada = auditoriaAdminRepository.save(auditoria);

        log.info("Acción de auditoría registrada exitosamente con ID: {}", auditoriaGuardada.getId());

        return auditoriaGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerAuditoriaPorAdmin(Integer idUsuarioAdmin) {
        log.debug("Obteniendo auditoría para administrador ID: {}", idUsuarioAdmin);
        return auditoriaAdminRepository.findByUsuarioAdmin_IdOrderByFechaAccionDesc(idUsuarioAdmin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerAuditoriaPorTipo(TipoAccionAuditoria tipoAccion) {
        log.debug("Obteniendo auditoría por tipo de acción: {}", tipoAccion);
        return auditoriaAdminRepository.findByTipoAccionOrderByFechaAccionDesc(tipoAccion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerAuditoriaOrganizacion(Integer idOrganizacion) {
        log.debug("Obteniendo auditoría para organización ID: {}", idOrganizacion);
        return auditoriaAdminRepository.findByOrganizacionId(idOrganizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerAuditoriaPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        log.debug("Obteniendo auditoría entre {} y {}", fechaInicio, fechaFin);
        return auditoriaAdminRepository.findByFechaAccionBetweenOrderByFechaAccionDesc(fechaInicio, fechaFin);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerUltimasAcciones(int limit) {
        log.debug("Obteniendo las últimas {} acciones de auditoría", limit);
        return auditoriaAdminRepository.findTopNByOrderByFechaAccionDesc(limit);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaAdmin> obtenerAuditoriaPorRegistro(String tablaAfectada, Integer idRegistro) {
        log.debug("Obteniendo auditoría para tabla: {} registro ID: {}", tablaAfectada, idRegistro);
        return auditoriaAdminRepository.findByTablaAfectadaAndIdRegistroAfectadoOrderByFechaAccionDesc(
                tablaAfectada, idRegistro);
    }
}
