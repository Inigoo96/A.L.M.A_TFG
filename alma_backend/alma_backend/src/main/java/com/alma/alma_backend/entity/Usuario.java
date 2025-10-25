package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_USUARIO")
    private Integer id;

    @Column(name = "DNI", nullable = false, unique = true, length = 15)
    private String dni;

    @Column(name = "EMAIL", nullable = false, unique = true, length = 100)
    private String email;

    @Column(name = "PASSWORD_HASH", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "NOMBRE", nullable = false, length = 50)
    private String nombre;

    @Column(name = "APELLIDOS", length = 100)
    private String apellidos;

    @Column(name = "TELEFONO", length = 20)
    private String telefono;

    @Enumerated(EnumType.STRING)
    @Column(name = "TIPO_USUARIO", nullable = false, length = 20)
    private TipoUsuario tipoUsuario;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ORGANIZACION", nullable = false)
    private Organizacion organizacion;

    @Column(name = "ACTIVO")
    @ColumnDefault("true")
    private Boolean activo;

    @Column(name = "FECHA_REGISTRO", updatable = false)
    @ColumnDefault("CURRENT_TIMESTAMP")
    private LocalDateTime fechaRegistro;

    @Column(name = "ULTIMO_ACCESO")
    private LocalDateTime ultimoAcceso;

    @Column(name = "PASSWORD_TEMPORAL")
    @ColumnDefault("true")
    private Boolean passwordTemporal;

    // --- Campos específicos para el Administrador que registra la organización ---
    @Column(name = "CARGO", length = 100)
    private String cargo;

    @Column(name = "DOCUMENTO_CARGO_URL", length = 255)
    private String documentoCargoUrl;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (passwordTemporal == null) {
            passwordTemporal = true;
        }
    }
}