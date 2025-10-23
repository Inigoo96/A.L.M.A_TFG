-- =====================================================
-- FASE 3: CHAT PROFESIONAL-PACIENTE
-- Implementación de comunicación directa entre profesional y paciente
-- Versión: 1.0
-- =====================================================

-- =====================================================
-- TABLA: SESION_CHAT
-- Agrupa conversaciones entre profesional y paciente
-- =====================================================
CREATE TABLE SESION_CHAT (
    ID_SESION_CHAT SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    ID_PROFESIONAL INTEGER NOT NULL,
    FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ULTIMA_ACTIVIDAD TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ESTADO VARCHAR(20) DEFAULT 'ACTIVA',

    CONSTRAINT PK_SESION_CHAT PRIMARY KEY (ID_SESION_CHAT),
    CONSTRAINT FK_SESION_CHAT_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT FK_SESION_CHAT_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE,
    CONSTRAINT CK_ESTADO_SESION CHECK (ESTADO IN ('ACTIVA', 'ARCHIVADA'))
);

-- Índices para optimización
CREATE INDEX idx_sesion_chat_paciente ON SESION_CHAT(ID_PACIENTE);
CREATE INDEX idx_sesion_chat_profesional ON SESION_CHAT(ID_PROFESIONAL);
CREATE INDEX idx_sesion_chat_estado ON SESION_CHAT(ESTADO);
CREATE INDEX idx_sesion_chat_ultima_actividad ON SESION_CHAT(ULTIMA_ACTIVIDAD);

COMMENT ON TABLE SESION_CHAT IS 'Sesiones de conversación entre profesional y paciente';
COMMENT ON COLUMN SESION_CHAT.ESTADO IS 'Estado de la sesión: ACTIVA, ARCHIVADA';
COMMENT ON COLUMN SESION_CHAT.ULTIMA_ACTIVIDAD IS 'Timestamp del último mensaje enviado';

-- =====================================================
-- TABLA: MENSAJE_CHAT
-- Mensajes individuales dentro de una sesión de chat
-- =====================================================
CREATE TABLE MENSAJE_CHAT (
    ID_MENSAJE SERIAL,
    ID_SESION_CHAT INTEGER NOT NULL,
    ID_REMITENTE INTEGER NOT NULL,
    MENSAJE TEXT NOT NULL,
    FECHA_ENVIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LEIDO BOOLEAN DEFAULT FALSE,
    FECHA_LECTURA TIMESTAMP,

    CONSTRAINT PK_MENSAJE_CHAT PRIMARY KEY (ID_MENSAJE),
    CONSTRAINT FK_MENSAJE_SESION FOREIGN KEY (ID_SESION_CHAT)
        REFERENCES SESION_CHAT(ID_SESION_CHAT) ON DELETE CASCADE,
    CONSTRAINT FK_MENSAJE_REMITENTE FOREIGN KEY (ID_REMITENTE)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE
);

-- Índices para optimización
CREATE INDEX idx_mensaje_sesion ON MENSAJE_CHAT(ID_SESION_CHAT);
CREATE INDEX idx_mensaje_fecha ON MENSAJE_CHAT(FECHA_ENVIO);
CREATE INDEX idx_mensaje_leido ON MENSAJE_CHAT(LEIDO);
CREATE INDEX idx_mensaje_remitente ON MENSAJE_CHAT(ID_REMITENTE);

COMMENT ON TABLE MENSAJE_CHAT IS 'Mensajes enviados en las sesiones de chat profesional-paciente';
COMMENT ON COLUMN MENSAJE_CHAT.ID_REMITENTE IS 'ID del usuario (profesional o paciente) que envió el mensaje';
COMMENT ON COLUMN MENSAJE_CHAT.LEIDO IS 'Indica si el mensaje ha sido leído por el destinatario';

-- =====================================================
-- TRIGGERS
-- =====================================================

-- Trigger para actualizar ultima_actividad cuando se envía un mensaje
CREATE OR REPLACE FUNCTION actualizar_ultima_actividad_chat()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE SESION_CHAT
    SET ULTIMA_ACTIVIDAD = CURRENT_TIMESTAMP
    WHERE ID_SESION_CHAT = NEW.ID_SESION_CHAT;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_mensaje_actividad
AFTER INSERT ON MENSAJE_CHAT
FOR EACH ROW EXECUTE FUNCTION actualizar_ultima_actividad_chat();

-- Trigger para actualizar fecha_lectura cuando se marca como leído
CREATE OR REPLACE FUNCTION actualizar_fecha_lectura()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.LEIDO = TRUE AND OLD.LEIDO = FALSE THEN
        NEW.FECHA_LECTURA = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_marcar_leido
BEFORE UPDATE ON MENSAJE_CHAT
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_lectura();

-- =====================================================
-- VALIDACIONES A NIVEL DE BD
-- =====================================================

-- Validar que solo pueden chatear si existe asignación activa
CREATE OR REPLACE FUNCTION validar_asignacion_chat()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM ASIGNACION_PROFESIONAL_PACIENTE
        WHERE ID_PACIENTE = NEW.ID_PACIENTE
        AND ID_PROFESIONAL = NEW.ID_PROFESIONAL
        AND ACTIVO = TRUE
    ) THEN
        RAISE EXCEPTION 'No existe asignación activa entre este profesional y paciente';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validar_chat_asignacion
BEFORE INSERT ON SESION_CHAT
FOR EACH ROW EXECUTE FUNCTION validar_asignacion_chat();

