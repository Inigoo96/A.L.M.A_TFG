package com.alma.alma_backend.repository;

import com.alma.alma_backend.entity.Organizacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizacionRepository extends JpaRepository<Organizacion, Integer> {

    /**
     * Busca una organización por su Código de Identificación Fiscal (CIF).
     * @param cif El CIF de la organización a buscar.
     * @return Un Optional que contiene la organización si se encuentra, o vacío si no.
     */
    Optional<Organizacion> findByCif(String cif);

}
