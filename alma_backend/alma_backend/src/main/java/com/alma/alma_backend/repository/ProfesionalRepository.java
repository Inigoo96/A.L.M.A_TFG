package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Profesional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesionalRepository extends JpaRepository<Profesional, Integer> {
}

