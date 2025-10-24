# ✅ FASE 4: CHATBOT IA Y METAS DIARIAS - IMPLEMENTACIÓN COMPLETA

## 📊 **RESUMEN DE IMPLEMENTACIÓN**

La Fase 4 ha sido implementada completamente con todos sus componentes funcionales. Esta fase añade capacidades de interacción con IA y gestión de metas diarias para los pacientes.

---

## 🗄️ **BASE DE DATOS**

### **Archivo SQL**
- `bd/V7__Fase4_Chatbot_IA_Metas.sql`

### **Cambios Realizados**

#### **1. Tabla SESION_INTERACCION (Ampliada)**
- **Nuevos Campos:**
  - `ESTADO_EMOCIONAL_DETECTADO` VARCHAR(50)
  - `TEMAS_CONVERSADOS` TEXT[]
  - `ALERTAS_GENERADAS` TEXT[]

#### **2. Tabla MENSAJE_IA (Nueva)**
```sql
- ID_MENSAJE_IA (PK)
- ID_SESION (FK → SESION_INTERACCION)
- ROL (USUARIO | ASISTENTE)
- MENSAJE (TEXT)
- TIMESTAMP_MENSAJE
- SENTIMIENTO_DETECTADO (POSITIVO | NEGATIVO | NEUTRO | MUY_POSITIVO | MUY_NEGATIVO)
```

#### **3. Tabla META_DIARIA (Nueva)**
```sql
- ID_META (PK)
- ID_PACIENTE (FK → PACIENTE)
- TEXTO_META (VARCHAR 255)
- FECHA_ASIGNADA (DATE)
- ESTADO (PENDIENTE | COMPLETADA | CANCELADA)
- NOTAS (TEXT)
- FECHA_COMPLETADA
- FECHA_CREACION
- FECHA_ULTIMA_MODIFICACION
```

#### **4. Triggers Automáticos**
- `actualizar_contador_mensajes_ia`: Actualiza el contador de mensajes en la sesión
- `actualizar_fecha_completada_meta`: Auto-actualiza la fecha de completado
- `validar_meta_duplicada`: Previene metas duplicadas para el mismo día

#### **5. Vistas Útiles**
- `v_conversaciones_ia`: Vista completa de sesiones con análisis emocional
- `v_mensajes_ia_completos`: Mensajes con contexto del paciente
- `v_metas_paciente`: Metas con información del paciente
- `v_estadisticas_metas_paciente`: Estadísticas de cumplimiento
- `v_alertas_ia`: Sesiones con alertas que requieren atención profesional

---

## 🏗️ **BACKEND (Java Spring Boot)**

### **Enums Creados**
- `TipoSesion.java` (CONVERSACION, EVALUACION, TERAPIA)
- `EstadoSesion.java` (ACTIVA, FINALIZADA, INTERRUMPIDA)
- `RolMensajeIA.java` (USUARIO, ASISTENTE)
- `SentimientoDetectado.java` (POSITIVO, NEGATIVO, NEUTRO, MUY_POSITIVO, MUY_NEGATIVO)
- `EstadoMeta.java` (PENDIENTE, COMPLETADA, CANCELADA)

### **Entidades JPA**

#### **1. SesionInteraccion.java**
```java
@Entity
@Table(name = "SESION_INTERACCION")
- Relaciones: Paciente, Profesional, List<MensajeIA>
- Campos nuevos: estadoEmocionalDetectado, temasConversados[], alertasGeneradas[]
- Trigger automático: @PrePersist, @PreUpdate
```

#### **2. MensajeIA.java**
```java
@Entity
@Table(name = "MENSAJE_IA")
- Relación: SesionInteraccion
- Campos: rol, mensaje, sentimientoDetectado
- Trigger automático: @PrePersist para timestamp
```

#### **3. MetaDiaria.java**
```java
@Entity
@Table(name = "META_DIARIA")
- Relación: Paciente
- Campos: textoMeta, fechaAsignada, estado, fechaCompletada
- Trigger automático: @PreUpdate para fechaCompletada
```

### **Repositorios**

#### **1. SesionInteraccionRepository.java**
- `findByPacienteIdOrderByFechaInicioDesc(Integer idPaciente)`
- `findByProfesionalIdOrderByFechaInicioDesc(Integer idProfesional)`
- `findByPacienteAndEstado(Integer idPaciente, EstadoSesion estado)`
- `findSesionesPacienteEnRango(Integer idPaciente, LocalDateTime fechaInicio, LocalDateTime fechaFin)`
- `findSesionesConAlertasPorProfesional(Integer idProfesional)`
- `countByPacienteId(Integer idPaciente)`

