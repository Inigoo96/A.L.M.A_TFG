-- =====================================================
-- FASE 1: CONSOLIDACIÓN DE GESTIÓN Y ROLES
-- Migración V3 - Gestión de estado de organizaciones y auditoría
-- =====================================================

-- =====================================================
-- 1. AÑADIR ESTADO DE ORGANIZACIÓN
-- =====================================================

-- Añadir columna ESTADO a la tabla ORGANIZACION
ALTER TABLE ORGANIZACION
ADD COLUMN ESTADO VARCHAR(20) DEFAULT 'ACTIVA';

-- Añadir constraint de validación para ESTADO
ALTER TABLE ORGANIZACION
ADD CONSTRAINT CK_ESTADO_ORG
CHECK (ESTADO IN ('ACTIVA', 'SUSPENDIDA', 'BAJA'));

-- Crear índice para optimizar búsquedas por estado
CREATE INDEX idx_organizacion_estado_activo ON ORGANIZACION(ESTADO);

-- Comentario explicativo
COMMENT ON COLUMN ORGANIZACION.ESTADO IS 'Estado operativo de la organización: ACTIVA (operando normalmente), SUSPENDIDA (temporalmente deshabilitada), BAJA (definitivamente deshabilitada, NUNCA se elimina)';

-- =====================================================
-- 2. TABLA DE AUDITORÍA DE ACCIONES ADMINISTRATIVAS
-- =====================================================

CREATE TABLE AUDITORIA_ADMIN (
    ID_AUDITORIA SERIAL,

    -- Usuario que realiza la acción (debe ser SUPER_ADMIN)
    ID_USUARIO_ADMIN INTEGER NOT NULL,

    -- Tipo de acción realizada
    TIPO_ACCION VARCHAR(50) NOT NULL,

    -- Tabla y registro afectado
    TABLA_AFECTADA VARCHAR(50),
    ID_REGISTRO_AFECTADO INTEGER,

    -- Datos del cambio (JSON para flexibilidad)
    DATOS_ANTERIORES JSONB,
    DATOS_NUEVOS JSONB,

    -- Justificación de la acción
    MOTIVO TEXT,

    -- Trazabilidad técnica
    IP_ORIGEN VARCHAR(45),

    -- Timestamp
    FECHA_ACCION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_AUDITORIA_ADMIN PRIMARY KEY (ID_AUDITORIA),
    CONSTRAINT FK_AUDITORIA_USUARIO FOREIGN KEY (ID_USUARIO_ADMIN)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE RESTRICT ON UPDATE CASCADE,

    -- Validación de tipos de acción permitidos
    CONSTRAINT CK_TIPO_ACCION CHECK (TIPO_ACCION IN (
        'VERIFICAR_ORGANIZACION',
        'RECHAZAR_ORGANIZACION',
        'SUSPENDER_ORGANIZACION',
        'ACTIVAR_ORGANIZACION',
        'DAR_BAJA_ORGANIZACION',
        'MODIFICAR_ORGANIZACION',
        'CREAR_SUPER_ADMIN',
        'ELIMINAR_USUARIO',
        'MODIFICAR_PERMISOS'
    ))
);

-- Índices para optimización de consultas de auditoría
CREATE INDEX idx_auditoria_usuario ON AUDITORIA_ADMIN(ID_USUARIO_ADMIN);
CREATE INDEX idx_auditoria_fecha ON AUDITORIA_ADMIN(FECHA_ACCION);
CREATE INDEX idx_auditoria_tipo ON AUDITORIA_ADMIN(TIPO_ACCION);
CREATE INDEX idx_auditoria_tabla ON AUDITORIA_ADMIN(TABLA_AFECTADA, ID_REGISTRO_AFECTADO);

