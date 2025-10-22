 ANÁLISIS COMPLETO Y PLAN DE IMPLEMENTACIÓN PARA A.L.M.A.

  He analizado a fondo tu backend y base de datos actual. Tienes una base muy sólida con seguridad robusta, validaciones exhaustivas y una arquitectura bien organizada.

  ---
  RESUMEN DEL ESTADO ACTUAL

  ✅ LO QUE YA TIENES IMPLEMENTADO (MUY BIEN):

  1. Sistema de usuarios multi-rol: SUPER_ADMIN, ADMIN_ORGANIZACION, PROFESIONAL, PACIENTE
  2. Gestión de organizaciones con anti-suplantación y verificación manual
  3. Asignaciones profesional-paciente con soft delete y marcado de asignación principal
  4. Seguridad JWT robusta con Spring Security
  5. Validaciones exhaustivas (DNI, CIF, emails corporativos, etc.)
  6. Estadísticas por organización y profesionales
  7. Auditoría automática con triggers de BD
  8. Tabla SESION_INTERACCION ya creada (pendiente de uso)

  📋 LO QUE FALTA SEGÚN TUS REQUISITOS:

  Basándome en tu descripción y el PDF conceptual:

  Para PACIENTES:
  - ❌ Chatbot IA (tabla ya existe, falta implementación)
  - ❌ Metas diarias
  - ❌ Foros compartidos entre organizaciones
  - ❌ Chat individual con profesional(es)
  - ❌ Chat con otros pacientes
  - ❌ Diarios interactivos
  - ❌ Recursos multimedia (podcasts, videos, música)
  - ❌ Progreso emocional / Fases del duelo
  - ❌ Agenda de citas

  Para PROFESIONALES:
  - ❌ Registro de progreso del paciente en duelo
  - ❌ Chat individual con pacientes
  - ❌ Notas de sesiones
  - ❌ Informes emocionales

  Para ADMIN_ORG:
  - ✅ Crear profesionales y pacientes (YA IMPLEMENTADO)
  - ✅ Crear/desvincular asignaciones (YA IMPLEMENTADO)
  - ✅ Ver estadísticas de su organización (YA IMPLEMENTADO)
  - ❌ Dashboard visual con gráficos

  Para SUPER_ADMIN:
  - ✅ Ver todas las organizaciones (YA IMPLEMENTADO)
  - ✅ Estadísticas globales (YA IMPLEMENTADO)
  - ❌ Gestión de estado de organizaciones (activar, suspender, dar de baja - NUNCA ELIMINAR)
  - ❌ Dashboard de gestión

  ---
  🎯 PLAN DE IMPLEMENTACIÓN POR FASES

  Te propongo un desarrollo incremental y seguro, dividido en 6 fases que puedes implementar de forma independiente:

  ---
  FASE 1: CONSOLIDACIÓN DE GESTIÓN Y ROLES ⭐ (PRIORIDAD ALTA)

  Objetivo: Completar la gestión de organizaciones y mejorar el sistema de roles

  📊 Cambios en Base de Datos:

  -- 1.1: Añadir estado de organización (activa, suspendida, baja)
  ALTER TABLE ORGANIZACION ADD COLUMN ESTADO VARCHAR(20) DEFAULT 'ACTIVA';
  ALTER TABLE ORGANIZACION ADD CONSTRAINT CK_ESTADO_ORG
      CHECK (ESTADO IN ('ACTIVA', 'SUSPENDIDA', 'BAJA'));
  CREATE INDEX idx_organizacion_estado_activo ON ORGANIZACION(ESTADO);

  -- 1.2: Tabla de auditoría de acciones críticas del SuperAdmin
  CREATE TABLE AUDITORIA_ADMIN (
      ID_AUDITORIA SERIAL PRIMARY KEY,
      ID_USUARIO_ADMIN INTEGER NOT NULL,
      TIPO_ACCION VARCHAR(50) NOT NULL, -- 'VERIFICAR_ORG', 'SUSPENDER_ORG', 'RECHAZAR_ORG', etc.
      TABLA_AFECTADA VARCHAR(50),
      ID_REGISTRO_AFECTADO INTEGER,
      DATOS_ANTERIORES JSONB,
      DATOS_NUEVOS JSONB,
      MOTIVO TEXT,
      IP_ORIGEN VARCHAR(45),
      FECHA_ACCION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_AUDITORIA_USUARIO FOREIGN KEY (ID_USUARIO_ADMIN)
          REFERENCES USUARIO(ID_USUARIO) ON DELETE RESTRICT
  );
  CREATE INDEX idx_auditoria_usuario ON AUDITORIA_ADMIN(ID_USUARIO_ADMIN);
  CREATE INDEX idx_auditoria_fecha ON AUDITORIA_ADMIN(FECHA_ACCION);
  CREATE INDEX idx_auditoria_tipo ON AUDITORIA_ADMIN(TIPO_ACCION);

  🔨 Tareas de Backend:

  1. Crear AuditoriaAdminService para registrar acciones críticas
  2. Añadir endpoints en OrganizacionController:
    - PUT /api/organizaciones/{id}/suspender
    - PUT /api/organizaciones/{id}/activar
    - PUT /api/organizaciones/{id}/dar-baja
  3. Actualizar OrganizacionService con validaciones de estado
  4. Crear DTOs: AuditoriaDTO, CambioEstadoOrganizacionDTO

  ---
  FASE 2: PROGRESO EMOCIONAL Y CITAS ⭐⭐ (PRIORIDAD ALTA)

  Objetivo: Registrar el progreso emocional del paciente y gestionar citas

  📊 Cambios en Base de Datos:

  -- 2.1: Fases del duelo
  CREATE TABLE FASE_DUELO (
      ID_FASE SERIAL PRIMARY KEY,
      NOMBRE VARCHAR(50) NOT NULL UNIQUE, -- 'NEGACION', 'IRA', 'NEGOCIACION', 'DEPRESION', 'ACEPTACION'
      DESCRIPCION TEXT,
      ORDEN_FASE INTEGER NOT NULL UNIQUE
  );

  -- 2.2: Progreso emocional del paciente
  CREATE TABLE PROGRESO_DUELO (
      ID_PROGRESO SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      ID_PROFESIONAL INTEGER, -- Quien registró el progreso (puede ser NULL si es auto-registro)
      ID_FASE_DUELO INTEGER NOT NULL,
      ESTADO_EMOCIONAL VARCHAR(50), -- 'MUY_MAL', 'MAL', 'NEUTRAL', 'BIEN', 'MUY_BIEN'
      NOTAS TEXT,
      FECHA_REGISTRO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_PROGRESO_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT FK_PROGRESO_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
          REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE SET NULL,
      CONSTRAINT FK_PROGRESO_FASE FOREIGN KEY (ID_FASE_DUELO)
          REFERENCES FASE_DUELO(ID_FASE)
  );
  CREATE INDEX idx_progreso_paciente ON PROGRESO_DUELO(ID_PACIENTE);
  CREATE INDEX idx_progreso_fecha ON PROGRESO_DUELO(FECHA_REGISTRO);

  -- 2.3: Agenda de citas
  CREATE TABLE CITA (
      ID_CITA SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      ID_PROFESIONAL INTEGER NOT NULL,
      FECHA_HORA TIMESTAMP NOT NULL,
      DURACION_MINUTOS INTEGER DEFAULT 60,
      TIPO_CITA VARCHAR(30) DEFAULT 'CONSULTA', -- 'CONSULTA', 'SEGUIMIENTO', 'URGENTE'
      ESTADO VARCHAR(20) DEFAULT 'PROGRAMADA', -- 'PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA'
      MOTIVO TEXT,
      NOTAS_SESION TEXT, -- Añadido por profesional DESPUÉS de la cita
      FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      FECHA_ULTIMA_MODIFICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_CITA_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT FK_CITA_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
          REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE,
      CONSTRAINT CK_TIPO_CITA CHECK (TIPO_CITA IN ('CONSULTA', 'SEGUIMIENTO', 'URGENTE')),
      CONSTRAINT CK_ESTADO_CITA CHECK (ESTADO IN ('PROGRAMADA', 'CONFIRMADA', 'COMPLETADA', 'CANCELADA'))
  );
  CREATE INDEX idx_cita_paciente ON CITA(ID_PACIENTE);
  CREATE INDEX idx_cita_profesional ON CITA(ID_PROFESIONAL);
  CREATE INDEX idx_cita_fecha ON CITA(FECHA_HORA);
  CREATE INDEX idx_cita_estado ON CITA(ESTADO);

  -- Trigger para actualizar fecha_modificacion
  CREATE TRIGGER trigger_progreso_modificacion BEFORE UPDATE ON PROGRESO_DUELO
      FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

  CREATE TRIGGER trigger_cita_modificacion BEFORE UPDATE ON CITA
      FOR EACH ROW EXECUTE FUNCTION actualizar_fecha_modificacion();

  -- Datos iniciales de fases del duelo (Modelo Kübler-Ross)
  INSERT INTO FASE_DUELO (NOMBRE, DESCRIPCION, ORDEN_FASE) VALUES
  ('NEGACION', 'Fase inicial donde se niega la pérdida', 1),
  ('IRA', 'Sentimientos de frustración y enojo', 2),
  ('NEGOCIACION', 'Intentos de negociar con la situación', 3),
  ('DEPRESION', 'Tristeza profunda ante la pérdida', 4),
  ('ACEPTACION', 'Aceptación de la nueva realidad', 5);

  🔨 Tareas de Backend:

  1. Crear entidades: FaseDuelo, ProgresoDuelo, Cita
  2. Crear servicios y repositorios correspondientes
  3. Crear controladores: ProgresoController, CitaController
  4. Endpoints clave:
    - POST /api/progreso - Profesional registra progreso del paciente
    - GET /api/progreso/paciente/{id} - Ver historial de progreso
    - POST /api/citas - Crear cita
    - GET /api/citas/profesional/{id} - Agenda del profesional
    - GET /api/citas/paciente/{id} - Citas del paciente
    - PUT /api/citas/{id}/completar - Marcar como completada y añadir notas

  ---
  FASE 3: CHAT PROFESIONAL-PACIENTE ⭐⭐ (PRIORIDAD ALTA)

  Objetivo: Comunicación directa entre profesional y paciente

  📊 Cambios en Base de Datos:

  -- 3.1: Sesiones de chat (agrupa mensajes)
  CREATE TABLE SESION_CHAT (
      ID_SESION_CHAT SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      ID_PROFESIONAL INTEGER NOT NULL,
      FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      ULTIMA_ACTIVIDAD TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      ESTADO VARCHAR(20) DEFAULT 'ACTIVA', -- 'ACTIVA', 'ARCHIVADA'

      CONSTRAINT FK_SESION_CHAT_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT FK_SESION_CHAT_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
          REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE,
      -- Solo puede haber una sesión activa entre profesional y paciente
      CONSTRAINT UQ_SESION_ACTIVA UNIQUE (ID_PACIENTE, ID_PROFESIONAL, ESTADO)
  );
  CREATE INDEX idx_sesion_chat_paciente ON SESION_CHAT(ID_PACIENTE);
  CREATE INDEX idx_sesion_chat_profesional ON SESION_CHAT(ID_PROFESIONAL);

  -- 3.2: Mensajes del chat
  CREATE TABLE MENSAJE_CHAT (
      ID_MENSAJE SERIAL PRIMARY KEY,
      ID_SESION_CHAT INTEGER NOT NULL,
      ID_REMITENTE INTEGER NOT NULL, -- ID de USUARIO
      MENSAJE TEXT NOT NULL,
      FECHA_ENVIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      LEIDO BOOLEAN DEFAULT FALSE,
      FECHA_LECTURA TIMESTAMP,

      CONSTRAINT FK_MENSAJE_SESION FOREIGN KEY (ID_SESION_CHAT)
          REFERENCES SESION_CHAT(ID_SESION_CHAT) ON DELETE CASCADE,
      CONSTRAINT FK_MENSAJE_REMITENTE FOREIGN KEY (ID_REMITENTE)
          REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE
  );
  CREATE INDEX idx_mensaje_sesion ON MENSAJE_CHAT(ID_SESION_CHAT);
  CREATE INDEX idx_mensaje_fecha ON MENSAJE_CHAT(FECHA_ENVIO);
  CREATE INDEX idx_mensaje_leido ON MENSAJE_CHAT(LEIDO);

  -- Trigger para actualizar ultima_actividad
  CREATE OR REPLACE FUNCTION actualizar_ultima_actividad_chat()
  RETURNS TRIGGER AS $$
  BEGIN
      UPDATE SESION_CHAT
      SET ULTIMA_ACTIVIDAD = CURRENT_TIMESTAMP
      WHERE ID_SESION_CHAT = NEW.ID_SESION_CHAT;
      RETURN NEW;
  END;
  $$ LANGUAGE plpgsql;

  CREATE TRIGGER trigger_mensaje_actividad AFTER INSERT ON MENSAJE_CHAT
      FOR EACH ROW EXECUTE FUNCTION actualizar_ultima_actividad_chat();

  🔨 Tareas de Backend:

  1. Crear entidades: SesionChat, MensajeChat
  2. Crear ChatService con lógica de sesiones
  3. Crear ChatController:
    - POST /api/chat/iniciar - Crear sesión de chat
    - POST /api/chat/{idSesion}/mensaje - Enviar mensaje
    - GET /api/chat/sesion/{id} - Obtener mensajes
    - GET /api/chat/paciente/{id}/sesiones - Sesiones del paciente
    - PUT /api/chat/mensaje/{id}/marcar-leido - Marcar como leído
  4. OPCIONAL: Implementar WebSocket para mensajes en tiempo real

  ---
  FASE 4: CHATBOT IA Y METAS DIARIAS ⭐⭐⭐ (PRIORIDAD MEDIA)

  Objetivo: Herramientas de apoyo diario para el paciente

  📊 Cambios en Base de Datos:

  -- 4.1: Mejorar tabla SESION_INTERACCION (ya existe, solo ampliar)
  -- Ya está creada, solo añadir campos:
  ALTER TABLE SESION_INTERACCION ADD COLUMN ESTADO_EMOCIONAL_DETECTADO VARCHAR(50);
  ALTER TABLE SESION_INTERACCION ADD COLUMN TEMAS_CONVERSADOS TEXT[]; -- Array de temas
  ALTER TABLE SESION_INTERACCION ADD COLUMN ALERTAS_GENERADAS TEXT[]; -- Alertas para profesional

  -- 4.2: Mensajes de la interacción con IA
  CREATE TABLE MENSAJE_IA (
      ID_MENSAJE_IA SERIAL PRIMARY KEY,
      ID_SESION INTEGER NOT NULL,
      ROL VARCHAR(20) NOT NULL, -- 'USUARIO', 'ASISTENTE'
      MENSAJE TEXT NOT NULL,
      TIMESTAMP_MENSAJE TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      SENTIMIENTO_DETECTADO VARCHAR(30), -- 'POSITIVO', 'NEGATIVO', 'NEUTRO'

      CONSTRAINT FK_MENSAJE_SESION_IA FOREIGN KEY (ID_SESION)
          REFERENCES SESION_INTERACCION(ID_SESION) ON DELETE CASCADE,
      CONSTRAINT CK_ROL_IA CHECK (ROL IN ('USUARIO', 'ASISTENTE'))
  );
  CREATE INDEX idx_mensaje_ia_sesion ON MENSAJE_IA(ID_SESION);

  -- 4.3: Metas diarias
  CREATE TABLE META_DIARIA (
      ID_META SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      TEXTO_META VARCHAR(255) NOT NULL,
      FECHA_ASIGNADA DATE NOT NULL,
      ESTADO VARCHAR(20) DEFAULT 'PENDIENTE', -- 'PENDIENTE', 'COMPLETADA', 'CANCELADA'
      NOTAS TEXT,
      FECHA_COMPLETADA TIMESTAMP,
      FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_META_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT CK_ESTADO_META CHECK (ESTADO IN ('PENDIENTE', 'COMPLETADA', 'CANCELADA'))
  );
  CREATE INDEX idx_meta_paciente ON META_DIARIA(ID_PACIENTE);
  CREATE INDEX idx_meta_fecha ON META_DIARIA(FECHA_ASIGNADA);
  CREATE INDEX idx_meta_estado ON META_DIARIA(ESTADO);

  🔨 Tareas de Backend:

  1. Actualizar entidad SesionInteraccion con nuevos campos
  2. Crear entidades: MensajeIA, MetaDiaria
  3. Crear IAService para integración con API de IA (OpenAI, Anthropic, etc.)
  4. Crear MetaDiariaService y MetaDiariaController
  5. Endpoints:
    - POST /api/ia/sesion - Iniciar conversación con IA
    - POST /api/ia/mensaje - Enviar mensaje a IA
    - GET /api/ia/sesiones/paciente/{id} - Historial
    - POST /api/metas - Crear meta diaria
    - GET /api/metas/hoy - Metas del día
    - PUT /api/metas/{id}/completar - Marcar como completada

  ---
  FASE 5: FOROS Y CHAT ENTRE PACIENTES ⭐⭐ (PRIORIDAD MEDIA)

  Objetivo: Socialización y apoyo entre pacientes

  📊 Cambios en Base de Datos:

  -- 5.1: Foros compartidos entre organizaciones
  CREATE TABLE FORO (
      ID_FORO SERIAL PRIMARY KEY,
      NOMBRE VARCHAR(100) NOT NULL,
      DESCRIPCION TEXT,
      ID_FASE_DUELO INTEGER, -- Foro específico de una fase (puede ser NULL para generales)
      ACTIVO BOOLEAN DEFAULT TRUE,
      FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_FORO_FASE FOREIGN KEY (ID_FASE_DUELO)
          REFERENCES FASE_DUELO(ID_FASE) ON DELETE SET NULL
  );
  CREATE INDEX idx_foro_fase ON FORO(ID_FASE_DUELO);

  -- 5.2: Mensajes en foros
  CREATE TABLE MENSAJE_FORO (
      ID_MENSAJE_FORO SERIAL PRIMARY KEY,
      ID_FORO INTEGER NOT NULL,
      ID_USUARIO INTEGER NOT NULL,
      MENSAJE TEXT NOT NULL,
      FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      EDITADO BOOLEAN DEFAULT FALSE,
      FECHA_EDICION TIMESTAMP,
      MODERADO BOOLEAN DEFAULT FALSE, -- Para control de moderación
      MOTIVO_MODERACION TEXT,

      CONSTRAINT FK_MENSAJE_FORO_FORO FOREIGN KEY (ID_FORO)
          REFERENCES FORO(ID_FORO) ON DELETE CASCADE,
      CONSTRAINT FK_MENSAJE_FORO_USUARIO FOREIGN KEY (ID_USUARIO)
          REFERENCES USUARIO(ID_USUARIO) ON DELETE CASCADE
  );
  CREATE INDEX idx_mensaje_foro_foro ON MENSAJE_FORO(ID_FORO);
  CREATE INDEX idx_mensaje_foro_fecha ON MENSAJE_FORO(FECHA_PUBLICACION);

  -- 5.3: Chat entre pacientes (sesiones privadas)
  CREATE TABLE CHAT_PACIENTES (
      ID_CHAT_PACIENTES SERIAL PRIMARY KEY,
      ID_PACIENTE_1 INTEGER NOT NULL,
      ID_PACIENTE_2 INTEGER NOT NULL,
      FECHA_CREACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      ULTIMA_ACTIVIDAD TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      ESTADO VARCHAR(20) DEFAULT 'ACTIVO',

      CONSTRAINT FK_CHAT_PAC1 FOREIGN KEY (ID_PACIENTE_1)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT FK_CHAT_PAC2 FOREIGN KEY (ID_PACIENTE_2)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      -- Evitar duplicados (orden no importa)
      CONSTRAINT CK_CHAT_DIFERENTE CHECK (ID_PACIENTE_1 < ID_PACIENTE_2),
      CONSTRAINT UQ_CHAT_PACIENTES UNIQUE (ID_PACIENTE_1, ID_PACIENTE_2)
  );

  -- 5.4: Mensajes entre pacientes
  CREATE TABLE MENSAJE_CHAT_PACIENTES (
      ID_MENSAJE SERIAL PRIMARY KEY,
      ID_CHAT_PACIENTES INTEGER NOT NULL,
      ID_REMITENTE_PACIENTE INTEGER NOT NULL,
      MENSAJE TEXT NOT NULL,
      FECHA_ENVIO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      LEIDO BOOLEAN DEFAULT FALSE,
      FECHA_LECTURA TIMESTAMP,

      CONSTRAINT FK_MENSAJE_CHAT_PAC FOREIGN KEY (ID_CHAT_PACIENTES)
          REFERENCES CHAT_PACIENTES(ID_CHAT_PACIENTES) ON DELETE CASCADE,
      CONSTRAINT FK_MENSAJE_REMITENTE_PAC FOREIGN KEY (ID_REMITENTE_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE
  );
  CREATE INDEX idx_mensaje_chat_pac ON MENSAJE_CHAT_PACIENTES(ID_CHAT_PACIENTES);
  CREATE INDEX idx_mensaje_chat_pac_fecha ON MENSAJE_CHAT_PACIENTES(FECHA_ENVIO);

  -- Datos iniciales de foros
  INSERT INTO FORO (NOMBRE, DESCRIPCION, ID_FASE_DUELO) VALUES
  ('General', 'Foro general para todos los pacientes', NULL),
  ('Fase de Negación', 'Apoyo mutuo en la fase de negación', 1),
  ('Fase de Ira', 'Gestión de sentimientos de ira', 2),
  ('Fase de Negociación', 'Espacio para compartir intentos de negociación', 3),
  ('Fase de Depresión', 'Apoyo en momentos difíciles', 4),
  ('Fase de Aceptación', 'Compartir avances y logros', 5);

  🔨 Tareas de Backend:

  1. Crear entidades: Foro, MensajeForo, ChatPacientes, MensajeChatPacientes
  2. Crear servicios con moderación básica
  3. Crear controladores: ForoController, ChatPacientesController
  4. Endpoints:
    - GET /api/foros - Listar foros
    - POST /api/foros/{id}/mensaje - Publicar en foro
    - GET /api/foros/{id}/mensajes - Ver mensajes del foro
    - POST /api/chat-pacientes/iniciar - Iniciar chat privado
    - POST /api/chat-pacientes/{id}/mensaje - Enviar mensaje
  5. IMPORTANTE: Implementar moderación para proteger a pacientes vulnerables

  ---
  FASE 6: RECURSOS MULTIMEDIA E INFORMES ⭐ (PRIORIDAD BAJA)

  Objetivo: Contenido de apoyo y reportes automáticos

  📊 Cambios en Base de Datos:

  -- 6.1: Podcasts
  CREATE TABLE PODCAST (
      ID_PODCAST SERIAL PRIMARY KEY,
      TITULO VARCHAR(150) NOT NULL,
      DESCRIPCION TEXT,
      URL VARCHAR(255) NOT NULL,
      DURACION_MINUTOS INTEGER,
      CATEGORIA VARCHAR(50), -- 'MEDITACION', 'HISTORIA', 'TESTIMONIO', etc.
      ID_FASE_DUELO_RECOMENDADA INTEGER,
      ACTIVO BOOLEAN DEFAULT TRUE,
      FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_PODCAST_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
          REFERENCES FASE_DUELO(ID_FASE)
  );

  -- 6.2: Videos
  CREATE TABLE VIDEO (
      ID_VIDEO SERIAL PRIMARY KEY,
      TITULO VARCHAR(150) NOT NULL,
      DESCRIPCION TEXT,
      URL VARCHAR(255) NOT NULL,
      DURACION_MINUTOS INTEGER,
      CATEGORIA VARCHAR(50),
      ID_FASE_DUELO_RECOMENDADA INTEGER,
      ACTIVO BOOLEAN DEFAULT TRUE,
      FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_VIDEO_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
          REFERENCES FASE_DUELO(ID_FASE)
  );

  -- 6.3: Música
  CREATE TABLE MUSICA (
      ID_MUSICA SERIAL PRIMARY KEY,
      TITULO VARCHAR(150) NOT NULL,
      ARTISTA VARCHAR(100),
      URL VARCHAR(255) NOT NULL,
      DURACION_MINUTOS INTEGER,
      GENERO VARCHAR(50), -- 'RELAJANTE', 'MOTIVADORA', 'REFLEXIVA'
      ID_FASE_DUELO_RECOMENDADA INTEGER,
      ACTIVO BOOLEAN DEFAULT TRUE,
      FECHA_PUBLICACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_MUSICA_FASE FOREIGN KEY (ID_FASE_DUELO_RECOMENDADA)
          REFERENCES FASE_DUELO(ID_FASE)
  );

  -- 6.4: Registro de uso de recursos
  CREATE TABLE USO_RECURSO (
      ID_USO SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      TIPO_RECURSO VARCHAR(20) NOT NULL, -- 'PODCAST', 'VIDEO', 'MUSICA'
      ID_RECURSO INTEGER NOT NULL, -- ID del podcast/video/música
      FECHA_USO TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
      TIEMPO_CONSUMIDO_MINUTOS INTEGER,
      VALORACION INTEGER, -- 1-5 estrellas

      CONSTRAINT FK_USO_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT CK_TIPO_RECURSO CHECK (TIPO_RECURSO IN ('PODCAST', 'VIDEO', 'MUSICA')),
      CONSTRAINT CK_VALORACION CHECK (VALORACION >= 1 AND VALORACION <= 5)
  );
  CREATE INDEX idx_uso_paciente ON USO_RECURSO(ID_PACIENTE);
  CREATE INDEX idx_uso_tipo ON USO_RECURSO(TIPO_RECURSO);

  -- 6.5: Informes emocionales automáticos
  CREATE TABLE INFORME_EMOCIONAL (
      ID_INFORME SERIAL PRIMARY KEY,
      ID_PACIENTE INTEGER NOT NULL,
      ID_PROFESIONAL INTEGER NOT NULL,
      PERIODO_INICIO DATE NOT NULL,
      PERIODO_FIN DATE NOT NULL,
      CONTENIDO_INFORME JSONB, -- Estadísticas y resumen
      GENERADO_POR VARCHAR(20) DEFAULT 'AUTOMATICO', -- 'AUTOMATICO', 'MANUAL'
      FECHA_GENERACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

      CONSTRAINT FK_INFORME_PACIENTE FOREIGN KEY (ID_PACIENTE)
          REFERENCES PACIENTE(ID_PACIENTE) ON DELETE CASCADE,
      CONSTRAINT FK_INFORME_PROFESIONAL FOREIGN KEY (ID_PROFESIONAL)
          REFERENCES PROFESIONAL(ID_PROFESIONAL) ON DELETE CASCADE
  );
  CREATE INDEX idx_informe_paciente ON INFORME_EMOCIONAL(ID_PACIENTE);
  CREATE INDEX idx_informe_fecha ON INFORME_EMOCIONAL(FECHA_GENERACION);

  🔨 Tareas de Backend:

  1. Crear entidades para recursos multimedia
  2. Crear RecursoMultimediaService con sistema de recomendación
  3. Crear InformeEmocionalService con generación automática semanal
  4. Endpoints:
    - GET /api/recursos/podcasts - Listar podcasts
    - GET /api/recursos/videos - Listar videos
    - GET /api/recursos/musica - Listar música
    - POST /api/recursos/uso - Registrar uso de recurso
    - GET /api/recursos/recomendados - Recursos recomendados según fase
    - POST /api/informes/generar - Generar informe manual
    - GET /api/informes/paciente/{id} - Ver informes del paciente

  ---
  📝 PROPUESTA DE SCRIPT SQL POR FASES

  Voy a crear archivos SQL separados para cada fase. Aquí está el primero:

● ---
  🎯 RESUMEN DEL PLAN Y RECOMENDACIONES

  ORDEN DE IMPLEMENTACIÓN RECOMENDADO:

  1. FASE 1 (1-2 semanas): Consolidación de gestión → Completa la gestión del SuperAdmin
  2. FASE 2 (2-3 semanas): Progreso emocional y citas → Core del seguimiento terapéutico
  3. FASE 3 (1-2 semanas): Chat profesional-paciente → Comunicación directa
  4. FASE 4 (3-4 semanas): Chatbot IA y metas → Requiere integración con API externa
  5. FASE 5 (2-3 semanas): Foros y chat pacientes → Funcionalidad social
  6. FASE 6 (1-2 semanas): Recursos multimedia → Contenido complementario

  CONSIDERACIONES IMPORTANTES:

  ✅ HAZLO ASÍ (Mejores prácticas):

  1. Una fase a la vez: Implementa completamente una fase antes de pasar a la siguiente
  2. Testing incremental: Prueba cada fase antes de continuar
  3. Migraciones versionadas: Usa scripts SQL numerados (V3, V4, V5, etc.)
  4. Soft deletes: NUNCA eliminar organizaciones, solo cambiar estado
  5. Auditoría: Registra todas las acciones críticas del SuperAdmin
  6. Validaciones: Mantén el mismo nivel de validación que ya tienes (DNI, CIF, etc.)

  ❌ EVITA ESTO:

  1. NO implementar todo a la vez: Crearás muchos archivos y perderás el control
  2. NO eliminar datos: Usa soft delete (cambiar estado) en vez de DELETE
  3. NO exponer datos sensibles: Los chats deben estar protegidos por rol
  4. NO permitir acceso cruzado entre organizaciones (excepto foros)

  SEGURIDAD ADICIONAL A CONSIDERAR:

  -- Ejemplo de validación a nivel de BD para chat profesional-paciente
  -- Solo pueden chatear si hay asignación activa
  CREATE OR REPLACE FUNCTION validar_asignacion_chat()
  RETURNS TRIGGER AS $$
  BEGIN
      IF NOT EXISTS (
          SELECT 1 FROM ASIGNACION_PROFESIONAL_PACIENTE
          WHERE ID_PACIENTE = NEW.ID_PACIENTE
          AND ID_PROFESIONAL = NEW.ID_PROFESIONAL
          AND activo = TRUE
      ) THEN
          RAISE EXCEPTION 'No existe asignación activa entre este profesional y paciente';
      END IF;
      RETURN NEW;
  END;
  $$ LANGUAGE plpgsql;

  CREATE TRIGGER trigger_validar_chat_asignacion
      BEFORE INSERT ON SESION_CHAT
      FOR EACH ROW EXECUTE FUNCTION validar_asignacion_chat();

  ---
  🚀 ¿CÓMO CONTINUAR?

  Te recomiendo que me pidas implementar UNA FASE A LA VEZ. Por ejemplo:

  OPCIÓN 1 - Conservadora (Recomendada):
  "Implementa la FASE 1 completa: script SQL, entidades JPA, servicios, repositorios y controladores"

  OPCIÓN 2 - Solo Base de Datos primero:
  "Crea los 6 archivos SQL de migración separados (uno por fase) para revisar antes de implementar"

  OPCIÓN 3 - Priorizar funcionalidad específica:
  "Implementa FASE 2 (Progreso emocional) y FASE 3 (Chat profesional-paciente) que son las más críticas"

  MI RECOMENDACIÓN PERSONAL:

  Empieza con FASE 1 → luego FASE 2 + FASE 3 juntas (son complementarias) → después valora si necesitas IA (FASE 4) o prefieres socialización (FASE 5).

  ¿Qué fase quieres que implemente primero? O si prefieres, puedo crear todos los scripts SQL de las 6 fases para que los revises antes de tocar el código Java.
