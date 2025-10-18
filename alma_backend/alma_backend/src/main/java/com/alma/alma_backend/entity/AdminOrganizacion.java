package com.alma.alma_backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ADMIN_ORGANIZACION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminOrganizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ADMIN")
    private Integer idAdmin;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "ID_USUARIO", referencedColumnName = "ID_USUARIO", nullable = false, unique = true)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "ID_ORGANIZACION", nullable = false)
    private Organizacion organizacion;
}