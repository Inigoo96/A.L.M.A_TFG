package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.UsoRecurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UsoRecursoRepository extends JpaRepository<UsoRecurso, Integer> {
    List<UsoRecurso> findByPacienteId(Integer pacienteId);
}
