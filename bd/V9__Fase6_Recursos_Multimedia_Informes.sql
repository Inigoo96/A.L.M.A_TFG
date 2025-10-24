-- =====================================================
-- FASE 6: RECURSOS MULTIMEDIA E INFORMES
-- Implementación de contenido de apoyo y reportes automáticos
-- Versión: 1.0
-- =====================================================

-- =====================================================
-- 1. TABLA: PODCAST
-- Recursos de audio para pacientes
-- =====================================================
CREATE TABLE PODCAST (
    ID_PODCAST SERIAL,
    TITULO VARCHAR(150) NOT NULL,
    DESCRIPCION TEXT,
    URL VARCHAR(255) NOT NULL,
    DURACION_MINUTOS INTEGER,
    CATEGORIA VARCHAR(50), -- Ej: MEDITACION, HISTORIA, TESTIMONIO
    ID_FASE_DUELO_RECOMENDADA INTEGER,
    ACTIVO BOOLEAN DEFAULT TRUE,
    FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_PODCAST PRIMARY KEY (ID_PODCAST),
    CONSTRAINT FK_PODCAST_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
        REFERENCES FASE_DUELO(ID_FASE) ON DELETE SET NULL
);

CREATE INDEX idx_podcast_categoria ON PODCAST(CATEGORIA);
CREATE INDEX idx_podcast_fase ON PODCAST(ID_FASE_DUELO_RECOMENDADA);

COMMENT ON TABLE PODCAST IS 'Recursos de audio (podcasts) disponibles para los pacientes';

-- =====================================================
-- 2. TABLA: VIDEO
-- Recursos de video para pacientes
-- =====================================================
CREATE TABLE VIDEO (
    ID_VIDEO SERIAL,
    TITULO VARCHAR(150) NOT NULL,
    DESCRIPCION TEXT,
    URL VARCHAR(255) NOT NULL,
    DURACION_MINUTOS INTEGER,
    CATEGORIA VARCHAR(50), -- Ej: EJERCICIOS, EXPLICATIVO, TESTIMONIO
    ID_FASE_DUELO_RECOMENDADA INTEGER,
    ACTIVO BOOLEAN DEFAULT TRUE,
    FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_VIDEO PRIMARY KEY (ID_VIDEO),
    CONSTRAINT FK_VIDEO_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
        REFERENCES FASE_DUELO(ID_FASE) ON DELETE SET NULL
);

CREATE INDEX idx_video_categoria ON VIDEO(CATEGORIA);
CREATE INDEX idx_video_fase ON VIDEO(ID_FASE_DUELO_RECOMENDADA);

COMMENT ON TABLE VIDEO IS 'Recursos de video disponibles para los pacientes';

-- =====================================================
-- 3. TABLA: MUSICA
-- Recursos de música para pacientes
-- =====================================================
CREATE TABLE MUSICA (
    ID_MUSICA SERIAL,
    TITULO VARCHAR(150) NOT NULL,
    ARTISTA VARCHAR(100),
    URL VARCHAR(255) NOT NULL,
    DURACION_MINUTOS INTEGER,
    GENERO VARCHAR(50), -- Ej: RELAJANTE, MOTIVADORA, REFLEXIVA
    ID_FASE_DUELO_RECOMENDADA INTEGER,
    ACTIVO BOOLEAN DEFAULT TRUE,
    FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_MUSICA PRIMARY KEY (ID_MUSICA),
    CONSTRAINT FK_MUSICA_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
        REFERENCES FASE_DUELO(ID_FASE) ON DELETE SET NULL
);

CREATE INDEX idx_musica_genero ON MUSICA(GENERO);
CREATE INDEX idx_musica_fase ON MUSICA(ID_FASE_DUELO_RECOMENDADA);

COMMENT ON TABLE MUSICA IS 'Recursos de música disponibles para los pacientes';

-- =====================================================
-- 4. TABLA: USO_RECURSO
-- Registro del uso de recursos multimedia por pacientes
-- =====================================================
CREATE TABLE USO_RECURSO (
    ID_USO SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    TIPO_RECURSO VARCHAR(20) NOT NULL, -- PODCAST, VIDEO, MUSICA
    ID_RECURSO INTEGER NOT NULL, -- ID del recurso específico
    FECHA_USO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    TIEMPO_CONSUMIDO_MINUTOS INTEGER,
    VALORACION INTEGER, -- 1-5 estrellas

    CONSTRAINT PK_USO_RECURSO PRIMARY KEY (ID_USO),
    CONSTRAINT FK_USO_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT CK_TIPO_RECURSO CHECK (TIPO_RECURSO IN ('PODCAST', 'VIDEO', 'MUSICA')),
    CONSTRAINT CK_VALORACION CHECK (VALORACION >= 1 AND VALORACION <= 5)
);

