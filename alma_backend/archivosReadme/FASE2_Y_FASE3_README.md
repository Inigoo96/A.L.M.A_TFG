# FASES 2 Y 3: PROGRESO EMOCIONAL, CITAS Y CHAT - IMPLEMENTACIÓN COMPLETA

## 📋 RESUMEN

Se han implementado las Fases 2 y 3 del sistema A.L.M.A., añadiendo funcionalidades críticas para el seguimiento terapéutico y la comunicación entre profesionales y pacientes.

### 🎯 OBJETIVOS CUMPLIDOS

**FASE 2: PROGRESO EMOCIONAL Y CITAS**
- ✅ Sistema de fases del duelo (Modelo Kübler-Ross)
- ✅ Registro de progreso emocional del paciente
- ✅ Gestión completa de citas profesional-paciente
- ✅ Validaciones automáticas de asignación
- ✅ Control de solapamiento de citas

**FASE 3: CHAT PROFESIONAL-PACIENTE**
- ✅ Sesiones de chat entre profesional y paciente
- ✅ Sistema de mensajería con marca de leído
- ✅ Validación de asignación activa para chatear
- ✅ Gestión de sesiones activas y archivadas
- ✅ Control de última actividad

---

## 📁 ARCHIVOS CREADOS

### 1. SCRIPTS SQL DE MIGRACIÓN

**`bd/V5__Fase2_Progreso_Emocional_Citas.sql`**
- Tabla: `FASE_DUELO` (catálogo de fases del duelo)
- Tabla: `PROGRESO_DUELO` (registro de progreso emocional)
- Tabla: `CITA` (gestión de citas)
- Vistas útiles: `v_progreso_reciente`, `v_citas_proximas`
- Triggers para actualización automática de fechas
- Validaciones: asignación activa y no solapamiento de citas

**`bd/V6__Fase3_Chat_Profesional_Paciente.sql`**
- Tabla: `SESION_CHAT` (agrupa conversaciones)
- Tabla: `MENSAJE_CHAT` (mensajes individuales)
- Vistas útiles: `v_sesiones_chat_activas`, `v_mensajes_chat_completos`, `v_estadisticas_sesion_chat`
- Triggers para última actividad y fecha de lectura
- Validaciones: asignación activa y remitente válido
- Índice único para evitar múltiples sesiones activas

### 2. ENUMERACIONES

```
EstadoEmocional.java: MUY_MAL, MAL, NEUTRAL, BIEN, MUY_BIEN
TipoCita.java: CONSULTA, SEGUIMIENTO, URGENTE
EstadoCita.java: PROGRAMADA, CONFIRMADA, COMPLETADA, CANCELADA
EstadoSesionChat.java: ACTIVA, ARCHIVADA
```

### 3. ENTIDADES JPA

**Fase 2:**
- `FaseDuelo.java` - Catálogo de fases
- `ProgresoDuelo.java` - Registro de progreso
- `Cita.java` - Gestión de citas

**Fase 3:**
- `SesionChat.java` - Sesiones de conversación
- `MensajeChat.java` - Mensajes individuales

### 4. REPOSITORIOS

**Fase 2:**
- `FaseDueloRepository.java`
- `ProgresoDueloRepository.java` (con queries personalizadas por rango de fecha)
- `CitaRepository.java` (con queries para citas próximas y solapamientos)

**Fase 3:**
- `SesionChatRepository.java` (con query para sesión activa única)
- `MensajeChatRepository.java` (con queries para mensajes no leídos)

### 5. SERVICIOS

**Interfaces:**
- `ProgresoDueloService.java`
- `CitaService.java`
- `ChatService.java`

**Implementaciones:**
- `ProgresoDueloServiceImpl.java` - Lógica de progreso emocional
- `CitaServiceImpl.java` - Lógica de gestión de citas con validaciones
- `ChatServiceImpl.java` - Lógica de chat con gestión de sesiones

### 6. DTOs

**Fase 2:**
```
FaseDueloDTO
ProgresoDueloRequestDTO
ProgresoDueloResponseDTO
CitaRequestDTO
CitaResponseDTO
ActualizarEstadoCitaDTO
```

