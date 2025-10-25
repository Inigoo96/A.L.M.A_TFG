package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE_FORO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeForo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MENSAJE_FORO")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FORO", nullable = false)
    private Foro foro;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO", nullable = false)
    private Usuario usuario;

    @Column(name = "MENSAJE", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "FECHA_PUBLICACION", updatable = false)
    private LocalDateTime fechaPublicacion;

    @Column(name = "EDITADO")
    private Boolean editado = false;

    @Column(name = "FECHA_EDICION")
    private LocalDateTime fechaEdicion;

    @Column(name = "MODERADO")
    private Boolean moderado = false;

    @Column(name = "MOTIVO_MODERACION", columnDefinition = "TEXT")
    private String motivoModeracion;

    @PrePersist
    protected void onCreate() {
        fechaPublicacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        if (editado != null && editado) {
            fechaEdicion = LocalDateTime.now();
        }
    }
}
