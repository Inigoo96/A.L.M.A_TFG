-- =====================================================
-- FASE 4: CHATBOT IA Y METAS DIARIAS
-- Implementación de asistente IA y herramientas de apoyo diario
-- Versión: 1.0
-- =====================================================

-- =====================================================
-- 1. AMPLIAR TABLA SESION_INTERACCION
-- Añadir campos para análisis de IA
-- =====================================================

-- Añadir columna para estado emocional detectado por IA
ALTER TABLE SESION_INTERACCION
ADD COLUMN ESTADO_EMOCIONAL_DETECTADO VARCHAR(50);

-- Añadir columna para temas conversados (array de texto)
ALTER TABLE SESION_INTERACCION
ADD COLUMN TEMAS_CONVERSADOS TEXT[];

-- Añadir columna para alertas generadas para el profesional
ALTER TABLE SESION_INTERACCION
ADD COLUMN ALERTAS_GENERADAS TEXT[];

-- Comentarios explicativos
COMMENT ON COLUMN SESION_INTERACCION.ESTADO_EMOCIONAL_DETECTADO IS 'Estado emocional detectado por IA durante la conversación';
COMMENT ON COLUMN SESION_INTERACCION.TEMAS_CONVERSADOS IS 'Array de temas principales detectados en la conversación';
COMMENT ON COLUMN SESION_INTERACCION.ALERTAS_GENERADAS IS 'Array de alertas generadas para el profesional (ej: ideación suicida, crisis)';

-- =====================================================
-- 2. TABLA: MENSAJE_IA
-- Registro de mensajes en conversaciones con IA
-- =====================================================

CREATE TABLE MENSAJE_IA (
    ID_MENSAJE_IA SERIAL,
    ID_SESION INTEGER NOT NULL,
    ROL VARCHAR(20) NOT NULL,
    MENSAJE TEXT NOT NULL,
    TIMESTAMP_MENSAJE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    SENTIMIENTO_DETECTADO VARCHAR(30),

    CONSTRAINT PK_MENSAJE_IA PRIMARY KEY (ID_MENSAJE_IA),
    CONSTRAINT FK_MENSAJE_SESION_IA FOREIGN KEY (ID_SESION)
        REFERENCES SESION_INTERACCION(ID_SESION) ON DELETE CASCADE,
    CONSTRAINT CK_ROL_IA CHECK (ROL IN ('USUARIO', 'ASISTENTE')),
    CONSTRAINT CK_SENTIMIENTO CHECK (SENTIMIENTO_DETECTADO IN ('POSITIVO', 'NEGATIVO', 'NEUTRO', 'MUY_POSITIVO', 'MUY_NEGATIVO'))
);

-- Índices para optimización
CREATE INDEX idx_mensaje_ia_sesion ON MENSAJE_IA(ID_SESION);
CREATE INDEX idx_mensaje_ia_timestamp ON MENSAJE_IA(TIMESTAMP_MENSAJE);
CREATE INDEX idx_mensaje_ia_sentimiento ON MENSAJE_IA(SENTIMIENTO_DETECTADO);

COMMENT ON TABLE MENSAJE_IA IS 'Mensajes de las conversaciones entre paciente y asistente IA';
COMMENT ON COLUMN MENSAJE_IA.ROL IS 'Rol del emisor: USUARIO (paciente) o ASISTENTE (IA)';
COMMENT ON COLUMN MENSAJE_IA.SENTIMIENTO_DETECTADO IS 'Sentimiento detectado por análisis de IA: POSITIVO, NEGATIVO, NEUTRO, MUY_POSITIVO, MUY_NEGATIVO';

-- =====================================================
-- 3. TABLA: META_DIARIA
-- Metas y objetivos diarios para el paciente
-- =====================================================

CREATE TABLE META_DIARIA (
    ID_META SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    TEXTO_META VARCHAR(255) NOT NULL,
    FECHA_ASIGNADA DATE NOT NULL,
    ESTADO VARCHAR(20) DEFAULT 'PENDIENTE',
    NOTAS TEXT,
    FECHA_COMPLETADA TIMESTAMP,
    FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_META_DIARIA PRIMARY KEY (ID_META),
    CONSTRAINT FK_META_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT CK_ESTADO_META CHECK (ESTADO IN ('PENDIENTE', 'COMPLETADA', 'CANCELADA'))
);

