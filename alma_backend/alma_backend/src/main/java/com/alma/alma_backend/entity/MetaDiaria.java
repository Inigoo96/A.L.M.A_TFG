package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "META_DIARIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDiaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_META")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @Column(name = "TEXTO_META", length = 255, nullable = false)
    private String textoMeta;

    @Column(name = "FECHA_ASIGNADA", nullable = false)
    private LocalDate fechaAsignada;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20)
    private EstadoMeta estado = EstadoMeta.PENDIENTE;

    @Column(name = "NOTAS", columnDefinition = "TEXT")
    private String notas;

    @Column(name = "FECHA_COMPLETADA")
    private LocalDateTime fechaCompletada;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "FECHA_ULTIMA_MODIFICACION")
    private LocalDateTime fechaUltimaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        fechaUltimaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();

        // Auto-actualizar fecha completada
        if (estado == EstadoMeta.COMPLETADA && fechaCompletada == null) {
            fechaCompletada = LocalDateTime.now();
        } else if (estado != EstadoMeta.COMPLETADA) {
            fechaCompletada = null;
        }
    }
}