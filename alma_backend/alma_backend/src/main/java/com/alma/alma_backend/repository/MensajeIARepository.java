package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.MensajeIA;
import com.alma.alma_backend.entity.RolMensajeIA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeIARepository extends JpaRepository<MensajeIA, Integer> {

    List<MensajeIA> findBySesionIdOrderByTimestampMensajeAsc(Integer idSesion);

    @Query("SELECT m FROM MensajeIA m WHERE m.sesion.id = :idSesion " +
           "AND m.rol = :rol ORDER BY m.timestampMensaje ASC")
    List<MensajeIA> findBySesionAndRol(
        @Param("idSesion") Integer idSesion,
        @Param("rol") RolMensajeIA rol
    );

    @Query("SELECT COUNT(m) FROM MensajeIA m WHERE m.sesion.id = :idSesion")
    Long countBySesionId(@Param("idSesion") Integer idSesion);

    @Query("SELECT m FROM MensajeIA m WHERE m.sesion.paciente.id = :idPaciente " +
           "ORDER BY m.timestampMensaje DESC")
    List<MensajeIA> findByPacienteId(@Param("idPaciente") Integer idPaciente);
}