-- Comentarios explicativos
COMMENT ON TABLE AUDITORIA_ADMIN IS 'Registro de auditoría de todas las acciones críticas realizadas por SUPER_ADMIN';
COMMENT ON COLUMN AUDITORIA_ADMIN.TIPO_ACCION IS 'Tipo de acción administrativa realizada';
COMMENT ON COLUMN AUDITORIA_ADMIN.DATOS_ANTERIORES IS 'Estado anterior del registro en formato JSON';
COMMENT ON COLUMN AUDITORIA_ADMIN.DATOS_NUEVOS IS 'Nuevo estado del registro en formato JSON';
COMMENT ON COLUMN AUDITORIA_ADMIN.MOTIVO IS 'Justificación proporcionada por el administrador para la acción';
COMMENT ON COLUMN AUDITORIA_ADMIN.IP_ORIGEN IS 'Dirección IP desde donde se realizó la acción';

-- =====================================================
-- 3. VISTA DE AUDITORÍA COMPLETA
-- =====================================================

CREATE OR REPLACE VIEW v_auditoria_completa AS
SELECT
    a.ID_AUDITORIA,
    a.TIPO_ACCION,
    a.TABLA_AFECTADA,
    a.ID_REGISTRO_AFECTADO,
    a.DATOS_ANTERIORES,
    a.DATOS_NUEVOS,
    a.MOTIVO,
    a.IP_ORIGEN,
    a.FECHA_ACCION,
    u.ID_USUARIO AS ID_ADMIN,
    u.EMAIL AS EMAIL_ADMIN,
    u.NOMBRE AS NOMBRE_ADMIN,
    u.APELLIDOS AS APELLIDOS_ADMIN,
    o.NOMBRE_OFICIAL AS ORGANIZACION_ADMIN
FROM AUDITORIA_ADMIN a
INNER JOIN USUARIO u ON a.ID_USUARIO_ADMIN = u.ID_USUARIO
INNER JOIN ORGANIZACION o ON u.ID_ORGANIZACION = o.ID_ORGANIZACION
ORDER BY a.FECHA_ACCION DESC;

COMMENT ON VIEW v_auditoria_completa IS 'Vista completa de auditoría con información del administrador que realizó la acción';

-- =====================================================
-- 4. FUNCIÓN PARA REGISTRAR AUDITORÍA AUTOMÁTICA
-- =====================================================

CREATE OR REPLACE FUNCTION registrar_auditoria_organizacion()
RETURNS TRIGGER AS $$
BEGIN
    -- Solo registrar si cambió el estado o la verificación
    IF (OLD.ESTADO IS DISTINCT FROM NEW.ESTADO) OR
       (OLD.ESTADO_VERIFICACION IS DISTINCT FROM NEW.ESTADO_VERIFICACION) THEN

        INSERT INTO AUDITORIA_ADMIN (
            ID_USUARIO_ADMIN,
            TIPO_ACCION,
            TABLA_AFECTADA,
            ID_REGISTRO_AFECTADO,
            DATOS_ANTERIORES,
            DATOS_NUEVOS,
            MOTIVO
        ) VALUES (
            COALESCE(NULLIF(current_setting('app.current_user_id', TRUE), '')::INTEGER, 1),
            CASE
                WHEN OLD.ESTADO_VERIFICACION IS DISTINCT FROM NEW.ESTADO_VERIFICACION
                    AND NEW.ESTADO_VERIFICACION = 'VERIFICADA' THEN 'VERIFICAR_ORGANIZACION'
                WHEN OLD.ESTADO_VERIFICACION IS DISTINCT FROM NEW.ESTADO_VERIFICACION
                    AND NEW.ESTADO_VERIFICACION = 'RECHAZADA' THEN 'RECHAZAR_ORGANIZACION'
                WHEN OLD.ESTADO IS DISTINCT FROM NEW.ESTADO
                    AND NEW.ESTADO = 'SUSPENDIDA' THEN 'SUSPENDER_ORGANIZACION'
                WHEN OLD.ESTADO IS DISTINCT FROM NEW.ESTADO
                    AND NEW.ESTADO = 'ACTIVA' THEN 'ACTIVAR_ORGANIZACION'
                WHEN OLD.ESTADO IS DISTINCT FROM NEW.ESTADO
                    AND NEW.ESTADO = 'BAJA' THEN 'DAR_BAJA_ORGANIZACION'
                ELSE 'MODIFICAR_ORGANIZACION'
            END,
            'ORGANIZACION',
            NEW.ID_ORGANIZACION,
            jsonb_build_object(
                'ESTADO', OLD.ESTADO,
                'ESTADO_VERIFICACION', OLD.ESTADO_VERIFICACION,
                'NOMBRE_OFICIAL', OLD.NOMBRE_OFICIAL
            ),
            jsonb_build_object(
                'ESTADO', NEW.ESTADO,
                'ESTADO_VERIFICACION', NEW.ESTADO_VERIFICACION,
                'NOMBRE_OFICIAL', NEW.NOMBRE_OFICIAL
            ),
            COALESCE(NEW.MOTIVO_RECHAZO, 'Cambio de estado')
        );
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION registrar_auditoria_organizacion() IS 'Función trigger para registrar automáticamente cambios en organizaciones';

