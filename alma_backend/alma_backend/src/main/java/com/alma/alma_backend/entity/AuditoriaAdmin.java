package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Entidad que registra todas las acciones críticas realizadas por SUPER_ADMIN.
 *
 * Esta tabla es fundamental para:
 * - Cumplimiento normativo (RGPD, LOPD)
 * - Trazabilidad de cambios críticos
 * - Investigación de incidentes
 * - Auditorías internas y externas
 *
 * IMPORTANTE: Los registros de auditoría NUNCA deben eliminarse.
 */
@Entity
@Table(name = "AUDITORIA_ADMIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_AUDITORIA")
    private Integer id;

    /**
     * Usuario administrador que realizó la acción.
     * Debe ser un usuario con rol SUPER_ADMIN.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_USUARIO_ADMIN", nullable = false)
    private Usuario usuarioAdmin;

    /**
     * Tipo de acción administrativa realizada.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_ACCION", nullable = false, length = 50)
    private TipoAccionAuditoria tipoAccion;

    /**
     * Nombre de la tabla afectada por la acción.
     */
    @Column(name = "TABLA_AFECTADA", length = 50)
    private String tablaAfectada;

    /**
     * ID del registro afectado en la tabla correspondiente.
     */
    @Column(name = "ID_REGISTRO_AFECTADO")
    private Integer idRegistroAfectado;

    /**
     * Estado anterior del registro en formato JSON.
     * Permite comparar qué cambió exactamente.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DATOS_ANTERIORES", columnDefinition = "jsonb")
    private Map<String, Object> datosAnteriores;

    /**
     * Nuevo estado del registro en formato JSON.
     */
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "DATOS_NUEVOS", columnDefinition = "jsonb")
    private Map<String, Object> datosNuevos;

    /**
     * Justificación o motivo de la acción proporcionada por el administrador.
     */
    @Column(name = "MOTIVO", columnDefinition = "TEXT")
    private String motivo;

    /**
     * Dirección IP desde donde se realizó la acción.
     * Útil para detectar accesos no autorizados.
     */
    @Column(name = "IP_ORIGEN", length = 45)
    private String ipOrigen;

    /**
     * Fecha y hora en que se realizó la acción.
     */
    @Column(name = "FECHA_ACCION", nullable = false)
    private LocalDateTime fechaAccion = LocalDateTime.now();

    /**
     * Constructor de conveniencia para crear un registro de auditoría.
     *
     * @param usuarioAdmin Usuario que realiza la acción
     * @param tipoAccion Tipo de acción realizada
     * @param tablaAfectada Tabla afectada
     * @param idRegistroAfectado ID del registro afectado
     * @param datosAnteriores Estado anterior
     * @param datosNuevos Estado nuevo
     * @param motivo Justificación de la acción
     * @param ipOrigen IP de origen
     */
    public AuditoriaAdmin(Usuario usuarioAdmin, TipoAccionAuditoria tipoAccion,
                          String tablaAfectada, Integer idRegistroAfectado,
                          Map<String, Object> datosAnteriores, Map<String, Object> datosNuevos,
                          String motivo, String ipOrigen) {
        this.usuarioAdmin = usuarioAdmin;
        this.tipoAccion = tipoAccion;
        this.tablaAfectada = tablaAfectada;
        this.idRegistroAfectado = idRegistroAfectado;
        this.datosAnteriores = datosAnteriores;
        this.datosNuevos = datosNuevos;
        this.motivo = motivo;
        this.ipOrigen = ipOrigen;
        this.fechaAccion = LocalDateTime.now();
    }
}