# FASE 1: CONSOLIDACIÓN DE GESTIÓN Y ROLES - COMPLETADA ✅

## 📋 RESUMEN

La Fase 1 añade al sistema A.L.M.A. la capacidad de gestionar el estado operativo de las organizaciones y mantener una auditoría completa de todas las acciones críticas realizadas por los SUPER_ADMIN.

### 🎯 OBJETIVOS CUMPLIDOS

- ✅ Gestión de estado de organizaciones (ACTIVA, SUSPENDIDA, BAJA)
- ✅ Sistema de auditoría completo con registro automático
- ✅ Endpoints para que SUPER_ADMIN gestione organizaciones
- ✅ Trazabilidad completa (quién, qué, cuándo, desde dónde)
- ✅ NUNCA se eliminan organizaciones (soft delete mediante estado BAJA)

---

## 📁 ARCHIVOS CREADOS/MODIFICADOS

### 1. BASE DE DATOS

**`bd/V3__Fase1_Gestion_Estado_Organizaciones.sql`**
- Nueva tabla: `AUDITORIA_ADMIN`
- Nuevo campo en `ORGANIZACION`: `ESTADO` (VARCHAR(20))
- Vista: `v_auditoria_completa`
- Función y trigger automático: `registrar_auditoria_organizacion()`
- Índices optimizados para consultas

### 2. ENUMERACIONES

**`entity/EstadoOrganizacion.java`**
```java
ACTIVA      // Operando normalmente
SUSPENDIDA  // Suspensión temporal
BAJA        // Baja definitiva (NO se elimina)
```

**`entity/TipoAccionAuditoria.java`**
```java
VERIFICAR_ORGANIZACION
RECHAZAR_ORGANIZACION
SUSPENDER_ORGANIZACION
ACTIVAR_ORGANIZACION
DAR_BAJA_ORGANIZACION
MODIFICAR_ORGANIZACION
CREAR_SUPER_ADMIN
ELIMINAR_USUARIO
MODIFICAR_PERMISOS
```

### 3. ENTIDADES

**`entity/AuditoriaAdmin.java`** (NUEVA)
- Registro completo de acciones críticas
- Soporte JSONB para PostgreSQL (datos antes/después)
- Relación con Usuario (administrador que realizó la acción)
- IP de origen, motivo, timestamp

**`entity/Organizacion.java`** (ACTUALIZADA)
- Añadido campo: `EstadoOrganizacion estado`

### 4. REPOSITORIOS

**`repository/AuditoriaAdminRepository.java`** (NUEVO)
- `findByUsuarioAdmin_IdOrderByFechaAccionDesc()`
- `findByTipoAccionOrderByFechaAccionDesc()`
- `findByOrganizacionId()`
- `findByFechaAccionBetweenOrderByFechaAccionDesc()`
- `findTopNByOrderByFechaAccionDesc()`
- Y más métodos de consulta

**`repository/OrganizacionRepository.java`** (ACTUALIZADO)
- Añadido: `findByEstado(EstadoOrganizacion estado)`

### 5. SERVICIOS

**`service/AuditoriaAdminService.java` + Implementación** (NUEVO)
- `registrarAccion()` - Registra acción de auditoría
- `obtenerAuditoriaPorAdmin()` - Historial de un admin
- `obtenerAuditoriaOrganizacion()` - Historial de una org
- `obtenerUltimasAcciones()` - Últimas N acciones

**`service/OrganizacionService.java` + Implementación** (ACTUALIZADO)
- `cambiarEstadoOrganizacion()` - Cambio genérico de estado
- `suspenderOrganizacion()` - Suspender temporalmente
- `activarOrganizacion()` - Reactivar organización
- `darDeBajaOrganizacion()` - Baja definitiva (NO elimina)
- `findByEstado()` - Buscar por estado

### 6. DTOs

**`dto/CambioEstadoOrganizacionDTO.java`** (NUEVO)
```java
EstadoOrganizacion nuevoEstado
String motivo (obligatorio)
String observaciones (opcional)
```

**`dto/AuditoriaDTO.java`** (NUEVO)
```java
Integer id
TipoAccionAuditoria tipoAccion
String tablaAfectada
Integer idRegistroAfectado
Map<String, Object> datosAnteriores
Map<String, Object> datosNuevos
String motivo
String ipOrigen
LocalDateTime fechaAccion
// Datos del admin que realizó la acción
```

### 7. CONTROLADOR

**`controller/OrganizacionController.java`** (ACTUALIZADO)

**Nuevos endpoints:**

1. **`PUT /api/organizaciones/{id}/suspender`**
   - Suspende temporalmente una organización
   - Requiere: `SUPER_ADMIN`
   - Body: `CambioEstadoOrganizacionDTO`

2. **`PUT /api/organizaciones/{id}/activar`**
   - Activa una organización suspendida
   - Requiere: `SUPER_ADMIN`
   - Body: `CambioEstadoOrganizacionDTO`

