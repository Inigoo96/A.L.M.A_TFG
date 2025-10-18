package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Integer> {

    Optional<Profesional> findByUsuario_IdUsuario(Integer idUsuario);

    List<Profesional> findByOrganizacion_IdOrganizacion(Integer idOrganizacion);
}
