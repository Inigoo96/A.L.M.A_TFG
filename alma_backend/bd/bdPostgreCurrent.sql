-- =====================================================
-- BASE DE DATOS A.L.M.A. (Asistente de Lenguaje Moderno Adaptativo)
-- PostgreSQL - Sistema completo de gestión con seguridad mejorada
-- Versión: 2.0 - Mega Update con validaciones y anti-suplantación
-- =====================================================

-- =====================================================
-- LIMPIEZA PREVIA (solo para desarrollo/reconstrucción)
-- =====================================================
-- COMENTAR ESTAS LÍNEAS EN PRODUCCIÓN
/*
DROP TABLE IF EXISTS SESION_INTERACCION CASCADE;
DROP TABLE IF EXISTS ASIGNACION_PROFESIONAL_PACIENTE CASCADE;
DROP TABLE IF EXISTS ADMIN_ORGANIZACION CASCADE;
DROP TABLE IF EXISTS PACIENTE CASCADE;
DROP TABLE IF EXISTS PROFESIONAL CASCADE;
DROP TABLE IF EXISTS USUARIO CASCADE;
DROP TABLE IF EXISTS ORGANIZACION CASCADE;
*/

-- =====================================================
-- TABLA: ORGANIZACION
-- Almacena información de organizaciones sanitarias con sistema anti-suplantación
-- =====================================================
CREATE TABLE ORGANIZACION (
    ID_ORGANIZACION SERIAL,

    -- Datos oficiales de la organización (anti-suplantación)
    CIF VARCHAR(9) NOT NULL,
    NUMERO_SEGURIDAD_SOCIAL VARCHAR(20) NOT NULL,
    NOMBRE_OFICIAL VARCHAR(255) NOT NULL,
    DIRECCION VARCHAR(255) NOT NULL,
    CODIGO_REGCESS VARCHAR(50) NOT NULL,
    EMAIL_CORPORATIVO VARCHAR(100) NOT NULL,
    TELEFONO_CONTACTO VARCHAR(20) NOT NULL,

    -- Documentos para verificación manual
    DOCUMENTO_CIF_URL VARCHAR(255),
    DOCUMENTO_SEGURIDAD_SOCIAL_URL VARCHAR(255),

    -- Estado de verificación (anti-suplantación)
    ESTADO_VERIFICACION VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_VERIFICACION',
    MOTIVO_RECHAZO VARCHAR(500),

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_ORGANIZACION PRIMARY KEY (ID_ORGANIZACION),
    CONSTRAINT UQ_CIF UNIQUE (CIF),
    CONSTRAINT UQ_EMAIL_CORPORATIVO UNIQUE (EMAIL_CORPORATIVO),
    CONSTRAINT CK_ESTADO_VERIFICACION CHECK (ESTADO_VERIFICACION IN ('PENDIENTE_VERIFICACION', 'EN_REVISION', 'VERIFICADA', 'RECHAZADA'))
);

-- Índices para optimización
CREATE INDEX idx_organizacion_cif ON ORGANIZACION(CIF);
CREATE INDEX idx_organizacion_estado ON ORGANIZACION(ESTADO_VERIFICACION);

COMMENT ON TABLE ORGANIZACION IS 'Organizaciones sanitarias registradas en el sistema con verificación anti-suplantación';
COMMENT ON COLUMN ORGANIZACION.CIF IS 'CIF de la organización (validado con algoritmo módulo 23)';
COMMENT ON COLUMN ORGANIZACION.NUMERO_SEGURIDAD_SOCIAL IS 'Número de inscripción en Seguridad Social';
COMMENT ON COLUMN ORGANIZACION.CODIGO_REGCESS IS 'Código del Registro General de Centros del SNS';
COMMENT ON COLUMN ORGANIZACION.ESTADO_VERIFICACION IS 'Estado de verificación: PENDIENTE_VERIFICACION, EN_REVISION, VERIFICADA, RECHAZADA';

