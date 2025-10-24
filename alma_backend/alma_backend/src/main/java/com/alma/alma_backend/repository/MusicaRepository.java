package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Musica;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MusicaRepository extends JpaRepository<Musica, Integer> {
    List<Musica> findByActivoTrue();
    List<Musica> findByFaseDueloRecomendadaIdAndActivoTrue(Integer faseDueloId);
    List<Musica> findByGeneroAndActivoTrue(String genero);
}
