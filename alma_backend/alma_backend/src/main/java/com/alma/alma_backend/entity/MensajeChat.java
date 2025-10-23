package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE_CHAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MENSAJE")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_SESION_CHAT", nullable = false)
    private SesionChat sesionChat;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REMITENTE", nullable = false)
    private Usuario remitente;

    @Column(name = "MENSAJE", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "FECHA_ENVIO", nullable = false, updatable = false)
    private LocalDateTime fechaEnvio;

    @Column(name = "LEIDO")
    private Boolean leido = false;

    @Column(name = "FECHA_LECTURA")
    private LocalDateTime fechaLectura;

    @PrePersist
    protected void onCreate() {
        fechaEnvio = LocalDateTime.now();
    }
}