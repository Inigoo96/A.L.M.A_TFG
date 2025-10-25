package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Entity
@Table(name = "INFORME_EMOCIONAL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InformeEmocional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_INFORME")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PACIENTE", nullable = false)
    private Paciente paciente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_PROFESIONAL", nullable = false)
    private Profesional profesional;

    @Column(name = "PERIODO_INICIO", nullable = false)
    private LocalDate periodoInicio;

    @Column(name = "PERIODO_FIN", nullable = false)
    private LocalDate periodoFin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "CONTENIDO_INFORME", columnDefinition = "jsonb")
    private Map<String, Object> contenidoInforme;

    @Enumerated(EnumType.STRING)
    @Column(name = "GENERADO_POR", length = 20)
    private GeneradoPor generadoPor = GeneradoPor.AUTOMATICO;

    @Column(name = "FECHA_GENERACION", updatable = false)
    private LocalDateTime fechaGeneracion;

    @PrePersist
    protected void onCreate() {
        fechaGeneracion = LocalDateTime.now();
    }
}