**Fase 3:**
```
SesionChatRequestDTO
SesionChatResponseDTO
MensajeChatRequestDTO
MensajeChatResponseDTO
```

### 7. CONTROLADORES REST

- `ProgresoDueloController.java` - 7 endpoints
- `CitaController.java` - 11 endpoints
- `ChatController.java` - 12 endpoints

---

## 🚀 INSTRUCCIONES DE APLICACIÓN

### PASO 1: Aplicar migraciones de base de datos

```bash
cd C:\SValero\TFG\A.L.M.A_TFG\alma_backend\bd

# Ejecutar script de Fase 2
psql -U postgres -d alma_db -f V5__Fase2_Progreso_Emocional_Citas.sql

# Ejecutar script de Fase 3
psql -U postgres -d alma_db -f V6__Fase3_Chat_Profesional_Paciente.sql
```

**Verificación:**
```sql
-- Verificar tablas creadas
SELECT table_name FROM information_schema.tables
WHERE table_schema = 'public'
AND table_name IN ('FASE_DUELO', 'PROGRESO_DUELO', 'CITA', 'SESION_CHAT', 'MENSAJE_CHAT');

-- Verificar datos iniciales de fases
SELECT * FROM FASE_DUELO ORDER BY ORDEN_FASE;
```

### PASO 2: Compilar el proyecto

```bash
cd C:\SValero\TFG\A.L.M.A_TFG\alma_backend\alma_backend
mvn clean install
```

### PASO 3: Ejecutar la aplicación

```bash
mvn spring-boot:run
```

### PASO 4: Verificar endpoints

**Test rápido con curl:**

```bash
# 1. Obtener todas las fases del duelo (sin autenticación necesaria para ver el catálogo)
curl -X GET http://localhost:8080/api/progreso-duelo/fases

# 2. Crear una cita (requiere token de PROFESIONAL o ADMIN_ORGANIZACION)
curl -X POST http://localhost:8080/api/citas \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idProfesional": 1,
    "fechaHora": "2025-11-15T10:00:00",
    "duracionMinutos": 60,
    "tipoCita": "CONSULTA",
    "motivo": "Primera consulta de seguimiento"
  }'

# 3. Iniciar sesión de chat (requiere token de PROFESIONAL o PACIENTE)
curl -X POST http://localhost:8080/api/chat/sesion \
  -H "Authorization: Bearer {token}" \
  -H "Content-Type: application/json" \
  -d '{
    "idPaciente": 1,
    "idProfesional": 1
  }'
```

---

## 📚 ENDPOINTS DISPONIBLES

### PROGRESO EMOCIONAL (`/api/progreso-duelo`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| POST | `/` | Registrar progreso | PROFESIONAL, PACIENTE |
| GET | `/{id}` | Obtener progreso por ID | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/paciente/{idPaciente}` | Historial del paciente | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/profesional/{idProfesional}` | Progresos registrados por profesional | PROFESIONAL, ADMIN_ORG |
| GET | `/paciente/{id}/rango?fechaInicio=&fechaFin=` | Progresos por rango de fecha | PROFESIONAL, PACIENTE, ADMIN_ORG |
| PUT | `/{id}` | Actualizar progreso | PROFESIONAL, PACIENTE |
| DELETE | `/{id}` | Eliminar progreso | PROFESIONAL, ADMIN_ORG |

