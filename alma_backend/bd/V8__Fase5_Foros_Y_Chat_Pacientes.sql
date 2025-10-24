-- =====================================================
-- FASE 5: FOROS Y CHAT ENTRE PACIENTES
-- Implementación de funcionalidades de socialización y apoyo mutuo
-- Versión: 1.0
-- =====================================================

-- =====================================================
-- 1. TABLA: FORO
-- Foros de discusión compartidos entre organizaciones
-- =====================================================
CREATE TABLE FORO (
    ID_FORO SERIAL,
    NOMBRE VARCHAR(100) NOT NULL,
    DESCRIPCION TEXT,
    ID_FASE_DUELO INTEGER,
    ACTIVO BOOLEAN DEFAULT TRUE,
    FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_FORO PRIMARY KEY (ID_FORO),
    CONSTRAINT FK_FORO_FASE FOREIGN KEY (ID_FASE_DUELO)
        REFERENCES FASE_DUELO(ID_FASE) ON DELETE SET NULL
);

CREATE INDEX idx_foro_fase ON FORO(ID_FASE_DUELO);
CREATE INDEX idx_foro_activo ON FORO(ACTIVO);

COMMENT ON TABLE FORO IS 'Foros de discusión temáticos para pacientes, pueden ser generales o específicos de una fase del duelo';
COMMENT ON COLUMN FORO.ID_FASE_DUELO IS 'Fase del duelo a la que está asociado el foro (NULL para foros generales)';

-- =====================================================
-- 2. TABLA: MENSAJE_FORO
-- Mensajes publicados en los foros
-- =====================================================
CREATE TABLE MENSAJE_FORO (
    ID_MENSAJE_FORO SERIAL,
    ID_FORO INTEGER NOT NULL,
    ID_USUARIO INTEGER NOT NULL,
    MENSAJE TEXT NOT NULL,
    FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    EDITADO BOOLEAN DEFAULT FALSE,
    FECHA_EDICION TIMESTAMP,
    MODERADO BOOLEAN DEFAULT FALSE,
    MOTIVO_MODERACION TEXT,

    CONSTRAINT PK_MENSAJE_FORO PRIMARY KEY (ID_MENSAJE_FORO),
    CONSTRAINT FK_MENSAJE_FORO_FORO FOREIGN KEY (ID_FORO)
        REFERENCES FORO(ID_FORO) ON DELETE CASCADE,
    CONSTRAINT FK_MENSAJE_FORO_USUARIO FOREIGN KEY (ID_USUARIO)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE
);

CREATE INDEX idx_mensaje_foro_foro ON MENSAJE_FORO(ID_FORO);
CREATE INDEX idx_mensaje_foro_usuario ON MENSAJE_FORO(ID_USUARIO);
CREATE INDEX idx_mensaje_foro_fecha ON MENSAJE_FORO(FECHA_PUBLICACION);

COMMENT ON TABLE MENSAJE_FORO IS 'Mensajes publicados por los usuarios en los foros';
COMMENT ON COLUMN MENSAJE_FORO.MODERADO IS 'Indica si el mensaje ha sido moderado (ocultado o eliminado) por un administrador';

-- =====================================================
-- 3. TABLA: CHAT_PACIENTES
-- Sesiones de chat privado entre dos pacientes
-- =====================================================
CREATE TABLE CHAT_PACIENTES (
    ID_CHAT_PACIENTES SERIAL,
    ID_PACIENTE_1 INTEGER NOT NULL,
    ID_PACIENTE_2 INTEGER NOT NULL,
    FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ULTIMA_ACTIVIDAD TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ESTADO VARCHAR(20) DEFAULT 'ACTIVO',

    CONSTRAINT PK_CHAT_PACIENTES PRIMARY KEY (ID_CHAT_PACIENTES),
    CONSTRAINT FK_CHAT_PAC1 FOREIGN KEY (ID_PACIENTE_1)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT FK_CHAT_PAC2 FOREIGN KEY (ID_PACIENTE_2)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT CK_CHAT_DIFERENTE CHECK (ID_PACIENTE_1 < ID_PACIENTE_2),
    CONSTRAINT UQ_CHAT_PACIENTES UNIQUE (ID_PACIENTE_1, ID_PACIENTE_2)
);

CREATE INDEX idx_chat_pacientes_p1 ON CHAT_PACIENTES(ID_PACIENTE_1);
CREATE INDEX idx_chat_pacientes_p2 ON CHAT_PACIENTES(ID_PACIENTE_2);

COMMENT ON TABLE CHAT_PACIENTES IS 'Sesiones de chat privado entre dos pacientes';

