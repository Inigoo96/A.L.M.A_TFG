-- =====================================================
-- HOTFIX: Corregir trigger de validación de asignación para citas
-- Problema: El trigger usaba ACTIVA pero la columna real es ACTIVO
-- Fecha: 2025-10-25
-- =====================================================

-- Recrear la función con el nombre de columna correcto
CREATE OR REPLACE FUNCTION validar_asignacion_cita()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM ASIGNACION_PROFESIONAL_PACIENTE
        WHERE ID_PACIENTE = NEW.ID_PACIENTE
        AND ID_PROFESIONAL = NEW.ID_PROFESIONAL
        AND ACTIVO = TRUE  -- CORREGIDO: La columna en la BD es ACTIVO, no ACTIVA
    ) THEN
        RAISE EXCEPTION 'No existe asignación activa entre este profesional y paciente';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- =====================================================
-- FIN HOTFIX
-- =====================================================