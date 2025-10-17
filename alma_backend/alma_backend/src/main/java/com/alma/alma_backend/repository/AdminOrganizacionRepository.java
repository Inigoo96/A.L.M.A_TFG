package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.AdminOrganizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminOrganizacionRepository extends JpaRepository<AdminOrganizacion, Integer> {
}
