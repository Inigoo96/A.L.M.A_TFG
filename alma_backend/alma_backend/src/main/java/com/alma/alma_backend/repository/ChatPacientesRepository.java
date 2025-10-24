package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.ChatPacientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatPacientesRepository extends JpaRepository<ChatPacientes, Integer> {

    @Query("SELECT cp FROM ChatPacientes cp WHERE (cp.paciente1.id = :id1 AND cp.paciente2.id = :id2) OR (cp.paciente1.id = :id2 AND cp.paciente2.id = :id1)")
    Optional<ChatPacientes> findChatByPacientes(@Param("id1") Integer id1, @Param("id2") Integer id2);

}
