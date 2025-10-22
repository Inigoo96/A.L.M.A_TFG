package com.alma.alma_backend.entity;

/**
 * Enumeración que representa los tipos de acciones críticas
 * que deben ser auditadas en el sistema.
 *
 * Estas acciones son típicamente realizadas por SUPER_ADMIN
 * y quedan registradas en la tabla AUDITORIA_ADMIN.
 */
public enum TipoAccionAuditoria {
    /**
     * Verificación de una organización (cambio a VERIFICADA)
     */
    VERIFICAR_ORGANIZACION,

    /**
     * Rechazo de una organización (cambio a RECHAZADA)
     */
    RECHAZAR_ORGANIZACION,

    /**
     * Suspensión temporal de una organización
     */
    SUSPENDER_ORGANIZACION,

    /**
     * Activación de una organización suspendida
     */
    ACTIVAR_ORGANIZACION,

    /**
     * Baja definitiva de una organización
     */
    DAR_BAJA_ORGANIZACION,

    /**
     * Modificación de datos de una organización
     */
    MODIFICAR_ORGANIZACION,

    /**
     * Creación de un nuevo SUPER_ADMIN
     */
    CREAR_SUPER_ADMIN,

    /**
     * Eliminación de un usuario del sistema
     */
    ELIMINAR_USUARIO,

    /**
     * Modificación de permisos de un usuario
     */
    MODIFICAR_PERMISOS
}