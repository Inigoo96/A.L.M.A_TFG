package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VideoRepository extends JpaRepository<Video, Integer> {
    List<Video> findByActivoTrue();
    List<Video> findByFaseDueloRecomendadaIdAndActivoTrue(Integer faseDueloId);
    List<Video> findByCategoriaAndActivoTrue(String categoria);
}
