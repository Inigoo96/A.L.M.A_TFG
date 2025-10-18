package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.AdminOrganizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminOrganizacionRepository extends JpaRepository<AdminOrganizacion, Integer> {

    Optional<AdminOrganizacion> findByUsuario_IdUsuario(Integer idUsuario);
}
