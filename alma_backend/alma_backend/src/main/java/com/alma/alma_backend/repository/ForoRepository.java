package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Foro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForoRepository extends JpaRepository<Foro, Integer> {

    List<Foro> findByActivoTrueOrderByNombreAsc();

}
