package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "CHAT_PACIENTES")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatPacientes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_CHAT_PACIENTES")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE_1", nullable = false)
    private Paciente paciente1;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE_2", nullable = false)
    private Paciente paciente2;

    @Column(name = "FECHA_CREACION", updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime fechaCreacion;

    @Column(name = "ULTIMA_ACTIVIDAD")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime ultimaActividad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20)
    private EstadoChatPacientes estado = EstadoChatPacientes.ACTIVO;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimaActividad = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ultimaActividad = LocalDateTime.now();
    }
}
