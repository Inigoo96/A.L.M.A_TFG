package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.MensajeChatPacientes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeChatPacientesRepository extends JpaRepository<MensajeChatPacientes, Integer> {

    List<MensajeChatPacientes> findByChatPacientesIdOrderByFechaEnvioAsc(Integer chatPacientesId);

}
