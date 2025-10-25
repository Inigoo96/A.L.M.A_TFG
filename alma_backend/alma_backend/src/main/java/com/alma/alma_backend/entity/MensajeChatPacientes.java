package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "MENSAJE_CHAT_PACIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensajeChatPacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_MENSAJE")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CHAT_PACIENTES", nullable = false)
    private ChatPacientes chatPacientes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_REMITENTE_PACIENTE", nullable = false)
    private Paciente remitente;

    @Column(name = "MENSAJE", nullable = false, columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "FECHA_ENVIO", updatable = false)
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