-- Validar que el remitente pertenece a la sesión de chat
CREATE OR REPLACE FUNCTION validar_remitente_mensaje()
RETURNS TRIGGER AS $$
DECLARE
    id_usuario_paciente INTEGER;
    id_usuario_profesional INTEGER;
BEGIN
    -- Obtener los IDs de usuario del paciente y profesional de la sesión
    SELECT p.ID_USUARIO, prof.ID_USUARIO
    INTO id_usuario_paciente, id_usuario_profesional
    FROM SESION_CHAT sc
    JOIN PACIENTE p ON sc.ID_PACIENTE = p.ID_PACIENTE
    JOIN PROFESIONAL prof ON sc.ID_PROFESIONAL = prof.ID_PROFESIONAL
    WHERE sc.ID_SESION_CHAT = NEW.ID_SESION_CHAT;

    -- Verificar que el remitente es uno de los participantes
    IF NEW.ID_REMITENTE != id_usuario_paciente AND NEW.ID_REMITENTE != id_usuario_profesional THEN
        RAISE EXCEPTION 'El remitente no pertenece a esta sesión de chat';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validar_remitente
BEFORE INSERT ON MENSAJE_CHAT
FOR EACH ROW EXECUTE FUNCTION validar_remitente_mensaje();

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista: Sesiones activas con último mensaje
CREATE OR REPLACE VIEW v_sesiones_chat_activas AS
SELECT
    sc.ID_SESION_CHAT,
    sc.ID_PACIENTE,
    u_pac.NOMBRE || ' ' || u_pac.APELLIDOS AS NOMBRE_PACIENTE,
    sc.ID_PROFESIONAL,
    u_prof.NOMBRE || ' ' || u_prof.APELLIDOS AS NOMBRE_PROFESIONAL,
    sc.FECHA_CREACION,
    sc.ULTIMA_ACTIVIDAD,
    sc.ESTADO,
    (SELECT COUNT(*) FROM MENSAJE_CHAT mc
     WHERE mc.ID_SESION_CHAT = sc.ID_SESION_CHAT
     AND mc.LEIDO = FALSE) AS MENSAJES_NO_LEIDOS,
    org.NOMBRE_OFICIAL AS ORGANIZACION
FROM SESION_CHAT sc
JOIN PACIENTE p ON sc.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u_pac ON p.ID_USUARIO = u_pac.ID_USUARIO
JOIN PROFESIONAL prof ON sc.ID_PROFESIONAL = prof.ID_PROFESIONAL
JOIN USUARIO u_prof ON prof.ID_USUARIO = u_prof.ID_USUARIO
JOIN ORGANIZACION org ON u_pac.ID_ORGANIZACION = org.ID_ORGANIZACION
WHERE sc.ESTADO = 'ACTIVA'
ORDER BY sc.ULTIMA_ACTIVIDAD DESC;

-- Vista: Mensajes con información de remitente
CREATE OR REPLACE VIEW v_mensajes_chat_completos AS
SELECT
    mc.ID_MENSAJE,
    mc.ID_SESION_CHAT,
    mc.ID_REMITENTE,
    u.NOMBRE || ' ' || u.APELLIDOS AS NOMBRE_REMITENTE,
    u.TIPO_USUARIO AS TIPO_REMITENTE,
    mc.MENSAJE,
    mc.FECHA_ENVIO,
    mc.LEIDO,
    mc.FECHA_LECTURA,
    sc.ID_PACIENTE,
    sc.ID_PROFESIONAL
FROM MENSAJE_CHAT mc
JOIN USUARIO u ON mc.ID_REMITENTE = u.ID_USUARIO
JOIN SESION_CHAT sc ON mc.ID_SESION_CHAT = sc.ID_SESION_CHAT
ORDER BY mc.FECHA_ENVIO ASC;

-- Vista: Estadísticas de mensajes por sesión
CREATE OR REPLACE VIEW v_estadisticas_sesion_chat AS
SELECT
    sc.ID_SESION_CHAT,
    sc.ID_PACIENTE,
    sc.ID_PROFESIONAL,
    COUNT(mc.ID_MENSAJE) AS TOTAL_MENSAJES,
    SUM(CASE WHEN mc.LEIDO = FALSE THEN 1 ELSE 0 END) AS MENSAJES_NO_LEIDOS,
    MAX(mc.FECHA_ENVIO) AS ULTIMO_MENSAJE_FECHA,
    sc.FECHA_CREACION,
    sc.ULTIMA_ACTIVIDAD,
    sc.ESTADO
FROM SESION_CHAT sc
LEFT JOIN MENSAJE_CHAT mc ON sc.ID_SESION_CHAT = mc.ID_SESION_CHAT
GROUP BY sc.ID_SESION_CHAT, sc.ID_PACIENTE, sc.ID_PROFESIONAL,
         sc.FECHA_CREACION, sc.ULTIMA_ACTIVIDAD, sc.ESTADO;

-- =====================================================
-- ÍNDICE COMPUESTO para búsqueda de sesión única
-- =====================================================
-- Evitar múltiples sesiones activas entre el mismo profesional y paciente
CREATE UNIQUE INDEX idx_sesion_unica_activa
ON SESION_CHAT(ID_PACIENTE, ID_PROFESIONAL, ESTADO)
WHERE ESTADO = 'ACTIVA';

-- =====================================================
-- FIN FASE 3
-- =====================================================