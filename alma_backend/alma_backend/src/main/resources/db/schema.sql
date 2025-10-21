-- =====================================================
-- BASE DE DATOS A.L.M.A. (Asistente de Lenguaje Moderno Adaptativo)
-- Sistema de gestión de organizaciones, profesionales y pacientes
-- =====================================================

-- Eliminar tablas si existen (solo para desarrollo/testing)
-- DROP TABLE IF EXISTS ASIGNACION_PROFESIONAL_PACIENTE;
-- DROP TABLE IF EXISTS SESION_INTERACCION;
-- DROP TABLE IF EXISTS PACIENTE;
-- DROP TABLE IF EXISTS PROFESIONAL;
-- DROP TABLE IF EXISTS USUARIO;
-- DROP TABLE IF EXISTS ORGANIZACION;

-- =====================================================
-- TABLA: ORGANIZACION
-- Almacena información de organizaciones sanitarias
-- =====================================================
CREATE TABLE IF NOT EXISTS ORGANIZACION (
    ID_ORGANIZACION INT AUTO_INCREMENT PRIMARY KEY,

    -- Datos oficiales de la organización
    CIF VARCHAR(9) NOT NULL UNIQUE COMMENT 'CIF de la organización (validado con algoritmo)',
    NUMERO_SEGURIDAD_SOCIAL VARCHAR(20) NOT NULL COMMENT 'Número de inscripción en Seguridad Social',
    NOMBRE_OFICIAL VARCHAR(255) NOT NULL COMMENT 'Nombre completo oficial de la organización',
    DIRECCION VARCHAR(255) NOT NULL COMMENT 'Dirección completa del centro sanitario',
    CODIGO_REGCESS VARCHAR(50) NOT NULL COMMENT 'Código del Registro General de Centros del SNS',
    EMAIL_CORPORATIVO VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email corporativo verificable',
    TELEFONO_CONTACTO VARCHAR(20) NOT NULL COMMENT 'Teléfono oficial de contacto',

    -- Documentos para verificación manual
    DOCUMENTO_CIF_URL VARCHAR(255) COMMENT 'URL del documento CIF escaneado',
    DOCUMENTO_SEGURIDAD_SOCIAL_URL VARCHAR(255) COMMENT 'URL del certificado de Seguridad Social',

    -- Estado de verificación
    ESTADO_VERIFICACION ENUM('PENDIENTE_VERIFICACION', 'EN_REVISION', 'VERIFICADA', 'RECHAZADA')
        NOT NULL DEFAULT 'PENDIENTE_VERIFICACION'
        COMMENT 'Estado del proceso de verificación de la organización',
    MOTIVO_RECHAZO VARCHAR(500) COMMENT 'Motivo del rechazo si ESTADO_VERIFICACION = RECHAZADA',

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro de la organización',
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    INDEX idx_cif (CIF),
    INDEX idx_estado_verificacion (ESTADO_VERIFICACION)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Organizaciones sanitarias registradas en el sistema';

-- =====================================================
-- TABLA: USUARIO
-- Almacena usuarios del sistema (base para todos los tipos)
-- =====================================================
CREATE TABLE IF NOT EXISTS USUARIO (
    ID_USUARIO INT AUTO_INCREMENT PRIMARY KEY,

    -- Datos personales básicos
    DNI VARCHAR(15) NOT NULL UNIQUE COMMENT 'DNI/NIE validado con módulo 23',
    EMAIL VARCHAR(100) NOT NULL UNIQUE COMMENT 'Email único del usuario',
    PASSWORD_HASH VARCHAR(255) NOT NULL COMMENT 'Contraseña hasheada (BCrypt)',
    NOMBRE VARCHAR(50) NOT NULL COMMENT 'Nombre del usuario',
    APELLIDOS VARCHAR(100) COMMENT 'Apellidos del usuario',
    TELEFONO VARCHAR(20) COMMENT 'Teléfono de contacto (validado formato español)',

    -- Tipo y permisos
    TIPO_USUARIO ENUM('PACIENTE', 'PROFESIONAL', 'ADMIN_ORGANIZACION', 'SUPER_ADMIN')
        NOT NULL
        COMMENT 'Tipo de usuario en el sistema',

    -- Relación con organización
    ID_ORGANIZACION INT NOT NULL COMMENT 'Organización a la que pertenece el usuario',

    -- Estado y seguridad
    ACTIVO BOOLEAN DEFAULT TRUE COMMENT 'Usuario activo/inactivo',
    PASSWORD_TEMPORAL BOOLEAN DEFAULT TRUE COMMENT 'Indica si la contraseña es temporal y debe cambiarse',

    -- Campos específicos para ADMIN_ORGANIZACION
    CARGO VARCHAR(100) COMMENT 'Cargo del administrador en la organización',
    DOCUMENTO_CARGO_URL VARCHAR(255) COMMENT 'URL del documento acreditativo del cargo',

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de registro del usuario',
    ULTIMO_ACCESO TIMESTAMP NULL COMMENT 'Fecha del último acceso al sistema',
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Claves foráneas
    FOREIGN KEY (ID_ORGANIZACION) REFERENCES ORGANIZACION(ID_ORGANIZACION)
        ON DELETE RESTRICT ON UPDATE CASCADE,

    INDEX idx_dni (DNI),
    INDEX idx_email (EMAIL),
    INDEX idx_tipo_usuario (TIPO_USUARIO),
    INDEX idx_organizacion (ID_ORGANIZACION),
    INDEX idx_activo (ACTIVO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Usuarios del sistema (base para pacientes, profesionales y administradores)';

-- =====================================================
-- TABLA: PROFESIONAL
-- Extiende USUARIO con información específica de profesionales sanitarios
-- =====================================================
CREATE TABLE IF NOT EXISTS PROFESIONAL (
    ID_PROFESIONAL INT AUTO_INCREMENT PRIMARY KEY,

    -- Relación con usuario base
    ID_USUARIO INT NOT NULL UNIQUE COMMENT 'Usuario base asociado al profesional',

    -- Datos profesionales
    NUMERO_COLEGIADO VARCHAR(20) UNIQUE COMMENT 'Número de colegiado validado',
    ESPECIALIDAD VARCHAR(100) COMMENT 'Especialidad del profesional',
    CENTRO_SALUD VARCHAR(255) COMMENT 'Centro de salud donde trabaja (incluye código REGCESS)',

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Claves foráneas
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_numero_colegiado (NUMERO_COLEGIADO),
    INDEX idx_especialidad (ESPECIALIDAD)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Profesionales sanitarios del sistema';

-- =====================================================
-- TABLA: PACIENTE
-- Extiende USUARIO con información específica de pacientes
-- =====================================================
CREATE TABLE IF NOT EXISTS PACIENTE (
    ID_PACIENTE INT AUTO_INCREMENT PRIMARY KEY,

    -- Relación con usuario base
    ID_USUARIO INT NOT NULL UNIQUE COMMENT 'Usuario base asociado al paciente',

    -- Datos del paciente
    TARJETA_SANITARIA VARCHAR(30) COMMENT 'Número de tarjeta sanitaria (SIP/TSI) - solo informativo',
    FECHA_NACIMIENTO DATE COMMENT 'Fecha de nacimiento del paciente',
    GENERO ENUM('MASCULINO', 'FEMENINO', 'NO_BINARIO', 'PREFIERO_NO_DECIR')
        COMMENT 'Género del paciente (opcional)',

    -- Auditoría
    FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Claves foráneas
    FOREIGN KEY (ID_USUARIO) REFERENCES USUARIO(ID_USUARIO)
        ON DELETE CASCADE ON UPDATE CASCADE,

    INDEX idx_tarjeta_sanitaria (TARJETA_SANITARIA),
    INDEX idx_fecha_nacimiento (FECHA_NACIMIENTO)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Pacientes del sistema';

-- =====================================================
-- TABLA: ASIGNACION_PROFESIONAL_PACIENTE
-- Relación entre profesionales y pacientes
-- =====================================================
CREATE TABLE IF NOT EXISTS ASIGNACION_PROFESIONAL_PACIENTE (
    ID_ASIGNACION INT AUTO_INCREMENT PRIMARY KEY,

    -- Relaciones
    ID_PROFESIONAL INT NOT NULL COMMENT 'Profesional asignado',
    ID_PACIENTE INT NOT NULL COMMENT 'Paciente asignado',

    -- Estado de la asignación
    ACTIVA BOOLEAN DEFAULT TRUE COMMENT 'Asignación activa o finalizada',

    -- Fechas
    FECHA_ASIGNACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Fecha de asignación',
    FECHA_FINALIZACION TIMESTAMP NULL COMMENT 'Fecha de finalización de la asignación',

    -- Notas
    NOTAS TEXT COMMENT 'Notas sobre la asignación',

    -- Auditoría
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Claves foráneas
    FOREIGN KEY (ID_PROFESIONAL) REFERENCES PROFESIONAL(ID_PROFESIONAL)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(ID_PACIENTE)
        ON DELETE CASCADE ON UPDATE CASCADE,

    -- Restricción: no puede haber asignaciones duplicadas activas
    UNIQUE KEY unique_asignacion_activa (ID_PROFESIONAL, ID_PACIENTE, ACTIVA),

    INDEX idx_profesional (ID_PROFESIONAL),
    INDEX idx_paciente (ID_PACIENTE),
    INDEX idx_activa (ACTIVA),
    INDEX idx_fecha_asignacion (FECHA_ASIGNACION)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Asignaciones entre profesionales y pacientes';

-- =====================================================
-- TABLA: SESION_INTERACCION (para futuro uso con IA)
-- Almacena sesiones de interacción con el asistente IA
-- =====================================================
CREATE TABLE IF NOT EXISTS SESION_INTERACCION (
    ID_SESION INT AUTO_INCREMENT PRIMARY KEY,

    -- Relaciones
    ID_PACIENTE INT NOT NULL COMMENT 'Paciente que interactúa',
    ID_PROFESIONAL INT COMMENT 'Profesional supervisor (opcional)',

    -- Datos de la sesión
    FECHA_INICIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT 'Inicio de la sesión',
    FECHA_FIN TIMESTAMP NULL COMMENT 'Fin de la sesión',
    DURACION_SEGUNDOS INT COMMENT 'Duración de la sesión en segundos',

    -- Estado y tipo
    TIPO_SESION ENUM('CONVERSACION', 'EVALUACION', 'TERAPIA')
        DEFAULT 'CONVERSACION'
        COMMENT 'Tipo de sesión con el asistente',
    ESTADO ENUM('ACTIVA', 'FINALIZADA', 'INTERRUMPIDA')
        DEFAULT 'ACTIVA'
        COMMENT 'Estado de la sesión',

    -- Métricas
    NUMERO_MENSAJES INT DEFAULT 0 COMMENT 'Número de mensajes intercambiados',
    SATISFACCION INT COMMENT 'Nivel de satisfacción del paciente (1-5)',

    -- Notas
    NOTAS_PROFESIONAL TEXT COMMENT 'Notas del profesional sobre la sesión',

    -- Auditoría
    FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    -- Claves foráneas
    FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(ID_PACIENTE)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (ID_PROFESIONAL) REFERENCES PROFESIONAL(ID_PROFESIONAL)
        ON DELETE SET NULL ON UPDATE CASCADE,

    INDEX idx_paciente (ID_PACIENTE),
    INDEX idx_profesional (ID_PROFESIONAL),
    INDEX idx_fecha_inicio (FECHA_INICIO),
    INDEX idx_estado (ESTADO),
    INDEX idx_tipo_sesion (TIPO_SESION)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
COMMENT='Sesiones de interacción con el asistente IA';

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
-- DATOS INICIALES (OPCIONAL)
-- =====================================================

-- Insertar organización de prueba (COMENTADO - descomentar si se necesita)
/*
INSERT INTO ORGANIZACION (
    CIF, NUMERO_SEGURIDAD_SOCIAL, NOMBRE_OFICIAL, DIRECCION,
    CODIGO_REGCESS, EMAIL_CORPORATIVO, TELEFONO_CONTACTO,
    ESTADO_VERIFICACION
) VALUES (
    'A12345674', -- CIF válido de ejemplo
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
-- NOTAS SOBRE SEGURIDAD
-- =====================================================
-- 1. Los DNI/NIE deben validarse con el algoritmo módulo 23 en la aplicación
-- 2. Los CIF deben validarse con su algoritmo específico
-- 3. Las contraseñas deben almacenarse hasheadas con BCrypt (mínimo cost 10)
-- 4. Los emails corporativos deben verificarse que pertenezcan al dominio de la organización
-- 5. Los documentos subidos deben almacenarse de forma segura (ej: AWS S3, Azure Blob)
-- 6. La tarjeta sanitaria es SOLO informativa, NO debe usarse para autenticación
-- 7. Implementar 2FA para administradores en producción
-- 8. Los usuarios con PASSWORD_TEMPORAL=true deben cambiar su contraseña en el primer acceso
-- 9. Las organizaciones en estado PENDIENTE_VERIFICACION no pueden crear usuarios hasta ser VERIFICADAS