CREATE INDEX idx_uso_paciente ON USO_RECURSO(ID_PACIENTE);
CREATE INDEX idx_uso_tipo_recurso ON USO_RECURSO(TIPO_RECURSO, ID_RECURSO);

COMMENT ON TABLE USO_RECURSO IS 'Registro del uso y valoración de recursos multimedia por los pacientes';

-- =====================================================
-- 5. TABLA: INFORME_EMOCIONAL
-- Informes generados para profesionales sobre el progreso del paciente
-- =====================================================
CREATE TABLE INFORME_EMOCIONAL (
    ID_INFORME SERIAL,
    ID_PACIENTE INTEGER NOT NULL,
    ID_PROFESIONAL INTEGER NOT NULL,
    PERIODO_INICIO DATE NOT NULL,
    PERIODO_FIN DATE NOT NULL,
    CONTENIDO_INFORME JSONB, -- Estadísticas y resumen en formato JSON
    GENERADO_POR VARCHAR(20) DEFAULT 'AUTOMATICO', -- AUTOMATICO, MANUAL
    FECHA_GENERACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT PK_INFORME_EMOCIONAL PRIMARY KEY (ID_INFORME),
    CONSTRAINT FK_INFORME_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
    CONSTRAINT FK_INFORME_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE,
    CONSTRAINT CK_GENERADO_POR CHECK (GENERADO_POR IN ('AUTOMATICO', 'MANUAL'))
);

CREATE INDEX idx_informe_paciente ON INFORME_EMOCIONAL(ID_PACIENTE);
CREATE INDEX idx_informe_profesional ON INFORME_EMOCIONAL(ID_PROFESIONAL);
CREATE INDEX idx_informe_fecha_generacion ON INFORME_EMOCIONAL(FECHA_GENERACION);

COMMENT ON TABLE INFORME_EMOCIONAL IS 'Informes emocionales generados para los profesionales sobre el progreso de sus pacientes';

-- =====================================================
-- 6. TRIGGERS para actualizar fecha_ultima_modificacion
-- =====================================================

-- Reutilizamos la función actualizar_fecha_modificacion() ya existente

CREATE TRIGGER trigger_podcast_modificacion
BEFORE UPDATE ON PODCAST
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_video_modificacion
BEFORE UPDATE ON VIDEO
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_musica_modificacion
BEFORE UPDATE ON MUSICA
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_uso_recurso_modificacion
BEFORE UPDATE ON USO_RECURSO
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_informe_emocional_modificacion
BEFORE UPDATE ON INFORME_EMOCIONAL
FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();


-- =====================================================
-- 7. INSERTS de PRUEBA
-- =====================================================

INSERT INTO PODCAST (TITULO, DESCRIPCION, URL, DURACION_MINUTOS, CATEGORIA, ID_FASE_DUELO_RECOMENDADA) VALUES
('Meditación Guiada para la Calma', 'Una sesión de 10 minutos para encontrar la paz interior.', 'https://example.com/podcast/meditacion1.mp3', 10, 'MEDITACION', 4),
('Aceptando la Realidad', 'Un testimonio sobre cómo afrontar la fase de aceptación.', 'https://example.com/podcast/aceptacion1.mp3', 15, 'TESTIMONIO', 5);

INSERT INTO VIDEO (TITULO, DESCRIPCION, URL, DURACION_MINUTOS, CATEGORIA, ID_FASE_DUELO_RECOMENDADA) VALUES
('Ejercicios de Respiración', 'Técnicas de respiración para manejar la ansiedad.', 'https://example.com/video/respiracion1.mp4', 5, 'EJERCICIOS', 3),
('El Duelo Explicado', 'Un video animado que explica las fases del duelo.', 'https://example.com/video/duelo1.mp4', 8, 'EXPLICATIVO', 1);

INSERT INTO MUSICA (TITULO, ARTISTA, URL, DURACION_MINUTOS, GENERO, ID_FASE_DUELO_RECOMENDADA) VALUES
('Sonidos de la Naturaleza', 'Ambiente Natural', 'https://example.com/music/naturaleza1.mp3', 30, 'RELAJANTE', NULL),
('Melodía para la Reflexión', 'Compositor Anónimo', 'https://example.com/music/reflexion1.mp3', 7, 'REFLEXIVA', 4);

-- =====================================================
-- FIN DE MIGRACIÓN FASE 6
-- =====================================================
