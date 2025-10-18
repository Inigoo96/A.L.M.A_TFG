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
    private Integer idProfesional;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_ORGANIZACION", nullable = false)
    private Organizacion organizacion;

    @Column(name = "NUMERO_COLEGIADO", unique = true, length = 20)
    private String numeroColegiado;

    @Column(name = "ESPECIALIDAD", length = 100)
    private String especialidad;

    @OneToMany(mappedBy = "profesional", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<AsignacionProfesionalPaciente> asignaciones;
}