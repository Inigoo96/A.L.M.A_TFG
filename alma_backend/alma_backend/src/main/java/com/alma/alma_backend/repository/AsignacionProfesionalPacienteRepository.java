package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.AsignacionProfesionalPaciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AsignacionProfesionalPacienteRepository extends JpaRepository<AsignacionProfesionalPaciente, Integer> {
}

