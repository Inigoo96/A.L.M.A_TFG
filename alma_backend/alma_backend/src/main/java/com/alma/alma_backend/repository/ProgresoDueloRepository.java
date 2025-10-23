package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.ProgresoDuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ProgresoDueloRepository extends JpaRepository<ProgresoDuelo, Integer> {

    List<ProgresoDuelo> findByPacienteId(Integer idPaciente);

    List<ProgresoDuelo> findByProfesionalId(Integer idProfesional);

    List<ProgresoDuelo> findByPacienteIdAndFechaRegistroBetween(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin);
}
