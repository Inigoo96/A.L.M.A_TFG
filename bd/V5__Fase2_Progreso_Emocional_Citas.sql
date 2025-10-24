-- =====================================================
-- FASE 2: PROGRESO EMOCIONAL Y CITAS
-- Implementación de seguimiento de duelo y gestión de citas
-- Versión: 1.0
-- =====================================================

-- =====================================================
-- TABLA: FASE_DUELO
-- Catálogo de fases del duelo según modelo Kübler-Ross
-- =====================================================
CREATE TABLE FASE_DUELO (
    ID_FASE SERIAL,
    NOMBRE VARCHAR(50) NOT NULL UNIQUE,
    DESCRIPCION TEXT,
    ORDEN_FASE INTEGER NOT NULL UNIQUE,

    CONSTRAINT PK_FASE_DUELO PRIMARY KEY (ID_FASE)
);

COMMENT ON TABLE FASE_DUELO IS 'Catálogo de fases del duelo según modelo Kübler-Ross';
COMMENT ON COLUMN FASE_DUELO.NOMBRE IS 'Nombre de la fase: NEGACION, IRA, NEGOCIACION, DEPRESION, ACEPTACION';
COMMENT ON COLUMN FASE_DUELO.ORDEN_FASE IS 'Orden secuencial de la fase del duelo';

-- Insertar datos iniciales de fases del duelo
INSERT INTO FASE_DUELO (NOMBRE, DESCRIPCION, ORDEN_FASE) VALUES
('NEGACION', 'Fase inicial donde se niega la pérdida', 1),
('IRA', 'Sentimientos de frustración y enojo', 2),
('NEGOCIACION', 'Intentos de negociar con la situación', 3),
('DEPRESION', 'Tristeza profunda ante la pérdida', 4),
('ACEPTACION', 'Aceptación de la nueva realidad', 5);

-- =====================================================
-- TABLA: PROGRESO_DUELO
-- Registro del progreso emocional del paciente
-- =====================================================
CREATE TABLE PROGRESO_DUELO (
    ID_PROGRESO SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    ID_PROFESIONAL INTEGER,
    ID_FASE_DUELO INTEGER NOT NULL,
    ESTADO_EMOCIONAL VARCHAR(50),
    NOTAS TEXT,
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_PROGRESO_DUELO PRIMARY KEY (ID_PROGRESO),
    CONSTRAINT FK_PROGRESO_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT FK_PROGRESO_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE SET NULL,
    CONSTRAINT FK_PROGRESO_FASE FOREIGN KEY (ID_FASE_DUELO)
        REFERENCES FASE_DUELO(ID_FASE),
    CONSTRAINT CK_ESTADO_EMOCIONAL CHECK (ESTADO_EMOCIONAL IN ('MUY_MAL', 'MAL', 'NEUTRAL', 'BIEN', 'MUY_BIEN'))
);

-- Índices para optimización
CREATE INDEX idx_progreso_paciente ON PROGRESO_DUELO(ID_PACIENTE);
CREATE INDEX idx_progreso_fecha ON PROGRESO_DUELO(FECHA_REGISTRO);
CREATE INDEX idx_progreso_fase ON PROGRESO_DUELO(ID_FASE_DUELO);

COMMENT ON TABLE PROGRESO_DUELO IS 'Registro del progreso emocional del paciente en el proceso de duelo';
COMMENT ON COLUMN PROGRESO_DUELO.ID_PROFESIONAL IS 'Profesional que registró el progreso (NULL si es auto-registro del paciente)';
COMMENT ON COLUMN PROGRESO_DUELO.ESTADO_EMOCIONAL IS 'Estado emocional: MUY_MAL, MAL, NEUTRAL, BIEN, MUY_BIEN';

-- =====================================================
-- TABLA: CITA
-- Gestión de citas entre profesional y paciente
-- =====================================================
CREATE TABLE CITA (
    ID_CITA SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    ID_PROFESIONAL INTEGER NOT NULL,
    FECHA_HORA TIMESTAMP NOT NULL,
    DURACION_MINUTOS INTEGER DEFAULT 60,
    TIPO_CITA VARCHAR(30) DEFAULT 'CONSULTA',
    ESTADO VARCHAR(20) DEFAULT 'PROGRAMADA',
    MOTIVO TEXT,
    NOTAS_SESION TEXT,
    FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_CITA PRIMARY KEY (ID_CITA),
    CONSTRAINT FK_CITA_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT FK_CITA_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE,
    CONSTRAINT CK_TIPO_CITA CHECK (TIPO_CITA IN ('CONSULTA', 'SEGUIMIENTO', 'URGENTE')),
    CONSTRAINT CK_ESTADO_CITA CHECK (ESTADO IN ('PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA'))
);

-- Índices para optimización
CREATE INDEX idx_cita_paciente ON CITA(ID_PACIENTE);
CREATE INDEX idx_cita_profesional ON CITA(ID_PROFESIONAL);
CREATE INDEX idx_cita_fecha ON CITA(FECHA_HORA);
CREATE INDEX idx_cita_estado ON CITA(ESTADO);

COMMENT ON TABLE CITA IS 'Gestión de agenda de citas entre profesional y paciente';
COMMENT ON COLUMN CITA.TIPO_CITA IS 'Tipo de cita: CONSULTA, SEGUIMIENTO, URGENTE';
COMMENT ON COLUMN CITA.ESTADO IS 'Estado: PROGRAMADA, CONFIRMADA, COMPLETADA, CANCELADA';
COMMENT ON COLUMN CITA.NOTAS_SESION IS 'Notas añadidas por el profesional después de la cita';

