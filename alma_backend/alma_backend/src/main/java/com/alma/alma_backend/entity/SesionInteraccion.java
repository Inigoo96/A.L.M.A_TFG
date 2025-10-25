package com.alma.alma_backend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SESION_INTERACCION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionInteraccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_SESION")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROFESIONAL")
    private Profesional profesional;

    @Column(name = "FECHA_INICIO", nullable = false, updatable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "FECHA_FIN")
    private LocalDateTime fechaFin;

    @Column(name = "DURACION_SEGUNDOS")
    private Integer duracionSegundos;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_SESION", length = 20)
    private TipoSesion tipoSesion = TipoSesion.CONVERSACION;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", length = 20)
    private EstadoSesion estado = EstadoSesion.ACTIVA;

    @Column(name = "NUMERO_MENSAJES")
    private Integer numeroMensajes = 0;

    @Column(name = "SATISFACCION")
    private Integer satisfaccion;

    @Column(name = "NOTAS_PROFESIONAL", columnDefinition = "TEXT")
    private String notasProfesional;

    @Column(name = "FECHA_ULTIMA_MODIFICACION")
    private LocalDateTime fechaUltimaModificacion;

    // Nuevos campos para Fase 4
    @Column(name = "ESTADO_EMOCIONAL_DETECTADO", length = 50)
    private String estadoEmocionalDetectado;

    @Column(name = "TEMAS_CONVERSADOS", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> temasConversados;

    @Column(name = "ALERTAS_GENERADAS", columnDefinition = "TEXT[]")
    @JdbcTypeCode(SqlTypes.ARRAY)
    private List<String> alertasGeneradas;

    // Relación con mensajes de IA
    @OneToMany(mappedBy = "sesion", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonManagedReference("sesion-interaccion-mensajes")
    private List<MensajeIA> mensajes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        fechaInicio = LocalDateTime.now();
        fechaUltimaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }
}