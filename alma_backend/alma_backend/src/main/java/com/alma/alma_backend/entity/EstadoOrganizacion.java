package com.alma.alma_backend.entity;

/**
 * Enumeración que representa el estado operativo de una organización en el sistema.
 *
 * Estados disponibles:
 * - ACTIVA: La organización está operando normalmente y puede crear usuarios
 * - SUSPENDIDA: Suspensión temporal (ej: problemas de pago, investigación)
 * - BAJA: Baja definitiva (ej: cierre del centro, violación de términos)
 *
 * IMPORTANTE: Las organizaciones NUNCA se eliminan físicamente de la base de datos,
 * solo se cambia su estado a BAJA para mantener la trazabilidad histórica.
 */
public enum EstadoOrganizacion {
    /**
     * Organización activa y operacional.
     * Puede crear usuarios, gestionar pacientes y profesionales normalmente.
     */
    ACTIVA,

    /**
     * Organización temporalmente suspendida.
     * No puede crear nuevos usuarios ni realizar operaciones críticas.
     * Los usuarios existentes pueden seguir accediendo (a definir según política).
     */
    SUSPENDIDA,

    /**
     * Organización dada de baja definitivamente.
     * No puede realizar ninguna operación.
     * Los datos históricos se mantienen para cumplir con regulaciones.
     */
    BAJA
}