3. **`PUT /api/organizaciones/{id}/dar-baja`**
   - Da de baja definitivamente (NO elimina de BD)
   - Requiere: `SUPER_ADMIN`
   - Body: `CambioEstadoOrganizacionDTO` (motivo obligatorio)

4. **`PUT /api/organizaciones/{id}/cambiar-estado`**
   - Cambio genérico de estado
   - Requiere: `SUPER_ADMIN`
   - Body: `CambioEstadoOrganizacionDTO`

5. **`GET /api/organizaciones/estado/{estado}`**
   - Lista organizaciones por estado
   - Requiere: `SUPER_ADMIN`
   - PathParam: `ACTIVA`, `SUSPENDIDA`, `BAJA`

6. **`GET /api/organizaciones/{id}/auditoria`**
   - Historial de auditoría de una organización
   - Requiere: `SUPER_ADMIN`
   - Response: `List<AuditoriaDTO>`

7. **`GET /api/organizaciones/auditoria/recientes?limit=50`**
   - Últimas N acciones de auditoría del sistema
   - Requiere: `SUPER_ADMIN`
   - QueryParam: `limit` (default: 50)

---

## 🚀 INSTRUCCIONES DE APLICACIÓN

### PASO 1: Aplicar migración de base de datos

**OPCIÓN A - PostgreSQL (Recomendado):**

```bash
cd C:\SValero\TFG\A.L.M.A_TFG\alma_backend\bd
psql -U postgres -d alma_db -f V3__Fase1_Gestion_Estado_Organizaciones.sql
```

**OPCIÓN B - Desde pgAdmin:**
1. Abrir pgAdmin
2. Conectar a la base de datos `alma_db`
3. Ejecutar el script `V3__Fase1_Gestion_Estado_Organizaciones.sql`

**IMPORTANTE:** Verifica que no haya errores en la ejecución del script.

### PASO 2: Compilar el proyecto

```bash
cd C:\SValero\TFG\A.L.M.A_TFG\alma_backend\alma_backend
mvn clean install
```

**Si hay errores de compilación:**
- Verifica que todas las dependencias estén correctas
- Asegúrate de tener Java 17
- Ejecuta: `mvn clean compile`

### PASO 3: Ejecutar la aplicación

```bash
mvn spring-boot:run
```

O desde tu IDE (IntelliJ IDEA, Eclipse):
- Ejecutar `AlmaBackendApplication.java`

### PASO 4: Verificar que funciona

**Test 1: Verificar tabla de auditoría**
```sql
SELECT * FROM AUDITORIA_ADMIN;
-- Debería estar vacía pero existir
```

**Test 2: Verificar campo ESTADO en organizaciones**
```sql
SELECT ID_ORGANIZACION, NOMBRE_OFICIAL, ESTADO, ESTADO_VERIFICACION
FROM ORGANIZACION;
-- Todas deberían tener ESTADO = 'ACTIVA'
```

**Test 3: Probar endpoint (requiere token de SUPER_ADMIN)**
```bash
# Obtener organizaciones activas
curl -X GET http://localhost:8080/api/organizaciones/estado/ACTIVA \
  -H "Authorization: Bearer {tu_token_super_admin}"
```

---

## 📚 EJEMPLOS DE USO

### 1. Suspender una organización

**Request:**
```http
PUT /api/organizaciones/1/suspender
Authorization: Bearer {token_super_admin}
Content-Type: application/json

{
  "motivo": "Incumplimiento de pago mensual - 3 meses de retraso",
  "observaciones": "Contactar antes de reactivar"
}
```

**Response:**
```json
{
  "id": 1,
  "nombreOficial": "Centro de Salud Ejemplo",
  "cif": "A12345674",
  "estado": "SUSPENDIDA",
  "estadoVerificacion": "VERIFICADA",
  ...
}
```

**Qué sucede internamente:**
1. Se cambia el estado de la organización a `SUSPENDIDA`
2. Se registra en `AUDITORIA_ADMIN`:
   - Usuario que realizó la acción
   - IP de origen
   - Datos anteriores (estado: ACTIVA)
   - Datos nuevos (estado: SUSPENDIDA)
   - Motivo proporcionado
   - Timestamp

### 2. Dar de baja una organización

**Request:**
```http
PUT /api/organizaciones/2/dar-baja
Authorization: Bearer {token_super_admin}
Content-Type: application/json

{
  "motivo": "Cierre definitivo del centro - Notificado por la dirección",
  "observaciones": "Mantener datos históricos por normativa RGPD"
}
```

**Response:**
```json
{
  "id": 2,
  "nombreOficial": "Clínica San José",
  "estado": "BAJA",
  ...
}
```

**IMPORTANTE:** La organización NO se elimina de la base de datos, solo cambia su `ESTADO` a `BAJA`.

### 3. Ver historial de auditoría de una organización

**Request:**
```http
GET /api/organizaciones/1/auditoria
Authorization: Bearer {token_super_admin}
```