-- =====================================================
-- TABLA: USUARIO
-- Tabla principal de usuarios (base para todos los tipos)
-- =====================================================
CREATE TABLE USUARIO (
    ID_USUARIO SERIAL,

    -- Datos personales básicos (validados automáticamente)
    DNI VARCHAR(15) NOT NULL,
    EMAIL VARCHAR(100) NOT NULL,
    PASSWORD_HASH VARCHAR(255) NOT NULL,
    NOMBRE VARCHAR(50) NOT NULL,
    APELLIDOS VARCHAR(100),
    TELEFONO VARCHAR(20),

    -- Tipo y permisos
    TIPO_USUARIO VARCHAR(20) NOT NULL,

    -- Relación con organización
    ID_ORGANIZACION INTEGER NOT NULL,

    -- Estado y seguridad
    ACTIVO BOOLEAN DEFAULT TRUE,
    PASSWORD_TEMPORAL BOOLEAN DEFAULT TRUE,

    -- Campos específicos para ADMIN_ORGANIZACION
    CARGO VARCHAR(100),
    DOCUMENTO_CARGO_URL VARCHAR(255),

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ULTIMO_ACCESO TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_USUARIO PRIMARY KEY (ID_USUARIO),
    CONSTRAINT UQ_DNI UNIQUE (DNI),
    CONSTRAINT UQ_EMAIL UNIQUE (EMAIL),
    CONSTRAINT CK_TIPO_USUARIO CHECK (TIPO_USUARIO IN ('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION', 'SUPER_ADMIN')),
    CONSTRAINT FK_USUARIO_ORGANIZACION FOREIGN KEY (ID_ORGANIZACION)
        REFERENCES ORGANIZACION(ID_ORGANIZACION) ON DELETE RESTRICT ON UPDATE CASCADE
);

-- Índices para optimización
CREATE INDEX idx_usuario_dni ON USUARIO(DNI);
CREATE INDEX idx_usuario_email ON USUARIO(EMAIL);
CREATE INDEX idx_usuario_tipo ON USUARIO(TIPO_USUARIO);
CREATE INDEX idx_usuario_organizacion ON USUARIO(ID_ORGANIZACION);
CREATE INDEX idx_usuario_activo ON USUARIO(ACTIVO);

COMMENT ON TABLE USUARIO IS 'Usuarios del sistema (base para pacientes, profesionales y administradores)';
COMMENT ON COLUMN USUARIO.DNI IS 'DNI/NIE validado automáticamente con algoritmo módulo 23';
COMMENT ON COLUMN USUARIO.PASSWORD_TEMPORAL IS 'Si es TRUE, el usuario debe cambiar su contraseña en el próximo login';
COMMENT ON COLUMN USUARIO.CARGO IS 'Cargo del administrador en la organización (solo para ADMIN_ORGANIZACION)';

