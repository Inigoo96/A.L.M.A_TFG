package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.MensajeForo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MensajeForoRepository extends JpaRepository<MensajeForo, Integer> {

    List<MensajeForo> findByForoIdOrderByFechaPublicacionAsc(Integer foroId);

}