-- =====================================================
-- TRIGGERS para actualizar fecha_ultima_modificacion
-- =====================================================

-- Trigger para PROGRESO_DUELO
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.FECHA_ULTIMA_MODIFICACION = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_progreso_modificacion
BEFORE UPDATE ON PROGRESO_DUELO
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

-- Trigger para CITA
CREATE TRIGGER trigger_cita_modificacion
BEFORE UPDATE ON CITA
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista: Progreso reciente de pacientes
CREATE OR REPLACE VIEW v_progreso_reciente AS
SELECT
    pd.ID_PROGRESO,
    pd.ID_PACIENTE,
    u_pac.NOMBRE || ' ' || u_pac.APELLIDOS AS NOMBRE_PACIENTE,
    pd.ID_PROFESIONAL,
    u_prof.NOMBRE || ' ' || u_prof.APELLIDOS AS NOMBRE_PROFESIONAL,
    fd.NOMBRE AS FASE_DUELO,
    fd.ORDEN_FASE,
    pd.ESTADO_EMOCIONAL,
    pd.NOTAS,
    pd.FECHA_REGISTRO
FROM PROGRESO_DUELO pd
JOIN PACIENTE p ON pd.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u_pac ON p.ID_USUARIO = u_pac.ID_USUARIO
LEFT JOIN PROFESIONAL prof ON pd.ID_PROFESIONAL = prof.ID_PROFESIONAL
LEFT JOIN USUARIO u_prof ON prof.ID_USUARIO = u_prof.ID_USUARIO
JOIN FASE_DUELO fd ON pd.ID_FASE_DUELO = fd.ID_FASE
ORDER BY pd.FECHA_REGISTRO DESC;

-- Vista: Citas próximas
CREATE OR REPLACE VIEW v_citas_proximas AS
SELECT
    c.ID_CITA,
    c.ID_PACIENTE,
    u_pac.NOMBRE || ' ' || u_pac.APELLIDOS AS NOMBRE_PACIENTE,
    c.ID_PROFESIONAL,
    u_prof.NOMBRE || ' ' || u_prof.APELLIDOS AS NOMBRE_PROFESIONAL,
    c.FECHA_HORA,
    c.DURACION_MINUTOS,
    c.TIPO_CITA,
    c.ESTADO,
    c.MOTIVO,
    org.NOMBRE_OFICIAL AS ORGANIZACION
FROM CITA c
JOIN PACIENTE p ON c.ID_PACIENTE = p.ID_PACIENTE
JOIN USUARIO u_pac ON p.ID_USUARIO = u_pac.ID_USUARIO
JOIN PROFESIONAL prof ON c.ID_PROFESIONAL = prof.ID_PROFESIONAL
JOIN USUARIO u_prof ON prof.ID_USUARIO = u_prof.ID_USUARIO
JOIN ORGANIZACION org ON u_pac.ID_ORGANIZACION = org.ID_ORGANIZACION
WHERE c.FECHA_HORA >= CURRENT_TIMESTAMP
AND c.ESTADO IN ('PROGRAMADA', 'CONFIRMADA')
ORDER BY c.FECHA_HORA ASC;

-- =====================================================
-- VALIDACIONES A NIVEL DE BD
-- =====================================================

-- Validar que solo pueden crear citas si existe asignación activa
CREATE OR REPLACE FUNCTION validar_asignacion_cita()
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

CREATE TRIGGER trigger_validar_cita_asignacion
BEFORE INSERT ON CITA
FOR EACH ROW EXECUTE FUNCTION validar_asignacion_cita();

-- Validar que la cita no se solape con otras del mismo profesional
CREATE OR REPLACE FUNCTION validar_solapamiento_citas()
RETURNS TRIGGER AS $$
DECLARE
    fecha_fin_nueva TIMESTAMP;
    conflictos INTEGER;
BEGIN
    -- Calcular fecha fin de la nueva cita
    fecha_fin_nueva := NEW.FECHA_HORA + (NEW.DURACION_MINUTOS || ' minutes')::INTERVAL;

    -- Verificar solapamientos (solo para citas programadas o confirmadas)
    SELECT COUNT(*) INTO conflictos
    FROM CITA
    WHERE ID_PROFESIONAL = NEW.ID_PROFESIONAL
    AND ID_CITA != COALESCE(NEW.ID_CITA, -1)
    AND ESTADO IN ('PROGRAMADA', 'CONFIRMADA')
    AND (
        -- La nueva cita empieza durante otra cita existente
        (NEW.FECHA_HORA >= FECHA_HORA AND NEW.FECHA_HORA < FECHA_HORA + (DURACION_MINUTOS || ' minutes')::INTERVAL)
        OR
        -- La nueva cita termina durante otra cita existente
        (fecha_fin_nueva > FECHA_HORA AND fecha_fin_nueva <= FECHA_HORA + (DURACION_MINUTOS || ' minutes')::INTERVAL)
        OR
        -- La nueva cita contiene completamente otra cita existente
        (NEW.FECHA_HORA <= FECHA_HORA AND fecha_fin_nueva >= FECHA_HORA + (DURACION_MINUTOS || ' minutes')::INTERVAL)
    );

    IF conflictos > 0 THEN
        RAISE EXCEPTION 'El profesional ya tiene una cita programada en ese horario';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validar_solapamiento
BEFORE INSERT OR UPDATE ON CITA
FOR EACH ROW EXECUTE FUNCTION validar_solapamiento_citas();

-- =====================================================
-- FIN FASE 2
-- =====================================================