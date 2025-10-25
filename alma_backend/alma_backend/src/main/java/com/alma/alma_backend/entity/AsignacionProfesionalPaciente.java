package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

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
    @JsonBackReference("profesional-asignacion")
    private Profesional profesional;

    @ManyToOne
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    @JsonBackReference("paciente-asignacion")
    private Paciente paciente;

    @Column(name = "ES_PRINCIPAL")
    @ColumnDefault("true")
    private Boolean esPrincipal;

    @Column(name = "FECHA_ASIGNACION", updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaAsignacion;

    @Column(name = "activo", nullable = false)
    @ColumnDefault("true")
    private Boolean activo;

    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;

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
        if (fechaUltimaModificacion == null) {
            fechaUltimaModificacion = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }
}