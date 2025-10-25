package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "FASE_DUELO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FaseDuelo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_FASE")
    private Integer id;

    @Column(name = "NOMBRE", nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ORDEN_FASE", nullable = false, unique = true)
    private Integer ordenFase;

    @OneToMany(mappedBy = "faseDuelo", fetch = FetchType.LAZY)
    @JsonManagedReference("fase-duelo-progresos")
    private List<ProgresoDuelo> progresosDuelo;
}