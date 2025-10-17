package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ASIGNACION_PROFESIONAL_PACIENTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AsignacionProfesionalPaciente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ASIGNACION")
    private Integer idAsignacion;

    @ManyToOne
    @JoinColumn(name = "ID_PROFESIONAL", nullable = false)
    private Profesional profesional;

    @ManyToOne
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @Column(name = "ES_PRINCIPAL")
    private Boolean esPrincipal = true;

    @Column(name = "FECHA_ASIGNACION")
    private LocalDateTime fechaAsignacion;

    @Column(name = "ACTIVO")
    private Boolean activo = true;

    @PrePersist
    protected void onCreate() {
        if (fechaAsignacion == null) {
            fechaAsignacion = LocalDateTime.now();
        }
        if (esPrincipal == null) {
            esPrincipal = true;
        }
        if (activo == null) {
            activo = true;
        }
    }
}