-- Índices para optimización
CREATE INDEX idx_meta_paciente ON META_DIARIA(ID_PACIENTE);
CREATE INDEX idx_meta_fecha ON META_DIARIA(FECHA_ASIGNADA);
CREATE INDEX idx_meta_estado ON META_DIARIA(ESTADO);
CREATE INDEX idx_meta_paciente_fecha ON META_DIARIA(ID_PACIENTE, FECHA_ASIGNADA);

COMMENT ON TABLE META_DIARIA IS 'Metas y objetivos diarios asignados a los pacientes';
COMMENT ON COLUMN META_DIARIA.TEXTO_META IS 'Descripción de la meta a cumplir';
COMMENT ON COLUMN META_DIARIA.ESTADO IS 'Estado de la meta: PENDIENTE, COMPLETADA, CANCELADA';
COMMENT ON COLUMN META_DIARIA.FECHA_ASIGNADA IS 'Fecha para la cual está asignada la meta';
COMMENT ON COLUMN META_DIARIA.FECHA_COMPLETADA IS 'Timestamp cuando se completó la meta';

-- =====================================================
-- 4. TRIGGERS PARA AUDITORÍA AUTOMÁTICA
-- =====================================================

-- Trigger para actualizar fecha_ultima_modificacion en META_DIARIA
CREATE TRIGGER trigger_meta_modificacion
BEFORE UPDATE ON META_DIARIA
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Trigger para actualizar fecha_completada cuando se marca como completada
CREATE OR REPLACE FUNCTION actualizar_fecha_completada_meta()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.ESTADO = 'COMPLETADA' AND OLD.ESTADO != 'COMPLETADA' THEN
        NEW.FECHA_COMPLETADA = CURRENT_TIMESTAMP;
    ELSIF NEW.ESTADO != 'COMPLETADA' THEN
        NEW.FECHA_COMPLETADA = NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_completar_meta
BEFORE UPDATE ON META_DIARIA
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_completada_meta();

COMMENT ON FUNCTION actualizar_fecha_completada_meta() IS 'Actualiza automáticamente la fecha de completado de una meta';

-- =====================================================
-- 5. TRIGGER PARA ACTUALIZAR CONTADOR DE MENSAJES
-- =====================================================

-- Trigger para actualizar numero_mensajes en SESION_INTERACCION
CREATE OR REPLACE FUNCTION actualizar_contador_mensajes_ia()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE SESION_INTERACCION
    SET NUMERO_MENSAJES = (
        SELECT COUNT(*)
        FROM MENSAJE_IA
        WHERE ID_SESION = NEW.ID_SESION
    )
    WHERE ID_SESION = NEW.ID_SESION;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_contador_mensajes_ia
AFTER INSERT ON MENSAJE_IA
FOR EACH ROW EXECUTE FUNCTION actualizar_contador_mensajes_ia();

COMMENT ON FUNCTION actualizar_contador_mensajes_ia() IS 'Actualiza el contador de mensajes en la sesión de interacción';

-- =====================================================
-- 6. VISTAS ÚTILES
-- =====================================================

-- Vista: Conversaciones con IA completas
CREATE OR REPLACE VIEW v_conversaciones_ia AS
SELECT
    si.ID_SESION,
    si.ID_PACIENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_PACIENTE,
    si.ID_PROFESIONAL,
    u_prof.NOMBRE || ' ' || u_prof.APELLIDOS AS NOMBRE_PROFESIONAL,
    si.FECHA_INICIO,
    si.FECHA_FIN,
    si.DURACION_SEGUNDOS,
    si.TIPO_SESION,
    si.ESTADO,
    si.NUMERO_MENSAJES,
    si.SATISFACCION,
    si.ESTADO_EMOCIONAL_DETECTADO,
    si.TEMAS_CONVERSADOS,
    si.ALERTAS_GENERADAS,
    org.NOMBRE_OFICIAL AS ORGANIZACION
FROM SESION_INTERACCION si
JOIN PACIENTE p ON si.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
LEFT JOIN PROFESIONAL prof ON si.ID_PROFESIONAL = prof.ID_PROFESIONAL
LEFT JOIN USUARIO u_prof ON prof.ID_USUARIO = u_prof.ID_USUARIO
JOIN ORGANIZACION org ON u.ID_ORGANIZACION = org.ID_ORGANIZACION
ORDER BY si.FECHA_INICIO DESC;

COMMENT ON VIEW v_conversaciones_ia IS 'Vista completa de conversaciones con IA incluyendo análisis emocional';

