package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.InformeEmocional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InformeEmocionalRepository extends JpaRepository<InformeEmocional, Integer> {
    List<InformeEmocional> findByPacienteIdOrderByFechaGeneracionDesc(Integer pacienteId);
    List<InformeEmocional> findByProfesionalIdOrderByFechaGeneracionDesc(Integer profesionalId);
}
