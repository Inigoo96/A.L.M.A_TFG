package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.TipoUsuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class ProfesionalDetalleDTO {

    // Datos del perfil Profesional
    private Integer idProfesional;
    private String numeroColegiado;
    private String especialidad;
    private String centroSalud;

    // Datos del Usuario
    private Integer idUsuario;
    private String email;
    private String nombre;
    private String apellidos;
    private TipoUsuario tipoUsuario;
    private Boolean activo;
    private LocalDateTime fechaRegistro;
    private LocalDateTime ultimoAcceso;

    // Datos de la Organización
    private Integer idOrganizacion;
    private String nombreOrganizacion;
    private String cifOrganizacion;

    public ProfesionalDetalleDTO(Integer idProfesional, String nombre, String apellidos, String email,
                                  String numeroColegiado, String especialidad, String centroSalud, Boolean activo) {
        this.idProfesional = idProfesional;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
        this.centroSalud = centroSalud;
        this.activo = activo;
    }

    /**
     * Constructor completo para queries JPQL que traen todos los datos.
     */
    public ProfesionalDetalleDTO(
            Integer idProfesional, String numeroColegiado, String especialidad, String centroSalud,
            Integer idUsuario, String email, String nombre, String apellidos, TipoUsuario tipoUsuario,
            Boolean activo, LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso,
            Integer idOrganizacion, String nombreOrganizacion, String cifOrganizacion) {
        this.idProfesional = idProfesional;
        this.numeroColegiado = numeroColegiado;
        this.especialidad = especialidad;
        this.centroSalud = centroSalud;
        this.idUsuario = idUsuario;
        this.email = email;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.tipoUsuario = tipoUsuario;
        this.activo = activo;
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
        this.cifOrganizacion = cifOrganizacion;
    }

    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") +
               (apellidos != null ? " " + apellidos : "");
    }
}
