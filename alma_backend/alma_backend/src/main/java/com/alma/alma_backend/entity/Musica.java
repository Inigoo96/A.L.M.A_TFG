package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MUSICA")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Musica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MUSICA")
    private Integer id;

    @Column(name = "TITULO", nullable = false, length = 150)
    private String titulo;

    @Column(name = "ARTISTA", length = 100)
    private String artista;

    @Column(name = "URL", nullable = false, length = 255)
    private String url;

    @Column(name = "DURACION_MINUTOS")
    private Integer duracionMinutos;

    @Column(name = "GENERO", length = 50)
    private String genero;

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
