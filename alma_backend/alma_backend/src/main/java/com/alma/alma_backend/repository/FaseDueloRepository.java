package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.FaseDuelo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FaseDueloRepository extends JpaRepository<FaseDuelo, Integer> {

    /**
     * Busca todas las fases del duelo y las ordena por su campo 'ordenFase'.
     * @return Lista de entidades FaseDuelo ordenadas.
     */
    List<FaseDuelo> findAllByOrderByOrdenFaseAsc();
}