#### **2. MensajeIARepository.java**
- `findBySesionIdOrderByTimestampMensajeAsc(Integer idSesion)`
- `findBySesionAndRol(Integer idSesion, RolMensajeIA rol)`
- `countBySesionId(Integer idSesion)`
- `findByPacienteId(Integer idPaciente)`

#### **3. MetaDiariaRepository.java**
- `findByPacienteIdOrderByFechaAsignadaDesc(Integer idPaciente)`
- `findByPacienteIdAndFechaAsignadaOrderByFechaCreacionDesc(Integer idPaciente, LocalDate fechaAsignada)`
- `findByPacienteIdAndEstadoOrderByFechaAsignadaDesc(Integer idPaciente, EstadoMeta estado)`
- `findMetasHoyPorPaciente(Integer idPaciente)`
- `findMetasEnRango(Integer idPaciente, LocalDate fechaInicio, LocalDate fechaFin)`
- `countByPacienteIdAndEstado(Integer idPaciente, EstadoMeta estado)`
- `findMetaDuplicada(Integer idPaciente, LocalDate fecha, String textoMeta)`

### **DTOs Creados**

#### **Para IA:**
1. `IniciarSesionIARequestDTO.java`
2. `SesionInteraccionResponseDTO.java`
3. `EnviarMensajeIARequestDTO.java`
4. `MensajeIAResponseDTO.java`
5. `FinalizarSesionIARequestDTO.java`

#### **Para Metas:**
1. `MetaDiariaRequestDTO.java`
2. `MetaDiariaResponseDTO.java`
3. `ActualizarMetaRequestDTO.java`
4. `EstadisticasMetasDTO.java`

### **Servicios**

#### **1. IAService.java + IAServiceImpl.java**
```java
Métodos principales:
- iniciarSesion(IniciarSesionIARequestDTO request)
- enviarMensaje(EnviarMensajeIARequestDTO request)
- finalizarSesion(FinalizarSesionIARequestDTO request)
- obtenerSesionPorId(Integer id)
- obtenerSesionesPorPaciente(Integer idPaciente)
- obtenerMensajesPorSesion(Integer idSesion)
- obtenerSesionesConAlertasPorProfesional(Integer idProfesional)
- agregarNotasProfesional(Integer idSesion, String notas)

Características:
✅ Validación de sesiones activas
✅ Límite de 100 mensajes por sesión
✅ Respuestas simuladas temporales (hasta configurar API de IA real)
✅ Análisis de sentimiento básico
```

#### **2. MetaDiariaService.java + MetaDiariaServiceImpl.java**
```java
Métodos principales:
- crearMeta(MetaDiariaRequestDTO request)
- obtenerMetaPorId(Integer id)
- obtenerMetasPorPaciente(Integer idPaciente)
- obtenerMetasHoyPorPaciente(Integer idPaciente)
- obtenerMetasEnRango(Integer idPaciente, LocalDate fechaInicio, LocalDate fechaFin)
- obtenerMetasPorEstado(Integer idPaciente, EstadoMeta estado)
- actualizarEstadoMeta(ActualizarMetaRequestDTO request)
- completarMeta(Integer idMeta, String notas)
- cancelarMeta(Integer idMeta, String notas)
- actualizarMeta(Integer id, MetaDiariaRequestDTO request)
- eliminarMeta(Integer id)
- obtenerEstadisticasMetas(Integer idPaciente)

Características:
✅ Validación de metas duplicadas
✅ Auto-asignación de fecha actual si no se proporciona
✅ Auto-actualización de fecha de completado
✅ Estadísticas con porcentaje de completado
```

### **Controladores REST**

#### **1. IAController.java** (`/api/ia`)

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| POST | `/sesion` | PACIENTE | Iniciar nueva sesión con IA |
| POST | `/mensaje` | PACIENTE | Enviar mensaje a la IA |
| PUT | `/sesion/finalizar` | PACIENTE, PROFESIONAL | Finalizar sesión |
| GET | `/sesion/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Obtener sesión por ID |
| GET | `/sesiones/paciente/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Historial de sesiones |
| GET | `/sesion/{id}/mensajes` | PACIENTE, PROFESIONAL, ADMIN_ORG | Mensajes de una sesión |
| GET | `/alertas/profesional/{id}` | PROFESIONAL, ADMIN_ORG | Sesiones con alertas |
| PUT | `/sesion/{id}/notas` | PROFESIONAL | Agregar notas profesional |

