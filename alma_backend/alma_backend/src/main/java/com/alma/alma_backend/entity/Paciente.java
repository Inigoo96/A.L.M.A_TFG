package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "PACIENTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Paciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_PACIENTE")
    private Integer idPaciente;

    @OneToOne
    @JoinColumn(name = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_ORGANIZACION", nullable = false)
    private Organizacion organizacion;

    @Column(name = "FECHA_NACIMIENTO")
    private LocalDate fechaNacimiento;

    @Column(name = "GENERO", length = 20)
    private String genero;

    @OneToMany(mappedBy = "paciente")
    private List<AsignacionProfesionalPaciente> asignaciones;
}