### CITAS (`/api/citas`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| POST | `/` | Crear cita | PROFESIONAL, ADMIN_ORG |
| GET | `/{id}` | Obtener cita por ID | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/paciente/{id}` | Citas del paciente | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/profesional/{id}` | Citas del profesional | PROFESIONAL, ADMIN_ORG |
| GET | `/paciente/{id}/proximas` | Próximas citas del paciente | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/profesional/{id}/proximas` | Próximas citas del profesional | PROFESIONAL, ADMIN_ORG |
| GET | `/estado/{estado}` | Citas por estado | PROFESIONAL, ADMIN_ORG |
| PUT | `/{id}/estado` | Actualizar estado (completar, etc.) | PROFESIONAL |
| PUT | `/{id}` | Actualizar cita | PROFESIONAL, ADMIN_ORG |
| PUT | `/{id}/cancelar` | Cancelar cita | PROFESIONAL, PACIENTE, ADMIN_ORG |
| DELETE | `/{id}` | Eliminar cita | PROFESIONAL, ADMIN_ORG |

### CHAT (`/api/chat`)

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| POST | `/sesion` | Iniciar sesión de chat | PROFESIONAL, PACIENTE |
| GET | `/sesion/{id}` | Obtener sesión | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/sesion/activa?idPaciente=&idProfesional=` | Obtener/crear sesión activa | PROFESIONAL, PACIENTE |
| GET | `/sesiones/paciente/{id}` | Sesiones del paciente | PROFESIONAL, PACIENTE, ADMIN_ORG |
| GET | `/sesiones/profesional/{id}` | Sesiones del profesional | PROFESIONAL, ADMIN_ORG |
| GET | `/sesiones/activas` | Todas las sesiones activas | ADMIN_ORG, SUPER_ADMIN |
| PUT | `/sesion/{id}/archivar` | Archivar sesión | PROFESIONAL, PACIENTE |
| POST | `/mensaje` | Enviar mensaje | PROFESIONAL, PACIENTE |
| GET | `/sesion/{id}/mensajes` | Obtener mensajes | PROFESIONAL, PACIENTE, ADMIN_ORG |
| PUT | `/mensaje/{id}/leer` | Marcar mensaje como leído | PROFESIONAL, PACIENTE |
| PUT | `/sesion/{id}/marcar-leidos` | Marcar todos como leídos | PROFESIONAL, PACIENTE |
| GET | `/sesion/{id}/no-leidos` | Contar mensajes no leídos | PROFESIONAL, PACIENTE |

---

## 💡 EJEMPLOS DE USO

### 1. Registrar progreso emocional

```json
POST /api/progreso-duelo
Authorization: Bearer {token_profesional}

{
  "idPaciente": 1,
  "idProfesional": 1,
  "idFaseDuelo": 3,
  "estadoEmocional": "NEUTRAL",
  "notas": "El paciente muestra signos de avance hacia la fase de negociación"
}
```

### 2. Crear cita con validación automática

```json
POST /api/citas
Authorization: Bearer {token_profesional}

{
  "idPaciente": 1,
  "idProfesional": 1,
  "fechaHora": "2025-11-20T15:00:00",
  "duracionMinutos": 60,
  "tipoCita": "SEGUIMIENTO",
  "motivo": "Revisión mensual de progreso"
}
```

**NOTA:** El sistema validará automáticamente:
- Que existe asignación activa entre profesional y paciente
- Que no hay solapamiento con otras citas del profesional

### 3. Completar cita y añadir notas

```json
PUT /api/citas/1/estado
Authorization: Bearer {token_profesional}

{
  "estado": "COMPLETADA",
  "notasSesion": "Sesión productiva. Paciente muestra avances significativos..."
}
```

### 4. Flujo completo de chat

```bash
# A. Iniciar sesión (crea si no existe, devuelve existente si ya hay una activa)
POST /api/chat/sesion
{
  "idPaciente": 1,
  "idProfesional": 1
}
# Respuesta: { "id": 5, "estado": "ACTIVA", ... }

# B. Enviar mensaje (profesional)
POST /api/chat/mensaje
{
  "idSesionChat": 5,
  "mensaje": "Hola, ¿cómo te encuentras hoy?"
}

# C. Enviar respuesta (paciente)
POST /api/chat/mensaje
{
  "idSesionChat": 5,
  "mensaje": "Bien, gracias. He completado las metas de hoy."
}

# D. Obtener todos los mensajes
GET /api/chat/sesion/5/mensajes

# E. Marcar todos como leídos
PUT /api/chat/sesion/5/marcar-leidos
```

---

## 🔒 SEGURIDAD Y VALIDACIONES

### Validaciones a nivel de Base de Datos

1. **Asignación activa para crear citas:**
   - Trigger `trigger_validar_cita_asignacion`
   - Solo se pueden crear citas si existe asignación activa

2. **No solapamiento de citas:**
   - Trigger `trigger_validar_solapamiento`
   - Impide que un profesional tenga citas simultáneas

3. **Asignación activa para chat:**
   - Trigger `trigger_validar_chat_asignacion`
   - Solo pueden chatear si hay asignación activa

