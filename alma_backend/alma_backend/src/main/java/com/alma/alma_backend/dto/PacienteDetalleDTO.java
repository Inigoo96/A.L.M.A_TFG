package com.alma.alma_backend.dto;

import com.alma.alma_backend.entity.Genero;
import com.alma.alma_backend.entity.TipoUsuario;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

/**
 * DTO que representa la información completa de un paciente,
 * incluyendo datos de usuario y organización sin problemas de lazy loading.
 *
 * Útil para respuestas de API que necesitan todos los datos del paciente
 * sin exponer información sensible como el password.
 */
@Data
@NoArgsConstructor
public class PacienteDetalleDTO {

    // Datos del perfil Paciente
    private Integer idPaciente;
    private String tarjetaSanitaria;
    private LocalDate fechaNacimiento;
    private Genero genero;

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

    /**
     * Constructor simplificado para queries básicas que solo necesitan datos del paciente y usuario.
     * Este constructor es usado por las queries JPQL en PacienteRepository.
     *
     * @param idPaciente ID del paciente
     * @param nombre Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param email Email del usuario
     * @param tarjetaSanitaria Tarjeta sanitaria del paciente
     * @param fechaNacimiento Fecha de nacimiento del paciente
     * @param genero Género del paciente
     * @param activo Estado activo del usuario
     */
    public PacienteDetalleDTO(Integer idPaciente, String nombre, String apellidos, String email,
                               String tarjetaSanitaria, LocalDate fechaNacimiento, Genero genero, Boolean activo) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tarjetaSanitaria = tarjetaSanitaria;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.activo = activo;
        // Los demás campos se dejan como null
    }

    /**
     * Constructor completo para queries que necesitan todos los datos del paciente, usuario y organización.
     * Este constructor es usado por queries JPQL más complejas.
     *
     * @param idPaciente ID del paciente
     * @param nombre Nombre del usuario
     * @param apellidos Apellidos del usuario
     * @param email Email del usuario
     * @param tarjetaSanitaria Tarjeta sanitaria del paciente
     * @param fechaNacimiento Fecha de nacimiento del paciente
     * @param genero Género del paciente
     * @param activo Estado activo del usuario
     * @param idUsuario ID del usuario
     * @param tipoUsuario Tipo de usuario
     * @param fechaRegistro Fecha de registro del usuario
     * @param ultimoAcceso Último acceso del usuario
     * @param idOrganizacion ID de la organización
     * @param nombreOrganizacion Nombre de la organización
     * @param cifOrganizacion CIF de la organización
     */
    public PacienteDetalleDTO(Integer idPaciente, String nombre, String apellidos, String email,
                               String tarjetaSanitaria, LocalDate fechaNacimiento, Genero genero, Boolean activo,
                               Integer idUsuario, TipoUsuario tipoUsuario, LocalDateTime fechaRegistro, LocalDateTime ultimoAcceso,
                               Integer idOrganizacion, String nombreOrganizacion, String cifOrganizacion) {
        this.idPaciente = idPaciente;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.email = email;
        this.tarjetaSanitaria = tarjetaSanitaria;
        this.fechaNacimiento = fechaNacimiento;
        this.genero = genero;
        this.activo = activo;
        this.idUsuario = idUsuario;
        this.tipoUsuario = tipoUsuario;
        this.fechaRegistro = fechaRegistro;
        this.ultimoAcceso = ultimoAcceso;
        this.idOrganizacion = idOrganizacion;
        this.nombreOrganizacion = nombreOrganizacion;
        this.cifOrganizacion = cifOrganizacion;
    }

    /**
     * Obtiene el nombre completo del paciente.
     *
     * @return Nombre completo (nombre + apellidos)
     */
    public String getNombreCompleto() {
        return (nombre != null ? nombre : "") +
               (apellidos != null ? " " + apellidos : "");
    }

    /**
     * Calcula la edad del paciente basándose en su fecha de nacimiento.
     *
     * @return Edad en años, o null si no hay fecha de nacimiento
     */
    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return Period.between(fechaNacimiento, LocalDate.now()).getYears();
    }
}