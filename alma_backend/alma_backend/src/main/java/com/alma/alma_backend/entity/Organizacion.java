package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "ORGANIZACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Organizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ORGANIZACION")
    private Integer idOrganizacion;

    @Column(name = "NOMBRE_ORGANIZACION", nullable = false, length = 150)
    private String nombreOrganizacion;

    @Column(name = "CIF", nullable = false, unique = true, length = 9)
    private String cif;

    @Column(name = "EMAIL_CONTACTO", length = 100)
    private String emailContacto;

    @Column(name = "TELEFONO_CONTACTO", length = 15)
    private String telefonoContacto;

    @Column(name = "ACTIVA")
    private Boolean activa = true;

    @Column(name = "FECHA_ALTA")
    private LocalDateTime fechaAlta;

    @OneToMany(mappedBy = "organizacion")
    private List<Profesional> profesionales;

    @OneToMany(mappedBy = "organizacion")
    private List<Paciente> pacientes;

    @OneToMany(mappedBy = "organizacion")
    private List<AdminOrganizacion> administradores;

    @PrePersist
    protected void onCreate() {
        if (fechaAlta == null) {
            fechaAlta = LocalDateTime.now();
        }
        if (activa == null) {
            activa = true;
        }
    }
}