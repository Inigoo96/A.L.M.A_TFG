package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.entity.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {

    /**
     * Comprueba si existe una organización con el CIF proporcionado.
     * @param cif El CIF a comprobar.
     * @return true si existe, false en caso contrario.
     */
    boolean existsByCif(String cif);

    /**
     * Busca una organización por su CIF.
     * @param cif El CIF a buscar.
     * @return Un Optional que contiene la Organizacion si se encuentra.
     */
    Optional<Organizacion> findByCif(String cif);

    /**
     * Obtiene las estadísticas agregadas de usuarios para todas las organizaciones.
     *
     * Esta query realiza una única consulta optimizada a la base de datos que:
     * 1. Agrupa los usuarios por organización y tipo de usuario
     * 2. Cuenta el número de usuarios en cada categoría
     * 3. Devuelve la información completa de la organización junto con los conteos
     *
     * La query utiliza LEFT JOIN para incluir organizaciones sin usuarios (conteo = 0)
     * y usa CASE WHEN para contar usuarios de cada tipo específico.
     *
     * @return Lista de DTOs con las estadísticas de cada organización
     */
    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.idOrganizacion, " +
           "o.nombreOrganizacion, " +
           "o.cif, " +
           "o.activa, " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.ADMIN_ORGANIZACION THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.SUPER_ADMIN THEN 1 ELSE 0 END)" +
           ") " +
           "FROM Organizacion o " +
           "LEFT JOIN o.usuarios u " +
           "GROUP BY o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa " +
           "ORDER BY o.nombreOrganizacion ASC")
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones();

    /**
     * Obtiene las estadísticas de usuarios para una organización específica.
     *
     * Similar a obtenerEstadisticasOrganizaciones() pero filtrado por ID de organización.
     * Útil para obtener estadísticas de una sola organización de forma eficiente.
     *
     * @param idOrganizacion ID de la organización a consultar
     * @return Optional con el DTO de estadísticas si la organización existe
     */
    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.idOrganizacion, " +
           "o.nombreOrganizacion, " +
           "o.cif, " +
           "o.activa, " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.ADMIN_ORGANIZACION THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.SUPER_ADMIN THEN 1 ELSE 0 END)" +
           ") " +
           "FROM Organizacion o " +
           "LEFT JOIN o.usuarios u " +
           "WHERE o.idOrganizacion = :idOrganizacion " +
           "GROUP BY o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa")
    Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(Integer idOrganizacion);

    /**
     * Obtiene las estadísticas de organizaciones activas únicamente.
     *
     * @return Lista de DTOs con las estadísticas de organizaciones activas
     */
    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.idOrganizacion, " +
           "o.nombreOrganizacion, " +
           "o.cif, " +
           "o.activa, " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.ADMIN_ORGANIZACION THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.SUPER_ADMIN THEN 1 ELSE 0 END)" +
           ") " +
           "FROM Organizacion o " +
           "LEFT JOIN o.usuarios u " +
           "WHERE o.activa = true " +
           "GROUP BY o.idOrganizacion, o.nombreOrganizacion, o.cif, o.activa " +
           "ORDER BY o.nombreOrganizacion ASC")
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas();
}