-- Vista: Mensajes de IA con contexto
CREATE OR REPLACE VIEW v_mensajes_ia_completos AS
SELECT
    mia.ID_MENSAJE_IA,
    mia.ID_SESION,
    mia.ROL,
    mia.MENSAJE,
    mia.TIMESTAMP_MENSAJE,
    mia.SENTIMIENTO_DETECTADO,
    si.ID_PACIENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_PACIENTE,
    si.TIPO_SESION,
    si.ESTADO AS ESTADO_SESION
FROM MENSAJE_IA mia
JOIN SESION_INTERACCION si ON mia.ID_SESION = si.ID_SESION
JOIN PACIENTE p ON si.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
ORDER BY mia.TIMESTAMP_MENSAJE ASC;

COMMENT ON VIEW v_mensajes_ia_completos IS 'Vista de mensajes de IA con información del paciente';

-- Vista: Metas diarias por paciente
CREATE OR REPLACE VIEW v_metas_paciente AS
SELECT
    md.ID_META,
    md.ID_PACIENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_PACIENTE,
    md.TEXTO_META,
    md.FECHA_ASIGNADA,
    md.ESTADO,
    md.NOTAS,
    md.FECHA_COMPLETADA,
    md.FECHA_CREACION,
    org.NOMBRE_OFICIAL AS ORGANIZACION
FROM META_DIARIA md
JOIN PACIENTE p ON md.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
JOIN ORGANIZACION org ON u.ID_ORGANIZACION = org.ID_ORGANIZACION
ORDER BY md.FECHA_ASIGNADA DESC, md.FECHA_CREACION DESC;

COMMENT ON VIEW v_metas_paciente IS 'Vista de metas diarias con información del paciente';

-- Vista: Estadísticas de metas por paciente
CREATE OR REPLACE VIEW v_estadisticas_metas_paciente AS
SELECT
    md.ID_PACIENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_PACIENTE,
    COUNT(*) AS TOTAL_METAS,
    SUM(CASE WHEN md.ESTADO = 'COMPLETADA' THEN 1 ELSE 0 END) AS METAS_COMPLETADAS,
    SUM(CASE WHEN md.ESTADO = 'PENDIENTE' THEN 1 ELSE 0 END) AS METAS_PENDIENTES,
    SUM(CASE WHEN md.ESTADO = 'CANCELADA' THEN 1 ELSE 0 END) AS METAS_CANCELADAS,
    ROUND(
        (SUM(CASE WHEN md.ESTADO = 'COMPLETADA' THEN 1 ELSE 0 END)::NUMERIC /
        NULLIF(COUNT(*), 0)) * 100, 2
    ) AS PORCENTAJE_COMPLETADO
FROM META_DIARIA md
JOIN PACIENTE p ON md.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
GROUP BY md.ID_PACIENTE, u.NOMBRE, u.APELLIDOS;

COMMENT ON VIEW v_estadisticas_metas_paciente IS 'Estadísticas de cumplimiento de metas por paciente';

-- Vista: Alertas generadas por IA
CREATE OR REPLACE VIEW v_alertas_ia AS
SELECT
    si.ID_SESION,
    si.ID_PACIENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_PACIENTE,
    u.EMAIL AS EMAIL_PACIENTE,
    si.ID_PROFESIONAL,
    u_prof.NOMBRE || ' ' || u_prof.APELLIDOS AS NOMBRE_PROFESIONAL,
    u_prof.EMAIL AS EMAIL_PROFESIONAL,
    si.FECHA_INICIO,
    si.ESTADO_EMOCIONAL_DETECTADO,
    si.ALERTAS_GENERADAS,
    si.TEMAS_CONVERSADOS,
    org.NOMBRE_OFICIAL AS ORGANIZACION
FROM SESION_INTERACCION si
JOIN PACIENTE p ON si.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
LEFT JOIN PROFESIONAL prof ON si.ID_PROFESIONAL = prof.ID_PROFESIONAL
LEFT JOIN USUARIO u_prof ON prof.ID_USUARIO = u_prof.ID_USUARIO
JOIN ORGANIZACION org ON u.ID_ORGANIZACION = org.ID_ORGANIZACION
WHERE si.ALERTAS_GENERADAS IS NOT NULL
  AND array_length(si.ALERTAS_GENERADAS, 1) > 0
ORDER BY si.FECHA_INICIO DESC;

