package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Paciente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PacienteRepository extends JpaRepository<Paciente, Integer> {

    Optional<Paciente> findByUsuario_IdUsuario(Integer idUsuario);

    List<Paciente> findByOrganizacion_IdOrganizacion(Integer idOrganizacion);
}
