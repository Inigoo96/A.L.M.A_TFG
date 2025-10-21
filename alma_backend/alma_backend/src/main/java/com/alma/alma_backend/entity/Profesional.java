package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "PROFESIONAL")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Profesional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PROFESIONAL")
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario; // DNI, Email, Teléfono están en Usuario

    @Column(name = "NUMERO_COLEGIADO", unique = true, length = 20)
    private String numeroColegiado;

    @Column(name = "ESPECIALIDAD", length = 100)
    private String especialidad;

    @Column(name = "CENTRO_SALUD", length = 255) // Incluye el código REGCESS
    private String centroSalud;

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AsignacionProfesionalPaciente> asignaciones;
}