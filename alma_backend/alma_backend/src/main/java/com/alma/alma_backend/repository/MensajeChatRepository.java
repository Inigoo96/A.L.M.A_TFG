package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.MensajeChat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatRepository extends JpaRepository<MensajeChat, Integer> {

    List<MensajeChat> findBySesionChat_IdOrderByFechaEnvioAsc(Integer idSesionChat);

    @Query("SELECT m FROM MensajeChat m WHERE m.sesionChat.id = :idSesionChat " +
           "AND m.leido = false ORDER BY m.fechaEnvio ASC")
    List<MensajeChat> findMensajesNoLeidosBySesion(@Param("idSesionChat") Integer idSesionChat);

    @Query("SELECT m FROM MensajeChat m WHERE m.sesionChat.id = :idSesionChat " +
           "AND m.remitente.id != :idUsuario AND m.leido = false " +
           "ORDER BY m.fechaEnvio ASC")
    List<MensajeChat> findMensajesNoLeidosPorDestinatario(
        @Param("idSesionChat") Integer idSesionChat,
        @Param("idUsuario") Integer idUsuario
    );

    @Query("SELECT COUNT(m) FROM MensajeChat m WHERE m.sesionChat.id = :idSesionChat " +
           "AND m.leido = false")
    Long countMensajesNoLeidos(@Param("idSesionChat") Integer idSesionChat);
}