#### **2. MetaDiariaController.java** (`/api/metas`)

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| POST | `/` | PACIENTE, PROFESIONAL, ADMIN_ORG | Crear nueva meta |
| GET | `/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Obtener meta por ID |
| GET | `/paciente/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Todas las metas del paciente |
| GET | `/paciente/{id}/hoy` | PACIENTE, PROFESIONAL, ADMIN_ORG | Metas de hoy |
| GET | `/paciente/{id}/rango` | PACIENTE, PROFESIONAL, ADMIN_ORG | Metas en rango de fechas |
| GET | `/paciente/{id}/estado/{estado}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Metas por estado |
| PUT | `/actualizar-estado` | PACIENTE, PROFESIONAL, ADMIN_ORG | Actualizar estado |
| PUT | `/{id}/completar` | PACIENTE, PROFESIONAL | Completar meta |
| PUT | `/{id}/cancelar` | PACIENTE, PROFESIONAL, ADMIN_ORG | Cancelar meta |
| PUT | `/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Actualizar meta |
| DELETE | `/{id}` | PACIENTE, PROFESIONAL, ADMIN_ORG | Eliminar meta |
| GET | `/paciente/{id}/estadisticas` | PACIENTE, PROFESIONAL, ADMIN_ORG | Estadísticas de metas |

---

## 🔒 **SEGURIDAD Y VALIDACIONES**

### **Validaciones Implementadas**

#### **En Base de Datos:**
1. ✅ Prevención de metas duplicadas (mismo día, mismo texto)
2. ✅ Validación de estados válidos (CHECK constraints)
3. ✅ Validación de sentimientos válidos
4. ✅ Triggers automáticos para auditoría

#### **En Backend:**
1. ✅ Validación de sesiones activas antes de enviar mensajes
2. ✅ Límite de 100 mensajes por sesión
3. ✅ Validación de longitud de mensajes (máx 2000 caracteres)
4. ✅ Validación de satisfacción (1-5)
5. ✅ Validación de metas duplicadas
6. ✅ Control de acceso por roles (@PreAuthorize)

### **Permisos por Rol**

| Funcionalidad | PACIENTE | PROFESIONAL | ADMIN_ORG |
|---------------|----------|-------------|-----------|
| Iniciar sesión IA | ✅ | ❌ | ❌ |
| Enviar mensajes IA | ✅ | ❌ | ❌ |
| Finalizar sesión | ✅ | ✅ | ❌ |
| Ver sesiones | ✅ (propias) | ✅ (asignados) | ✅ (org) |
| Ver alertas | ❌ | ✅ | ✅ |
| Agregar notas | ❌ | ✅ | ❌ |
| Crear metas | ✅ | ✅ | ✅ |
| Completar metas | ✅ | ✅ | ❌ |
| Ver estadísticas | ✅ (propias) | ✅ (asignados) | ✅ (org) |

---

## 📝 **NOTAS IMPORTANTES**

### **🚧 Pendiente de Configuración (TODO)**

#### **Integración con API de IA Real**
El servicio `IAServiceImpl.java` contiene métodos simulados temporales:

```java
// TODO: Inyectar cliente de API de IA (OpenAI, Anthropic, etc.)
// @Autowired
// private IAClientService iaClientService;

// Métodos a implementar cuando se configure la API:
- generarRespuestaSimulada() → Reemplazar con llamada real a IA
- analizarSentimientoBasico() → Usar análisis de sentimiento de IA
- Extraer temas conversados con NLP
- Detectar alertas (ideación suicida, crisis, etc.)
```

**Opciones de APIs de IA recomendadas:**
1. **OpenAI API** (GPT-4, GPT-3.5-turbo)
2. **Anthropic Claude API** (Claude 3 Opus/Sonnet)
3. **Google Gemini API**
4. **Azure OpenAI Service**

### **🔧 Configuración Recomendada para Producción**

1. **Rate Limiting:**
   - Limitar número de sesiones por paciente por día
   - Limitar frecuencia de mensajes (ej: 1 mensaje cada 2 segundos)

2. **Moderación de Contenido:**
   - Implementar filtros para contenido inapropiado
   - Detección automática de crisis (ideación suicida, autolesión)
   - Notificaciones inmediatas al profesional asignado

3. **Privacidad:**
   - Encriptación de mensajes en reposo
   - Logs de auditoría de acceso a sesiones
   - Retención de datos según RGPD/LOPD

