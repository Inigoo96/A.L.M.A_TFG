# ✅ FASE 6: RECURSOS MULTIMEDIA E INFORMES - IMPLEMENTACIÓN COMPLETA

## 📊 **RESUMEN DE IMPLEMENTACIÓN**

La Fase 6 enriquece la plataforma con contenido de apoyo para los pacientes y herramientas de seguimiento para los profesionales. Se ha añadido una biblioteca de recursos multimedia (podcasts, videos, música) y un sistema para generar informes emocionales.

---

## 🗄️ **BASE DE DATOS**

### **Archivo SQL**
- `bd/V9__Fase6_Recursos_Multimedia_Informes.sql`

### **Cambios Realizados**

#### **1. Tablas de Recursos (Nuevas)**
- `PODCAST`: Almacena recursos de audio.
- `VIDEO`: Almacena recursos de video.
- `MUSICA`: Almacena recursos de música.

#### **2. Tabla de Seguimiento (Nueva)**
- `USO_RECURSO`: Registra qué paciente consume qué recurso, permitiendo valoraciones.

#### **3. Tabla de Informes (Nueva)**
- `INFORME_EMOCIONAL`: Contiene informes generados por profesionales sobre el progreso de sus pacientes.

---

## 🏗️ **BACKEND (Java Spring Boot)**

### **Enums Creados**
- `TipoRecurso.java` (PODCAST, VIDEO, MUSICA)
- `GeneradoPor.java` (AUTOMATICO, MANUAL)

### **Entidades JPA**
- `Podcast.java`
- `Video.java`
- `Musica.java`
- `UsoRecurso.java`
- `InformeEmocional.java`

### **Repositorios**
- `PodcastRepository.java`
- `VideoRepository.java`
- `MusicaRepository.java`
- `UsoRecursoRepository.java`
- `InformeEmocionalRepository.java`

### **DTOs Creados**
- `RecursoDTO.java`
- `UsoRecursoRequestDTO.java`
- `UsoRecursoResponseDTO.java`
- `InformeEmocionalRequestDTO.java`
- `InformeEmocionalResponseDTO.java`

### **Servicios**
- `RecursoService.java` + `RecursoServiceImpl.java`
- `InformeEmocionalService.java` + `InformeEmocionalServiceImpl.java`

### **Controladores REST**
- `RecursoController.java`
- `InformeEmocionalController.java`

---

## 🚀 **INSTRUCCIONES DE APLICACIÓN**

### **PASO 1: Aplicar migración de base de datos**
```bash
# Conectar a PostgreSQL y ejecutar:
psql -U postgres -d alma_db -f bd/V9__Fase6_Recursos_Multimedia_Informes.sql
```

### **PASO 2: Reconstruir el proyecto**
```bash
# Desde la raíz del proyecto alma_backend
mvn clean install
```

### **PASO 3: Ejecutar la aplicación**
Inicia `AlmaBackendApplication.java` desde tu IDE.

---

## 📚 **ENDPOINTS DISPONIBLES**

### **Recursos (`/api/recursos`)**

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| GET | `/` | PACIENTE | Obtener la lista de todos los recursos disponibles. |
| GET | `/recomendados/{pacienteId}` | PACIENTE | Obtener recursos recomendados para un paciente. |
| POST | `/uso` | PACIENTE | Registrar el uso y valoración de un recurso. |

### **Informes (`/api/informes`)**

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| POST | `/generar-manual` | PROFESIONAL | Generar un nuevo informe emocional manualmente. |
| GET | `/paciente/{pacienteId}` | PACIENTE, PROFESIONAL | Obtener los informes de un paciente específico. |
| GET | `/profesional/{profesionalId}` | PROFESIONAL | Obtener los informes generados por un profesional. |

---

## 🔒 **SEGURIDAD Y VALIDACIONES**

1.  **Acceso por Rol:** Todos los endpoints están protegidos con `@PreAuthorize`.
    - Los recursos son accesibles principalmente por `PACIENTE`.
    - La generación de informes está restringida a `PROFESIONAL`.
2.  **Propiedad de Datos:** La lógica de los servicios deberá verificar que un paciente solo pueda ver sus propios informes y que un profesional solo pueda gestionar informes de sus pacientes asignados (lógica a reforzar en tests).

---

## ✅ **CHECKLIST DE VERIFICACIÓN**

- [ ] Script `V9__Fase6_Recursos_Multimedia_Informes.sql` ejecutado sin errores.
- [ ] Las nuevas tablas (`PODCAST`, `VIDEO`, `MUSICA`, `USO_RECURSO`, `INFORME_EMOCIONAL`) existen.
- [ ] La aplicación arranca correctamente sin errores.
- [ ] Los endpoints de `/api/recursos` y `/api/informes` están protegidos y responden correctamente según el rol.

---

## ✨ **Conclusión**

La Fase 6 ha sido implementada. Ahora está **lista para ser testeada**. ¡Éxito con los tests! 🚀