COMMENT ON VIEW v_alertas_ia IS 'Vista de sesiones con alertas generadas por IA que requieren atención profesional';

-- =====================================================
-- 7. FUNCIÓN DE UTILIDAD PARA OBTENER METAS DE HOY
-- =====================================================

CREATE OR REPLACE FUNCTION obtener_metas_hoy(p_id_paciente INTEGER)
RETURNS TABLE(
    id_meta INTEGER,
    texto_meta VARCHAR(255),
    estado VARCHAR(20),
    notas TEXT,
    fecha_creacion TIMESTAMP
) AS $$
BEGIN
    RETURN QUERY
    SELECT
        md.ID_META,
        md.TEXTO_META,
        md.ESTADO,
        md.NOTAS,
        md.FECHA_CREACION
    FROM META_DIARIA md
    WHERE md.ID_PACIENTE = p_id_paciente
      AND md.FECHA_ASIGNADA = CURRENT_DATE
    ORDER BY md.FECHA_CREACION DESC;
END;
$$ LANGUAGE plpgsql;

COMMENT ON FUNCTION obtener_metas_hoy(INTEGER) IS 'Obtiene las metas del día actual para un paciente específico';

-- =====================================================
-- 8. ÍNDICES COMPUESTOS PARA CONSULTAS COMUNES
-- =====================================================

-- Índice para búsqueda de sesiones activas con alertas
CREATE INDEX idx_sesion_estado_alertas
ON SESION_INTERACCION(ESTADO, ID_PACIENTE)
WHERE ALERTAS_GENERADAS IS NOT NULL;

-- Índice para búsqueda de mensajes por sesión y rol
CREATE INDEX idx_mensaje_ia_sesion_rol ON MENSAJE_IA(ID_SESION, ROL);

-- =====================================================
-- 9. VALIDACIONES A NIVEL DE BD
-- =====================================================

-- Validar que la meta no esté duplicada para el mismo día
CREATE OR REPLACE FUNCTION validar_meta_duplicada()
RETURNS TRIGGER AS $$
DECLARE
    metas_existentes INTEGER;
BEGIN
    SELECT COUNT(*) INTO metas_existentes
    FROM META_DIARIA
    WHERE ID_PACIENTE = NEW.ID_PACIENTE
      AND FECHA_ASIGNADA = NEW.FECHA_ASIGNADA
      AND UPPER(TRIM(TEXTO_META)) = UPPER(TRIM(NEW.TEXTO_META))
      AND ID_META != COALESCE(NEW.ID_META, -1)
      AND ESTADO != 'CANCELADA';

    IF metas_existentes > 0 THEN
        RAISE EXCEPTION 'Ya existe una meta similar para este paciente en la fecha especificada';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validar_meta_duplicada
BEFORE INSERT OR UPDATE ON META_DIARIA
FOR EACH ROW EXECUTE FUNCTION validar_meta_duplicada();

COMMENT ON FUNCTION validar_meta_duplicada() IS 'Previene la creación de metas duplicadas para el mismo día';

-- =====================================================
-- NOTAS DE SEGURIDAD Y USO
-- =====================================================

-- 1. INTEGRACIÓN CON IA:
--    - Los mensajes se almacenan para análisis posterior
--    - El sentimiento detectado ayuda a identificar crisis
--    - Las alertas generadas notifican al profesional asignado

-- 2. PRIVACIDAD:
--    - Los mensajes con IA son confidenciales
--    - Solo el paciente y su profesional asignado pueden verlos
--    - Las alertas se envían únicamente al profesional asignado

-- 3. METAS DIARIAS:
--    - Pueden ser creadas por el paciente o el profesional
--    - No se pueden duplicar metas para el mismo día
--    - Las metas completadas registran timestamp automático

-- 4. ANÁLISIS EMOCIONAL:
--    - ESTADO_EMOCIONAL_DETECTADO se actualiza al finalizar sesión
--    - TEMAS_CONVERSADOS se extrae mediante NLP de la IA
--    - ALERTAS_GENERADAS incluye: ideación suicida, crisis, etc.

-- 5. VALIDACIONES EN BACKEND:
--    - Verificar que el paciente tenga asignación activa
--    - Limitar número de mensajes por sesión (ej: 100 mensajes)
--    - Validar longitud de mensajes (ej: máximo 2000 caracteres)
--    - Implementar rate limiting para prevenir abuso

-- =====================================================
-- FIN DE MIGRACIÓN FASE 4
-- =====================================================