package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {
}

