package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Podcast;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PodcastRepository extends JpaRepository<Podcast, Integer> {
    List<Podcast> findByActivoTrue();
    List<Podcast> findByFaseDueloRecomendadaIdAndActivoTrue(Integer faseDueloId);
    List<Podcast> findByCategoriaAndActivoTrue(String categoria);
}
