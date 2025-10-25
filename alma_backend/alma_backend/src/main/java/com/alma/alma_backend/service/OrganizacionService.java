package com.alma.alma_backend.service;

import com.alma.alma_backend.dto.CambioEstadoOrganizacionDTO;
import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.dto.OrganizacionRequestDTO;
import com.alma.alma_backend.entity.EstadoOrganizacion;
import com.alma.alma_backend.entity.Organizacion;
import com.alma.alma_backend.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface OrganizacionService {

    Organizacion save(Organizacion organizacion);

    Optional<Organizacion> findById(Integer id);

    Optional<Organizacion> findByCif(String cif);

    List<Organizacion> findAll();

    void deleteById(Integer id);

    Organizacion updateOrganizacion(Integer id, OrganizacionRequestDTO organizacionDetails);

    /**
     * Obtiene las estadísticas de usuarios para todas las organizaciones.
     *
     * @return Lista de DTOs con las estadísticas agregadas de cada organización
     */
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones();

    /**
     * Obtiene las estadísticas de usuarios para una organización específica.
     *
     * @param idOrganizacion ID de la organización
     * @return Optional con el DTO de estadísticas
     */
    Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(Integer idOrganizacion);

    /**
     * Obtiene las estadísticas de organizaciones activas únicamente.
     *
     * @return Lista de DTOs con las estadísticas de organizaciones activas
     */
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas();

    // =====================================================
    // MÉTODOS DE GESTIÓN DE ESTADO (FASE 1)
    // =====================================================

    /**
     * Cambia el estado de una organización y registra la acción en auditoría.
     *
     * @param idOrganizacion ID de la organización
     * @param cambioEstadoDTO DTO con el nuevo estado y motivo
     * @param usuarioAdmin Usuario SUPER_ADMIN que realiza el cambio
     * @param ipOrigen IP desde donde se realiza la acción
     * @return Organización actualizada
     */
    Organizacion cambiarEstadoOrganizacion(Integer idOrganizacion, CambioEstadoOrganizacionDTO cambioEstadoDTO,
                                           Usuario usuarioAdmin, String ipOrigen);

    /**
     * Suspende temporalmente una organización.
     * La organización no puede realizar operaciones críticas mientras esté suspendida.
     *
     * @param idOrganizacion ID de la organización
     * @param motivo Motivo de la suspensión
     * @param usuarioAdmin Usuario SUPER_ADMIN que realiza la acción
     * @param ipOrigen IP desde donde se realiza la acción
     * @return Organización suspendida
     */
    Organizacion suspenderOrganizacion(Integer idOrganizacion, String motivo,
                                       Usuario usuarioAdmin, String ipOrigen);

    /**
     * Activa una organización suspendida.
     *
     * @param idOrganizacion ID de la organización
     * @param motivo Motivo de la activación
     * @param usuarioAdmin Usuario SUPER_ADMIN que realiza la acción
     * @param ipOrigen IP desde donde se realiza la acción
     * @return Organización activada
     */
    Organizacion activarOrganizacion(Integer idOrganizacion, String motivo,
                                     Usuario usuarioAdmin, String ipOrigen);

    /**
     * Da de baja definitivamente una organización.
     * La organización NO se elimina de la base de datos, solo cambia su estado a BAJA.
     *
     * @param idOrganizacion ID de la organización
     * @param motivo Motivo de la baja
     * @param usuarioAdmin Usuario SUPER_ADMIN que realiza la acción
     * @param ipOrigen IP desde donde se realiza la acción
     * @return Organización dada de baja
     */
    Organizacion darDeBajaOrganizacion(Integer idOrganizacion, String motivo,
                                       Usuario usuarioAdmin, String ipOrigen);

    /**
     * Obtiene todas las organizaciones filtradas por estado.
     *
     * @param estado Estado a filtrar
     * @return Lista de organizaciones con ese estado
     */
    List<Organizacion> findByEstado(EstadoOrganizacion estado);
}
