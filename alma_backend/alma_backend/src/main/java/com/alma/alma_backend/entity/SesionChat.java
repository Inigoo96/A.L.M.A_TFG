package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "SESION_CHAT")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionChat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SESION_CHAT")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROFESIONAL", nullable = false)
    private Profesional profesional;

    @Column(name = "FECHA_CREACION", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ULTIMA_ACTIVIDAD")
    private LocalDateTime ultimaActividad;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20)
    private EstadoSesionChat estado = EstadoSesionChat.ACTIVA;

    @OneToMany(mappedBy = "sesionChat", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("sesion-chat-mensajes")
    private List<MensajeChat> mensajes;

    @PrePersist
    protected void onCreate() {
        fechaCreacion = LocalDateTime.now();
        ultimaActividad = LocalDateTime.now();
    }
}