**Response:**
```json
[
  {
    "id": 15,
    "tipoAccion": "SUSPENDER_ORGANIZACION",
    "tablaAfectada": "ORGANIZACION",
    "idRegistroAfectado": 1,
    "datosAnteriores": {
      "ESTADO": "ACTIVA",
      "NOMBRE_OFICIAL": "Centro de Salud Ejemplo",
      "CIF": "A12345674"
    },
    "datosNuevos": {
      "ESTADO": "SUSPENDIDA",
      "NOMBRE_OFICIAL": "Centro de Salud Ejemplo",
      "CIF": "A12345674"
    },
    "motivo": "Incumplimiento de pago mensual - 3 meses de retraso",
    "ipOrigen": "192.168.1.100",
    "fechaAccion": "2025-10-22T14:30:00",
    "emailAdmin": "admin@alma.com",
    "nombreAdmin": "Juan",
    "apellidosAdmin": "Pérez"
  },
  {
    "id": 8,
    "tipoAccion": "VERIFICAR_ORGANIZACION",
    "tablaAfectada": "ORGANIZACION",
    "idRegistroAfectado": 1,
    ...
  }
]
```

### 4. Ver últimas acciones del sistema

**Request:**
```http
GET /api/organizaciones/auditoria/recientes?limit=10
Authorization: Bearer {token_super_admin}
```

**Response:**
```json
[
  // Las 10 acciones más recientes de todos los SUPER_ADMIN
]
```

---

## 🔒 SEGURIDAD

### Validaciones implementadas:

1. **Solo SUPER_ADMIN puede:**
   - Cambiar estado de organizaciones
   - Ver auditoría completa
   - Consultar organizaciones por estado

2. **Motivo obligatorio:**
   - Todos los cambios de estado requieren justificación
   - Se valida que el motivo no esté vacío
   - Para BAJA es especialmente crítico

3. **Trazabilidad completa:**
   - Se registra IP de origen
   - Usuario que realiza la acción
   - Timestamp exacto
   - Estado anterior y nuevo (en JSON)

4. **Soft delete:**
   - Las organizaciones NUNCA se eliminan
   - Solo cambian a estado BAJA
   - Se mantienen datos históricos

### Recomendaciones:

- **NO usar el endpoint `DELETE /api/organizaciones/{id}`** para dar de baja organizaciones
- Usar siempre `PUT /api/organizaciones/{id}/dar-baja`
- Revisar periódicamente la tabla `AUDITORIA_ADMIN`
- Establecer políticas de retención de auditoría según normativa

---

## 🐛 TROUBLESHOOTING

### Error: "Column 'estado' does not exist"
**Solución:** Ejecuta el script SQL de migración V3.

### Error: "Table 'auditoria_admin' doesn't exist"
**Solución:** Verifica que el script SQL se ejecutó correctamente.

### Error de compilación: "Cannot find symbol EstadoOrganizacion"
**Solución:**
```bash
mvn clean compile
```

### Error: "Usuario autenticado no encontrado"
**Solución:** Verifica que el token JWT sea válido y contenga el email del usuario.

### Error: "IP origen es null"
**Solución:** Normal en desarrollo local. En producción configurar proxy correctamente.

---

## ✅ CHECKLIST DE VERIFICACIÓN

Antes de pasar a FASE 2, verifica:

- [ ] Script SQL ejecutado sin errores
- [ ] Tabla `AUDITORIA_ADMIN` existe
- [ ] Campo `ESTADO` existe en tabla `ORGANIZACION`
- [ ] Todas las organizaciones tienen `ESTADO = 'ACTIVA'`
- [ ] Aplicación compila sin errores
- [ ] Aplicación arranca correctamente
- [ ] Endpoint `/api/organizaciones/estado/ACTIVA` funciona
- [ ] Al suspender una organización se crea registro en auditoría
- [ ] Endpoint de auditoría retorna datos correctos

---

## 📊 PRÓXIMOS PASOS

Una vez completada la FASE 1, puedes continuar con:

**FASE 2: PROGRESO EMOCIONAL Y CITAS**
- Fases del duelo (tabla FASE_DUELO)
- Registro de progreso emocional (tabla PROGRESO_DUELO)
- Agenda de citas profesional-paciente (tabla CITA)

**O bien:**

**FASE 3: CHAT PROFESIONAL-PACIENTE**
- Sesiones de chat
- Mensajes con marca de leído
- WebSocket en tiempo real (opcional)

---

## 📞 SOPORTE

Si encuentras problemas durante la implementación:

1. Revisa los logs de la aplicación
2. Verifica que el script SQL se ejecutó completamente
3. Comprueba que tienes las versiones correctas (Java 17, PostgreSQL 12+)
4. Revisa que todos los archivos se hayan creado correctamente

---

**FASE 1 - COMPLETADA ✅**

Desarrollado por: Claude Code (Anthropic)
Fecha: 22 de Octubre de 2025
Versión: 1.0