package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE_IA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeIA {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MENSAJE_IA")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESION", nullable = false)
    @JsonBackReference("sesion-interaccion-mensajes")
    private SesionInteraccion sesion;

    @Enumerated(EnumType.STRING)
    @Column(name = "ROL", length = 20, nullable = false)
    private RolMensajeIA rol;

    @Column(name = "MENSAJE", columnDefinition = "TEXT", nullable = false)
    private String mensaje;

    @Column(name = "TIMESTAMP_MENSAJE", nullable = false, updatable = false)
    private LocalDateTime timestampMensaje;

    @Enumerated(EnumType.STRING)
    @Column(name = "SENTIMIENTO_DETECTADO", length = 30)
    private SentimientoDetectado sentimientoDetectado;

    @PrePersist
    protected void onCreate() {
        timestampMensaje = LocalDateTime.now();
    }
}