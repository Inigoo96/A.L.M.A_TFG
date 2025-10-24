-- =====================================================
-- FASE 1: CORRECCIÓN DE AUDITORÍA DUPLICADA
-- Migración V4 - Eliminar trigger de auditoría de la base de datos
-- =====================================================

-- MOTIVO:
-- La auditoría de cambios de estado de organización se gestiona
-- desde la capa de aplicación (backend Java), que es más precisa
-- y contiene más detalles (IP de origen, motivo específico, etc.).
-- El trigger en la base de datos genera registros duplicados e
-- incompletos, por lo que debe ser eliminado para tener una
-- única fuente de verdad.

-- PASO 1: Eliminar el trigger de la tabla ORGANIZACION
DROP TRIGGER IF EXISTS trigger_auditoria_organizacion ON ORGANIZACION;

-- PASO 2: Eliminar la función asociada al trigger
DROP FUNCTION IF EXISTS registrar_auditoria_organizacion();

-- =====================================================
-- FIN DE LA CORRECCIÓN
-- =====================================================