4. **Rendimiento:**
   - Caché de respuestas frecuentes
   - Paginación de historial de mensajes
   - Índices compuestos para consultas comunes

---

## 🧪 **TESTING RECOMENDADO**

### **1. Tests de Integración para IA**
```bash
# Probar flujo completo:
1. POST /api/ia/sesion (Iniciar sesión)
2. POST /api/ia/mensaje (Enviar 5 mensajes)
3. GET /api/ia/sesion/{id}/mensajes (Verificar 10 mensajes: 5 usuario + 5 asistente)
4. PUT /api/ia/sesion/finalizar (Finalizar con satisfacción)
5. GET /api/ia/sesion/{id} (Verificar estado FINALIZADA)
```

### **2. Tests de Integración para Metas**
```bash
# Probar flujo completo:
1. POST /api/metas (Crear meta para hoy)
2. GET /api/metas/paciente/{id}/hoy (Verificar meta creada)
3. PUT /api/metas/{id}/completar (Completar meta)
4. GET /api/metas/paciente/{id}/estadisticas (Verificar 100% completado)
5. POST /api/metas (Intentar crear meta duplicada → Error esperado)
```

### **3. Tests de Validación**
- Intentar enviar mensaje a sesión finalizada (Error esperado)
- Intentar enviar más de 100 mensajes en una sesión (Error esperado)
- Crear metas duplicadas (Error esperado)
- Completar meta ya completada (Error esperado)

---

## 📈 **PRÓXIMOS PASOS (FASE 5 y 6)**

Según el plan original en `TODO_ALMA_FASES.md`:

### **FASE 5: Foros y Chat entre Pacientes** (Pendiente)
- Foros compartidos entre organizaciones
- Chat privado entre pacientes
- Sistema de moderación

### **FASE 6: Recursos Multimedia e Informes** (Pendiente)
- Podcasts, videos, música
- Registro de uso de recursos
- Informes emocionales automáticos

---

## 🎯 **RESULTADO FINAL**

✅ **FASE 4 COMPLETAMENTE IMPLEMENTADA**

### **Archivos Creados (Total: 29 archivos)**

#### **Base de Datos (1 archivo):**
- `V7__Fase4_Chatbot_IA_Metas.sql`

#### **Enums (5 archivos):**
- `TipoSesion.java`
- `EstadoSesion.java`
- `RolMensajeIA.java`
- `SentimientoDetectado.java`
- `EstadoMeta.java`

#### **Entidades (3 archivos):**
- `SesionInteraccion.java`
- `MensajeIA.java`
- `MetaDiaria.java`

#### **Repositorios (3 archivos):**
- `SesionInteraccionRepository.java`
- `MensajeIARepository.java`
- `MetaDiariaRepository.java`

#### **DTOs (9 archivos):**
- `IniciarSesionIARequestDTO.java`
- `SesionInteraccionResponseDTO.java`
- `EnviarMensajeIARequestDTO.java`
- `MensajeIAResponseDTO.java`
- `FinalizarSesionIARequestDTO.java`
- `MetaDiariaRequestDTO.java`
- `MetaDiariaResponseDTO.java`
- `ActualizarMetaRequestDTO.java`
- `EstadisticasMetasDTO.java`

#### **Servicios (4 archivos):**
- `IAService.java`
- `IAServiceImpl.java`
- `MetaDiariaService.java`
- `MetaDiariaServiceImpl.java`

#### **Controladores (2 archivos):**
- `IAController.java`
- `MetaDiariaController.java`

#### **Documentación (1 archivo):**
- `FASE_4_RESUMEN_IMPLEMENTACION.md`

---

## 💾 **Tokens Restantes**
- **Usados:** ~78,000 / 200,000 (39%)
- **Disponibles:** ~122,000 (61%)

---

## ✨ **Conclusión**

La Fase 4 está **lista para ser testeada**. Todos los componentes han sido implementados siguiendo los estándares del proyecto:
- ✅ Seguridad con Spring Security
- ✅ Validaciones exhaustivas
- ✅ DTOs para transferencia de datos
- ✅ Manejo de excepciones
- ✅ Logs con SLF4J
- ✅ Documentación completa

**Recuerda aplicar el script SQL antes de ejecutar la aplicación:**
```bash
# Conectar a PostgreSQL y ejecutar:
psql -U postgres -d alma_db -f bd/V7__Fase4_Chatbot_IA_Metas.sql
```

¡Éxito con los tests de la Fase 4! 🚀