-- =====================================================
-- 4. TABLA: MENSAJE_CHAT_PACIENTES
-- Mensajes de los chats privados entre pacientes
-- =====================================================
CREATE TABLE MENSAJE_CHAT_PACIENTES (
    ID_MENSAJE SERIAL,
    ID_CHAT_PACIENTES INTEGER NOT NULL,
    ID_REMITENTE_PACIENTE INTEGER NOT NULL,
    MENSAJE TEXT NOT NULL,
    FECHA_ENVIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    LEIDO BOOLEAN DEFAULT FALSE,
    FECHA_LECTURA TIMESTAMP,

    CONSTRAINT PK_MENSAJE_CHAT_PACIENTES PRIMARY KEY (ID_MENSAJE),
    CONSTRAINT FK_MENSAJE_CHAT_PAC FOREIGN KEY (ID_CHAT_PACIENTES)
        REFERENCES CHAT_PACIENTES(ID_CHAT_PACIENTES) ON DELETE CASCADE,
    CONSTRAINT FK_MENSAJE_REMITENTE_PAC FOREIGN KEY (ID_REMITENTE_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE
);

CREATE INDEX idx_mensaje_chat_pac_chat ON MENSAJE_CHAT_PACIENTES(ID_CHAT_PACIENTES);
CREATE INDEX idx_mensaje_chat_pac_fecha ON MENSAJE_CHAT_PACIENTES(FECHA_ENVIO);

COMMENT ON TABLE MENSAJE_CHAT_PACIENTES IS 'Mensajes de los chats privados entre pacientes';
COMMENT ON COLUMN MENSAJE_CHAT_PACIENTES.ID_REMITENTE_PACIENTE IS 'ID del paciente que envía el mensaje';

-- =====================================================
-- 5. DATOS INICIALES
-- =====================================================
INSERT INTO FORO (NOMBRE, DESCRIPCION, ID_FASE_DUELO) VALUES
('Foro General', 'Un espacio para hablar de cualquier tema y conectar con otros.', NULL),
('Viviendo la Negación', 'Comparte tus sentimientos y experiencias en la fase de negación.', 1),
('Gestionando la Ira', 'Un lugar para expresar y entender la ira como parte del duelo.', 2),
('Momentos de Negociación', 'Habla sobre los "y si..." y encuentra apoyo en la fase de negociación.', 3),
('Afrontando la Depresión', 'No estás solo. Comparte cómo te sientes en la fase de depresión.', 4),
('Caminando hacia la Aceptación', 'Un espacio para compartir avances, logros y la vida en la fase de aceptación.', 5);

-- =====================================================
-- 6. TRIGGERS Y FUNCIONES
-- =====================================================

-- Trigger para actualizar ultima_actividad en CHAT_PACIENTES
CREATE OR REPLACE FUNCTION actualizar_ultima_actividad_chat_pacientes()
RETURNS TRIGGER AS $$
BEGIN
    UPDATE CHAT_PACIENTES
    SET ULTIMA_ACTIVIDAD = CURRENT_TIMESTAMP
    WHERE ID_CHAT_PACIENTES = NEW.ID_CHAT_PACIENTES;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_mensaje_actividad_pacientes
AFTER INSERT ON MENSAJE_CHAT_PACIENTES
FOR EACH ROW EXECUTE FUNCTION actualizar_ultima_actividad_chat_pacientes();

-- Trigger para actualizar fecha_lectura en MENSAJE_CHAT_PACIENTES
CREATE OR REPLACE FUNCTION actualizar_fecha_lectura_pacientes()
RETURNS TRIGGER AS $$
BEGIN
    IF NEW.LEIDO = TRUE AND OLD.LEIDO = FALSE THEN
        NEW.FECHA_LECTURA = CURRENT_TIMESTAMP;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_marcar_leido_pacientes
BEFORE UPDATE ON MENSAJE_CHAT_PACIENTES
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_lectura_pacientes();

-- =====================================================
-- 7. VALIDACIONES A NIVEL DE BD
-- =====================================================

-- Validar que el remitente de un mensaje de chat de pacientes es uno de los dos participantes
CREATE OR REPLACE FUNCTION validar_remitente_chat_pacientes()
RETURNS TRIGGER AS $$
DECLARE
    participante1 INTEGER;
    participante2 INTEGER;
BEGIN
    SELECT ID_PACIENTE_1, ID_PACIENTE_2
    INTO participante1, participante2
    FROM CHAT_PACIENTES
    WHERE ID_CHAT_PACIENTES = NEW.ID_CHAT_PACIENTES;

    IF NEW.ID_REMITENTE_PACIENTE != participante1 AND NEW.ID_REMITENTE_PACIENTE != participante2 THEN
        RAISE EXCEPTION 'El remitente no es un participante válido en este chat.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_validar_remitente_pacientes
BEFORE INSERT ON MENSAJE_CHAT_PACIENTES
FOR EACH ROW EXECUTE FUNCTION validar_remitente_chat_pacientes();

-- =====================================================
-- FIN DE MIGRACIÓN FASE 5
-- =====================================================
