package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "VIDEO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Video {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_VIDEO")
    private Integer id;

    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Column(name = "DESCRIPCION", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "URL", nullable = false, length = 255)
    private String url;

    @Column(name = "DURACION_MINUTOS")
    private Integer duracionMinutos;

    @Column(name = "CATEGORIA", length = 50)
    private String categoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FASE_DUELO_RECOMENDADA")
    private FaseDuelo faseDueloRecomendada;

    @Column(name = "ACTIVO")
    private Boolean activo = true;

    @Column(name = "FECHA_PUBLICACION", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaPublicacion;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }
}
