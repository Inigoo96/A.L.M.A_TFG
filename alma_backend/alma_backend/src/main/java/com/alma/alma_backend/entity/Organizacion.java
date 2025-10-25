package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "ORGANIZACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORGANIZACION")
    private Integer id;

    @Column(name = "CIF", nullable = false, unique = true, length = 9)
    private String cif;

    @Column(name = "NUMERO_SEGURIDAD_SOCIAL", nullable = false, length = 20)
    private String numeroSeguridadSocial;

    @Column(name = "NOMBRE_OFICIAL", nullable = false, length = 255)
    private String nombreOficial;

    @Column(name = "DIRECCION", nullable = false, length = 255)
    private String direccion;

    @Column(name = "CODIGO_REGCESS", nullable = false, length = 50)
    private String codigoRegcess;

    @Column(name = "EMAIL_CORPORATIVO", nullable = false, length = 100)
    private String emailCorporativo;

    @Column(name = "TELEFONO_CONTACTO", nullable = false, length = 20)
    private String telefonoContacto;

    // --- Campos para verificación ---

    @Column(name = "DOCUMENTO_CIF_URL", length = 255)
    private String documentoCifUrl;

    @Column(name = "DOCUMENTO_SEGURIDAD_SOCIAL_URL", length = 255)
    private String documentoSeguridadSocialUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_VERIFICACION", nullable = false, length = 30)
    private EstadoVerificacion estadoVerificacion = EstadoVerificacion.PENDIENTE_VERIFICACION;

    @Column(name = "MOTIVO_RECHAZO", length = 500)
    private String motivoRechazo;

    // --- Estado operativo de la organización ---

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO", nullable = false, length = 20)
    private EstadoOrganizacion estado = EstadoOrganizacion.ACTIVA;

    @Column(name = "fecha_ultima_modificacion")
    private LocalDateTime fechaUltimaModificacion;

    @PrePersist
    protected void onCreate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaUltimaModificacion = LocalDateTime.now();
    }

}