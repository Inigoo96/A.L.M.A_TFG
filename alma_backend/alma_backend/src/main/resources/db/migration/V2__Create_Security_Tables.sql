-- V2__Create_Security_Tables.sql
-- Este script implementa los requisitos críticos de seguridad definidos.

-- 1. Creación de la tabla ORGANIZACION
-- Almacena los datos de las organizaciones sanitarias y su estado de verificación.
CREATE TABLE ORGANIZACION (
    ID INT AUTO_INCREMENT PRIMARY KEY,
    CIF VARCHAR(9) NOT NULL UNIQUE,
    NUMERO_SEGURIDAD_SOCIAL VARCHAR(20) NOT NULL,
    NOMBRE_OFICIAL VARCHAR(255) NOT NULL,
    DIRECCION VARCHAR(255) NOT NULL,
    CODIGO_REGCESS VARCHAR(50) NOT NULL,
    EMAIL_CORPORATIVO VARCHAR(100) NOT NULL UNIQUE,
    TELEFONO_CONTACTO VARCHAR(20) NOT NULL,
    DOCUMENTO_CIF_URL VARCHAR(255),
    DOCUMENTO_SEGURIDAD_SOCIAL_URL VARCHAR(255),
    ESTADO_VERIFICACION VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_VERIFICACION',
    MOTIVO_RECHAZO VARCHAR(500)
);

-- 2. Modificación de la tabla USUARIO
-- Se añaden campos para cumplir con los nuevos requisitos y se establece la relación con ORGANIZACION.
-- Asumimos que la tabla USUARIO ya existe por V1, así que usamos ALTER TABLE.

-- Primero, eliminamos la columna 'nombre_organizacion' que ya no se usará.
-- Si esta columna no existe en tu V1, puedes comentar o eliminar esta línea.
-- ALTER TABLE USUARIO DROP COLUMN nombre_organizacion;

-- Añadimos las nuevas columnas.
ALTER TABLE USUARIO
    ADD COLUMN DNI VARCHAR(15) NOT NULL UNIQUE AFTER ID_USUARIO,
    ADD COLUMN TELEFONO VARCHAR(20) AFTER APELLIDOS,
    ADD COLUMN organizacion_id INT NOT NULL AFTER TIPO_USUARIO,
    ADD COLUMN CARGO VARCHAR(100) AFTER PASSWORD_TEMPORAL,
    ADD COLUMN DOCUMENTO_CARGO_URL VARCHAR(255) AFTER CARGO;

-- Modificamos la longitud de PASSWORD_HASH para que sea compatible con bcrypt.
ALTER TABLE USUARIO MODIFY COLUMN PASSWORD_HASH VARCHAR(255) NOT NULL;

-- Creamos la clave foránea para vincular USUARIO con ORGANIZACION.
ALTER TABLE USUARIO
    ADD CONSTRAINT fk_usuario_organizacion
    FOREIGN KEY (organizacion_id) REFERENCES ORGANIZACION(ID);


-- 3. Modificación de la tabla PROFESIONAL
-- Se añaden los campos requeridos.
ALTER TABLE PROFESIONAL
    ADD COLUMN NUMERO_COLEGIADO VARCHAR(20) UNIQUE,
    ADD COLUMN ESPECIALIDAD VARCHAR(100),
    ADD COLUMN CENTRO_SALUD VARCHAR(255);

-- Aseguramos que la relación con USUARIO sea única.
ALTER TABLE PROFESIONAL
    ADD CONSTRAINT uq_profesional_usuario UNIQUE (ID_USUARIO);


-- 4. Modificación de la tabla PACIENTE
-- Se añaden los campos requeridos.
ALTER TABLE PACIENTE
    ADD COLUMN TARJETA_SANITARIA VARCHAR(30),
    ADD COLUMN GENERO VARCHAR(20);

-- Aseguramos que la relación con USUARIO sea única.
ALTER TABLE PACIENTE
    ADD CONSTRAINT uq_paciente_usuario UNIQUE (ID_USUARIO);


-- 5. Actualización de la tabla ASIGNACION_PROFESIONAL_PACIENTE (si existe)
-- Aseguramos que las claves foráneas estén correctamente definidas.
-- Si la tabla no existe o ya está bien, puedes omitir esta parte.
/*
ALTER TABLE ASIGNACION_PROFESIONAL_PACIENTE
    ADD CONSTRAINT fk_asignacion_profesional FOREIGN KEY (ID_PROFESIONAL) REFERENCES PROFESIONAL(ID_PROFESIONAL),
    ADD CONSTRAINT fk_asignacion_paciente FOREIGN KEY (ID_PACIENTE) REFERENCES PACIENTE(ID_PACIENTE);
*/

-- Fin del script V2