package com.alma.alma_backend.repository;

import com.alma.alma_backend.dto.OrganizacionEstadisticasDTO;
import com.alma.alma_backend.entity.EstadoOrganizacion;
import com.alma.alma_backend.entity.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {
    Optional<Organizacion> findByCif(String cif);
    Optional<Organizacion> findByEmailCorporativo(String emailCorporativo);

    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.id, o.nombreOficial, o.cif, o.estadoVerificacion, " +
           "COUNT(DISTINCT u.id), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END)) " +
           "FROM Organizacion o LEFT JOIN Usuario u ON u.organizacion.id = o.id " +
           "GROUP BY o.id, o.nombreOficial, o.cif, o.estadoVerificacion")
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizaciones();

    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.id, o.nombreOficial, o.cif, " +
           "(o.estadoVerificacion = com.alma.alma_backend.entity.EstadoVerificacion.VERIFICADA), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.ADMIN_ORGANIZACION THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.SUPER_ADMIN THEN 1 ELSE 0 END)) " +
           "FROM Organizacion o LEFT JOIN Usuario u ON u.organizacion.id = o.id " +
           "WHERE o.id = :idOrganizacion " +
           "GROUP BY o.id, o.nombreOficial, o.cif, o.estadoVerificacion")
    Optional<OrganizacionEstadisticasDTO> obtenerEstadisticasPorOrganizacion(@Param("idOrganizacion") Integer idOrganizacion);

    @Query("SELECT new com.alma.alma_backend.dto.OrganizacionEstadisticasDTO(" +
           "o.id, o.nombreOficial, o.cif, o.estadoVerificacion, " +
           "COUNT(DISTINCT u.id), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PROFESIONAL THEN 1 ELSE 0 END), " +
           "SUM(CASE WHEN u.tipoUsuario = com.alma.alma_backend.entity.TipoUsuario.PACIENTE THEN 1 ELSE 0 END)) " +
           "FROM Organizacion o LEFT JOIN Usuario u ON u.organizacion.id = o.id " +
           "WHERE o.estadoVerificacion = com.alma.alma_backend.entity.EstadoVerificacion.VERIFICADA " +
           "GROUP BY o.id, o.nombreOficial, o.cif, o.estadoVerificacion")
    List<OrganizacionEstadisticasDTO> obtenerEstadisticasOrganizacionesActivas();

    /**
     * Busca organizaciones por estado operativo.
     *
     * @param estado Estado de la organización (ACTIVA, SUSPENDIDA, BAJA)
     * @return Lista de organizaciones con ese estado
     */
    List<Organizacion> findByEstado(EstadoOrganizacion estado);
}