4. **Remitente válido en mensajes:**
   - Trigger `trigger_validar_remitente`
   - Verifica que el remitente pertenece a la sesión

5. **Sesión única activa:**
   - Índice único `idx_sesion_unica_activa`
   - Solo una sesión activa por pareja profesional-paciente

### Validaciones a nivel de Servicio

1. **Fechas futuras para citas**
2. **Estados válidos de citas y sesiones**
3. **Mensajes no vacíos**
4. **Existencia de entidades relacionadas**

---

## 🐛 TROUBLESHOOTING

### Error: "No existe asignación activa"

**Causa:** Intento de crear cita o chat sin asignación activa.

**Solución:**
```sql
-- Verificar asignaciones activas
SELECT * FROM ASIGNACION_PROFESIONAL_PACIENTE
WHERE ID_PACIENTE = 1 AND ID_PROFESIONAL = 1 AND ACTIVO = TRUE;

-- Crear asignación si es necesario (vía endpoint correspondiente)
POST /api/asignaciones
```

### Error: "El profesional ya tiene una cita programada en ese horario"

**Causa:** Solapamiento de citas.

**Solución:**
```bash
# Consultar citas del profesional en ese rango
GET /api/citas/profesional/1

# Elegir otro horario disponible
```

### Error: "Table FASE_DUELO doesn't exist"

**Causa:** Scripts SQL no ejecutados.

**Solución:**
```bash
psql -U postgres -d alma_db -f bd/V5__Fase2_Progreso_Emocional_Citas.sql
psql -U postgres -d alma_db -f bd/V6__Fase3_Chat_Profesional_Paciente.sql
```

### Error de compilación en entidades

**Solución:**
```bash
mvn clean compile
```

---

## ✅ CHECKLIST DE VERIFICACIÓN

Antes de continuar con las siguientes fases:

- [ ] Scripts SQL V5 y V6 ejecutados sin errores
- [ ] Tablas FASE_DUELO, PROGRESO_DUELO, CITA, SESION_CHAT, MENSAJE_CHAT existen
- [ ] 5 fases del duelo insertadas correctamente
- [ ] Aplicación compila sin errores
- [ ] Aplicación arranca correctamente
- [ ] Endpoints de progreso funcionan
- [ ] Endpoints de citas funcionan con validaciones
- [ ] Endpoints de chat funcionan
- [ ] Validación de asignación activa funciona
- [ ] No se pueden crear citas solapadas
- [ ] Sistema de mensajes no leídos funciona
- [ ] Solo una sesión activa por pareja

---

## 📊 VISTAS SQL ÚTILES

### Ver progresos recientes con detalles
```sql
SELECT * FROM v_progreso_reciente LIMIT 10;
```

### Ver citas próximas
```sql
SELECT * FROM v_citas_proximas;
```

### Ver sesiones activas con mensajes no leídos
```sql
SELECT * FROM v_sesiones_chat_activas
WHERE MENSAJES_NO_LEIDOS > 0;
```

### Ver mensajes de una sesión con remitentes
```sql
SELECT * FROM v_mensajes_chat_completos
WHERE ID_SESION_CHAT = 1
ORDER BY FECHA_ENVIO;
```

### Estadísticas de sesiones
```sql
SELECT * FROM v_estadisticas_sesion_chat
WHERE ESTADO = 'ACTIVA';
```

---

## 📞 PRÓXIMOS PASOS

Con las Fases 2 y 3 completadas, puedes continuar con:

**FASE 4: CHATBOT IA Y METAS DIARIAS**
- Integración con API de IA (OpenAI/Anthropic)
- Sistema de metas diarias
- Análisis de sentimiento
- Alertas automáticas

**FASE 5: FOROS Y CHAT ENTRE PACIENTES**
- Foros compartidos entre organizaciones
- Chat privado entre pacientes
- Sistema de moderación

**FASE 6: RECURSOS MULTIMEDIA E INFORMES**
- Podcasts, videos, música
- Informes emocionales automáticos
- Sistema de recomendación

---

**FASES 2 Y 3 - IMPLEMENTADAS ✅**

Desarrollado por: Claude Code (Anthropic)
Fecha: 23 de Octubre de 2025
Versión: 1.0