-- =====================================================
-- 5. TRIGGER PARA AUDITORÍA AUTOMÁTICA
-- =====================================================

CREATE TRIGGER trigger_auditoria_organizacion
    AFTER UPDATE ON ORGANIZACION
    FOR EACH ROW
    EXECUTE FUNCTION registrar_auditoria_organizacion();

COMMENT ON TRIGGER trigger_auditoria_organizacion ON ORGANIZACION IS 'Registra automáticamente cambios de estado y verificación en auditoría';

-- =====================================================
-- 6. ACTUALIZAR ORGANIZACIONES EXISTENTES
-- =====================================================

-- Establecer todas las organizaciones existentes como ACTIVA por defecto
UPDATE ORGANIZACION
SET ESTADO = 'ACTIVA'
WHERE ESTADO IS NULL;

-- =====================================================
-- 7. ÍNDICE COMPUESTO PARA CONSULTAS COMUNES
-- =====================================================

-- Índice para búsquedas de organizaciones activas y verificadas
CREATE INDEX idx_organizacion_estado_verificacion
ON ORGANIZACION(ESTADO, ESTADO_VERIFICACION)
WHERE ESTADO = 'ACTIVA' AND ESTADO_VERIFICACION = 'VERIFICADA';

COMMENT ON INDEX idx_organizacion_estado_verificacion IS 'Optimiza búsquedas de organizaciones operacionales (activas y verificadas)';

-- =====================================================
-- NOTAS DE SEGURIDAD Y USO
-- =====================================================

-- 1. GESTIÓN DE ESTADO:
--    - ACTIVA: La organización puede operar normalmente
--    - SUSPENDIDA: Suspensión temporal (ej: problemas de pago, investigación)
--    - BAJA: Baja definitiva (ej: cierre del centro, violación de términos)
--    IMPORTANTE: NUNCA se debe usar DELETE en organizaciones

-- 2. AUDITORÍA:
--    - Todas las acciones críticas del SUPER_ADMIN quedan registradas
--    - Los cambios incluyen datos antes/después en formato JSON
--    - Se registra la IP de origen para trazabilidad completa

-- 3. VALIDACIONES EN BACKEND:
--    - Solo SUPER_ADMIN puede cambiar estado de organizaciones
--    - Al suspender/dar de baja: validar que no afecte operaciones en curso
--    - Al dar de baja: NO eliminar datos, solo cambiar ESTADO a 'BAJA'

-- 4. TRIGGER AUTOMÁTICO:
--    - Registra automáticamente cambios de ESTADO y ESTADO_VERIFICACION
--    - Requiere configurar 'app.current_user_id' desde la aplicación
--    - Ejemplo desde Java: connection.createStatement().execute("SET app.current_user_id = " + userId);

-- =====================================================
-- FIN DE MIGRACIÓN FASE 1
-- =====================================================