-- =====================================================
-- TABLA: PROFESIONAL
-- Extiende USUARIO con información de profesionales sanitarios
-- =====================================================
CREATE TABLE PROFESIONAL (
    ID_PROFESIONAL SERIAL,

    -- Relación con usuario base
    ID_USUARIO INTEGER NOT NULL,

    -- Datos profesionales
    NUMERO_COLEGIADO VARCHAR(20),
    ESPECIALIDAD VARCHAR(100),
    CENTRO_SALUD VARCHAR(255),

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_PROFESIONAL PRIMARY KEY (ID_PROFESIONAL),
    CONSTRAINT UQ_ID_USUARIO_PROFESIONAL UNIQUE (ID_USUARIO),
    CONSTRAINT UQ_NUMERO_COLEGIADO UNIQUE (NUMERO_COLEGIADO),
    CONSTRAINT FK_PROFESIONAL_USUARIO FOREIGN KEY (ID_USUARIO)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Índices
CREATE INDEX idx_profesional_colegiado ON PROFESIONAL(NUMERO_COLEGIADO);
CREATE INDEX idx_profesional_especialidad ON PROFESIONAL(ESPECIALIDAD);

COMMENT ON TABLE PROFESIONAL IS 'Profesionales sanitarios del sistema';
COMMENT ON COLUMN PROFESIONAL.NUMERO_COLEGIADO IS 'Número de colegiado validado (formato alfanumérico)';
COMMENT ON COLUMN PROFESIONAL.CENTRO_SALUD IS 'Centro de salud donde trabaja (incluye código REGCESS)';

-- =====================================================
-- TABLA: PACIENTE
-- Extiende USUARIO con información de pacientes
-- =====================================================
CREATE TABLE PACIENTE (
    ID_PACIENTE SERIAL,

    -- Relación con usuario base
    ID_USUARIO INTEGER NOT NULL,

    -- Datos del paciente
    TARJETA_SANITARIA VARCHAR(30),
    FECHA_NACIMIENTO DATE,
    GENERO VARCHAR(30),

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_PACIENTE PRIMARY KEY (ID_PACIENTE),
    CONSTRAINT UQ_ID_USUARIO_PACIENTE UNIQUE (ID_USUARIO),
    CONSTRAINT CK_GENERO CHECK (GENERO IN ('MASCULINO', 'FEMENINO', 'NO_BINARIO', 'PREFIERO_NO_DECIR')),
    CONSTRAINT FK_PACIENTE_USUARIO FOREIGN KEY (ID_USUARIO)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE ON UPDATE CASCADE
);

-- Índices
CREATE INDEX idx_paciente_tarjeta ON PACIENTE(TARJETA_SANITARIA);
CREATE INDEX idx_paciente_fecha_nacimiento ON PACIENTE(FECHA_NACIMIENTO);

COMMENT ON TABLE PACIENTE IS 'Pacientes del sistema';
COMMENT ON COLUMN PACIENTE.TARJETA_SANITARIA IS 'Número de tarjeta sanitaria (SIP/TSI) - SOLO INFORMATIVO, NO para login';
COMMENT ON COLUMN PACIENTE.GENERO IS 'Género del paciente (opcional e inclusivo)';

-- =====================================================
-- TABLA: ASIGNACION_PROFESIONAL_PACIENTE
-- Relación entre profesionales y pacientes
-- =====================================================
CREATE TABLE ASIGNACION_PROFESIONAL_PACIENTE (
    ID_ASIGNACION SERIAL,

    -- Relaciones
    ID_PROFESIONAL INTEGER NOT NULL,
    ID_PACIENTE INTEGER NOT NULL,

    -- Estado de la asignación
    ACTIVA BOOLEAN DEFAULT TRUE,

    -- Fechas
    FECHA_ASIGNACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_FINALIZACION TIMESTAMP,

    -- Notas
    NOTAS TEXT,

    -- Auditoría
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_ASIGNACION PRIMARY KEY (ID_ASIGNACION),
    CONSTRAINT FK_ASIGNACION_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_ASIGNACION_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE ON UPDATE CASCADE,
    -- No puede haber asignaciones duplicadas activas
    CONSTRAINT UQ_ASIGNACION_ACTIVA UNIQUE (ID_PROFESIONAL, ID_PACIENTE, ACTIVA)
);
ALTER TABLE asignacion_profesional_paciente RENAME COLUMN ACTIVA TO activo;
ALTER TABLE asignacion_profesional_paciente ADD COLUMN es_principal BOOLEAN DEFAULT FALSE;
-- Índices
CREATE INDEX idx_asignacion_profesional ON ASIGNACION_PROFESIONAL_PACIENTE(ID_PROFESIONAL);
CREATE INDEX idx_asignacion_paciente ON ASIGNACION_PROFESIONAL_PACIENTE(ID_PACIENTE);
CREATE INDEX idx_asignacion_activa ON ASIGNACION_PROFESIONAL_PACIENTE(ACTIVA);
CREATE INDEX idx_asignacion_fecha ON ASIGNACION_PROFESIONAL_PACIENTE(FECHA_ASIGNACION);

COMMENT ON TABLE ASIGNACION_PROFESIONAL_PACIENTE IS 'Asignaciones entre profesionales y pacientes';

-- =====================================================
-- TABLA: ADMIN_ORGANIZACION
-- Administradores de organizaciones (tabla de relación)
-- =====================================================
CREATE TABLE ADMIN_ORGANIZACION (
    ID_ADMIN SERIAL,
    ID_USUARIO INTEGER NOT NULL,
    ID_ORGANIZACION INTEGER NOT NULL,

    -- Constraints
    CONSTRAINT PK_ADMIN_ORGANIZACION PRIMARY KEY (ID_ADMIN),
    CONSTRAINT UQ_ID_USUARIO_ADMIN_ORG UNIQUE (ID_USUARIO),
    CONSTRAINT FK_ADMIN_USUARIO FOREIGN KEY (ID_USUARIO)
        REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE,
    CONSTRAINT FK_ADMIN_ORGANIZACION FOREIGN KEY (ID_ORGANIZACION)
        REFERENCES ORGANIZACION(ID_ORGANIZACION)
);

COMMENT ON TABLE ADMIN_ORGANIZACION IS 'Administradores que gestionan profesionales y pacientes de una organización';

-- =====================================================
-- TABLA: SESION_INTERACCION (para futuro uso con IA)
-- Almacena sesiones de interacción con el asistente IA
-- =====================================================
CREATE TABLE SESION_INTERACCION (
    ID_SESION SERIAL,

    -- Relaciones
    ID_PACIENTE INTEGER NOT NULL,
    ID_PROFESIONAL INTEGER,

    -- Datos de la sesión
    FECHA_INICIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_FIN TIMESTAMP,
    DURACION_SEGUNDOS INTEGER,

    -- Estado y tipo
    TIPO_SESION VARCHAR(20) DEFAULT 'CONVERSACION',
    ESTADO VARCHAR(20) DEFAULT 'ACTIVA',

    -- Métricas
    NUMERO_MENSAJES INTEGER DEFAULT 0,
    SATISFACCION INTEGER,

    -- Notas
    NOTAS_PROFESIONAL TEXT,

    -- Auditoría
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT PK_SESION_INTERACCION PRIMARY KEY (ID_SESION),
    CONSTRAINT CK_TIPO_SESION CHECK (TIPO_SESION IN ('CONVERSACION', 'EVALUACION', 'TERAPIA')),
    CONSTRAINT CK_ESTADO_SESION CHECK (ESTADO IN ('ACTIVA', 'FINALIZADA', 'INTERRUMPIDA')),
    CONSTRAINT CK_SATISFACCION CHECK (SATISFACCION >= 1 AND SATISFACCION <= 5),
    CONSTRAINT FK_SESION_PACIENTE FOREIGN KEY (ID_PACIENTE)
        REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT FK_SESION_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
        REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE SET NULL ON UPDATE CASCADE
);

-- Índices
CREATE INDEX idx_sesion_paciente ON SESION_INTERACCION(ID_PACIENTE);
CREATE INDEX idx_sesion_profesional ON SESION_INTERACCION(ID_PROFESIONAL);
CREATE INDEX idx_sesion_fecha_inicio ON SESION_INTERACCION(FECHA_INICIO);
CREATE INDEX idx_sesion_estado ON SESION_INTERACCION(ESTADO);
CREATE INDEX idx_sesion_tipo ON SESION_INTERACCION(TIPO_SESION);

COMMENT ON TABLE SESION_INTERACCION IS 'Sesiones de interacción con el asistente IA';

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de usuarios con su organización
CREATE OR REPLACE VIEW v_usuarios_completo AS
SELECT
    u.ID_USUARIO,
    u.DNI,
    u.EMAIL,
    u.NOMBRE,
    u.APELLIDOS,
    u.TELEFONO,
    u.TIPO_USUARIO,
    u.ACTIVO,
    u.PASSWORD_TEMPORAL,
    u.CARGO,
    u.FECHA_REGISTRO,
    u.ULTIMO_ACCESO,
    o.ID_ORGANIZACION,
    o.NOMBRE_OFICIAL AS NOMBRE_ORGANIZACION,
    o.CIF AS CIF_ORGANIZACION,
    o.ESTADO_VERIFICACION AS ESTADO_ORGANIZACION
FROM USUARIO u
INNER JOIN ORGANIZACION o ON u.ID_ORGANIZACION = o.ID_ORGANIZACION;

-- Vista de profesionales con información completa
CREATE OR REPLACE VIEW v_profesionales_completo AS
SELECT
    p.ID_PROFESIONAL,
    p.NUMERO_COLEGIADO,
    p.ESPECIALIDAD,
    p.CENTRO_SALUD,
    u.ID_USUARIO,
    u.DNI,
    u.EMAIL,
    u.NOMBRE,
    u.APELLIDOS,
    u.TELEFONO,
    u.ACTIVO,
    o.ID_ORGANIZACION,
    o.NOMBRE_OFICIAL AS NOMBRE_ORGANIZACION,
    o.ESTADO_VERIFICACION AS ESTADO_ORGANIZACION
FROM PROFESIONAL p
INNER JOIN USUARIO u ON p.ID_USUARIO = u.ID_USUARIO
INNER JOIN ORGANIZACION o ON u.ID_ORGANIZACION = o.ID_ORGANIZACION;

-- Vista de pacientes con información completa
CREATE OR REPLACE VIEW v_pacientes_completo AS
SELECT
    pac.ID_PACIENTE,
    pac.TARJETA_SANITARIA,
    pac.FECHA_NACIMIENTO,
    pac.GENERO,
    u.ID_USUARIO,
    u.DNI,
    u.EMAIL,
    u.NOMBRE,
    u.APELLIDOS,
    u.TELEFONO,
    u.ACTIVO,
    o.ID_ORGANIZACION,
    o.NOMBRE_OFICIAL AS NOMBRE_ORGANIZACION
FROM PACIENTE pac
INNER JOIN USUARIO u ON pac.ID_USUARIO = u.ID_USUARIO
INNER JOIN ORGANIZACION o ON u.ID_ORGANIZACION = o.ID_ORGANIZACION;

-- =====================================================
-- TRIGGERS PARA AUDITORÍA AUTOMÁTICA
-- =====================================================

-- Función para actualizar fecha_ultima_modificacion automáticamente
CREATE OR REPLACE FUNCTION actualizar_fecha_modificacion()
RETURNS TRIGGER AS $$
BEGIN
    NEW.FECHA_ULTIMA_MODIFICACION = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Aplicar trigger a todas las tablas relevantes
CREATE TRIGGER trigger_organizacion_modificacion
    BEFORE UPDATE ON ORGANIZACION
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_usuario_modificacion
    BEFORE UPDATE ON USUARIO
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_profesional_modificacion
    BEFORE UPDATE ON PROFESIONAL
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_paciente_modificacion
    BEFORE UPDATE ON PACIENTE
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_asignacion_modificacion
    BEFORE UPDATE ON ASIGNACION_PROFESIONAL_PACIENTE
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

CREATE TRIGGER trigger_sesion_modificacion
    BEFORE UPDATE ON SESION_INTERACCION
    FOR EACH ROW
    EXECUTE FUNCTION actualizar_fecha_modificacion();

-- =====================================================
-- DATOS INICIALES (OPCIONAL - COMENTADO)
-- =====================================================

-- Ejemplo de organización verificada (descomentar si se necesita)
/*
INSERT INTO ORGANIZACION (
    CIF, NUMERO_SEGURIDAD_SOCIAL, NOMBRE_OFICIAL, DIRECCION,
    CODIGO_REGCESS, EMAIL_CORPORATIVO, TELEFONO_CONTACTO,
    ESTADO_VERIFICACION
) VALUES (
    'A12345674', -- CIF válido de ejemplo (validar con algoritmo)
    '281234567890',
    'Centro de Salud Ejemplo',
    'Calle Principal 123, 28001 Madrid',
    'CSS12345',
    'contacto@centroejemplo.es',
    '912345678',
    'VERIFICADA'
);
*/

-- =====================================================
-- NOTAS IMPORTANTES DE SEGURIDAD
-- =====================================================

-- 1. VALIDACIONES EN BACKEND (Java):
--    - DNI/NIE: Algoritmo módulo 23 (ValidationUtils.java)
--    - CIF: Algoritmo con dígito de control (ValidationUtils.java)
--    - Emails corporativos: Verificar dominio de organización
--    - Teléfonos: Formato español (6XX, 7XX, 8XX, 9XX)

-- 2. CONTRASEÑAS:
--    - Hasheadas con BCrypt (mínimo cost 10)
--    - PASSWORD_TEMPORAL=true fuerza cambio en primer acceso
--    - Profesionales y pacientes reciben contraseña temporal

-- 3. ANTI-SUPLANTACIÓN:
--    - Organizaciones en PENDIENTE_VERIFICACION no pueden crear usuarios
--    - Verificación manual de documentos por super-admin
--    - CIF y Número SS obligatorios

-- 4. TARJETA SANITARIA:
--    - SOLO campo informativo
--    - NUNCA usar para autenticación
--    - Login siempre con email/password

-- 5. PERMISOS:
--    - Solo ADMIN_ORGANIZACION puede crear profesionales/pacientes
--    - Profesionales solo ven sus pacientes asignados
--    - Pacientes solo ven su propia información

-- 6. AUDITORÍA:
--    - Todos los cambios registran FECHA_ULTIMA_MODIFICACION
--    - ULTIMO_ACCESO se actualiza en cada login
--    - Triggers automáticos para auditoría

-- =====================================================
-- FIN DEL SCRIPT
-- =====================================================