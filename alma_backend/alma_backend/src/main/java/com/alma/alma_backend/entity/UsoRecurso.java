package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "USO_RECURSO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UsoRecurso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USO")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_RECURSO", nullable = false, length = 20)
    private TipoRecurso tipoRecurso;

    @Column(name = "ID_RECURSO", nullable = false)
    private Integer idRecurso;

    @Column(name = "FECHA_USO", updatable = false)
    private LocalDateTime fechaUso;

    @Column(name = "TIEMPO_CONSUMIDO_MINUTOS")
    private Integer tiempoConsumidoMinutos;

    @Column(name = "VALORACION")
    private Integer valoracion;

    @PrePersist
    protected void onCreate() {
        fechaUso = LocalDateTime.now();
    }
}
