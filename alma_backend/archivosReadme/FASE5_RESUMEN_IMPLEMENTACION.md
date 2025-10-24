# ✅ FASE 5: FOROS Y CHAT ENTRE PACIENTES - IMPLEMENTACIÓN COMPLETA

## 📊 **RESUMEN DE IMPLEMENTACIÓN**

La Fase 5 ha sido implementada para dotar a la plataforma de funcionalidades de comunidad y apoyo mutuo entre pacientes. Se han añadido foros de discusión temáticos y un sistema de chat privado entre pacientes, todo ello protegido para que solo los usuarios con rol de paciente puedan acceder.

---

## 🗄️ **BASE DE DATOS**

### **Archivo SQL**
- `bd/V8__Fase5_Foros_Y_Chat_Pacientes.sql`

### **Cambios Realizados**

#### **1. Tabla FORO (Nueva)**
- Almacena los foros de discusión, con la posibilidad de asociarlos a una fase de duelo específica.

#### **2. Tabla MENSAJE_FORO (Nueva)**
- Contiene los mensajes que los usuarios publican en los foros.

#### **3. Tabla CHAT_PACIENTES (Nueva)**
- Define una sesión de chat privado entre dos pacientes, con una constraint que evita sesiones duplicadas.

#### **4. Tabla MENSAJE_CHAT_PACIENTES (Nueva)**
- Almacena los mensajes individuales de los chats privados entre pacientes.

#### **5. Triggers y Validaciones**
- `actualizar_ultima_actividad_chat_pacientes`: Actualiza el timestamp de la última actividad en un chat.
- `validar_remitente_chat_pacientes`: Asegura que solo un participante del chat pueda enviar mensajes en él.

#### **6. Datos Iniciales**
- Se han insertado 6 foros iniciales: uno general y cinco específicos para cada fase del duelo.

---

## 🏗️ **BACKEND (Java Spring Boot)**

### **Enums Creados**
- `EstadoChatPacientes.java` (ACTIVO, BLOQUEADO, ARCHIVADO)

### **Entidades JPA**
- `Foro.java`
- `MensajeForo.java`
- `ChatPacientes.java`
- `MensajeChatPacientes.java`

### **Repositorios**
- `ForoRepository.java`
- `MensajeForoRepository.java`
- `ChatPacientesRepository.java`
- `MensajeChatPacientesRepository.java`

### **DTOs Creados**
- `ForoDTO.java`
- `MensajeForoRequestDTO.java`
- `MensajeForoResponseDTO.java`
- `ChatPacientesRequestDTO.java`
- `ChatPacientesResponseDTO.java`
- `MensajeChatPacientesRequestDTO.java`
- `MensajeChatPacientesResponseDTO.java`

### **Servicios**
- `ForoService.java` + `ForoServiceImpl.java`
- `ChatPacientesService.java` + `ChatPacientesServiceImpl.java`

### **Controladores REST**
- `ForoController.java`
- `ChatPacientesController.java`

---

## 🚀 **INSTRUCCIONES DE APLICACIÓN**

### **PASO 1: Aplicar migración de base de datos**
```bash
# Conectar a PostgreSQL y ejecutar:
psql -U postgres -d alma_db -f bd/V8__Fase5_Foros_Y_Chat_Pacientes.sql
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

### **Foros (`/api/foros`)**

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| GET | `/` | PACIENTE | Obtener la lista de todos los foros activos. |
| GET | `/{foroId}/mensajes` | PACIENTE | Obtener todos los mensajes de un foro específico. |
| POST | `/mensajes` | PACIENTE | Publicar un nuevo mensaje en un foro. |

### **Chat entre Pacientes (`/api/chat-pacientes`)**

| Método | Endpoint | Roles | Descripción |
|--------|----------|-------|-------------|
| POST | `/sesion` | PACIENTE | Iniciar o recuperar una sesión de chat entre dos pacientes. |
| GET | `/{chatId}/mensajes` | PACIENTE | Obtener los mensajes de un chat específico. |
| POST | `/mensajes` | PACIENTE | Enviar un mensaje en un chat. |

---

## 🔒 **SEGURIDAD Y VALIDACIONES**

1.  **Acceso por Rol:** Todos los endpoints de la Fase 5 están protegidos con `@PreAuthorize("hasRole('PACIENTE')")`, asegurando que solo los pacientes autenticados puedan acceder a estas funcionalidades.
2.  **Validación de Pertenencia:** A nivel de base de datos, un trigger (`validar_remitente_chat_pacientes`) impide que un usuario envíe mensajes a un chat del que no es participante.
3.  **Consistencia de Datos:** Se utiliza una constraint `CHECK (ID_PACIENTE_1 < ID_PACIENTE_2)` y una `UNIQUE` en la tabla `CHAT_PACIENTES` para evitar sesiones de chat duplicadas entre los mismos dos usuarios.

---

## 🐛 **TROUBLESHOOTING**

### **Error: 403 Forbidden / Access Denied**
- **Causa:** Estás intentando acceder a los endpoints con un token de un usuario que no tiene el rol `PACIENTE`.
- **Solución:** Asegúrate de que el token JWT utilizado en la cabecera `Authorization` pertenece a un usuario paciente.

### **Error: "Foro no encontrado" o "Paciente no encontrado"**
- **Causa:** El ID proporcionado en la petición no existe en la base de datos.
- **Solución:** Verifica que los IDs (de foro, usuario, paciente, etc.) son correctos y existen en sus respectivas tablas.

---

## ✅ **CHECKLIST DE VERIFICACIÓN**

- [ ] Script `V8__Fase5_Foros_Y_Chat_Pacientes.sql` ejecutado sin errores.
- [ ] Las tablas `FORO`, `MENSAJE_FORO`, `CHAT_PACIENTES` y `MENSAJE_CHAT_PACIENTES` existen.
- [ ] La tabla `FORO` contiene los 6 registros iniciales.
- [ ] La aplicación arranca correctamente sin errores de bean o dependencia.
- [ ] Los endpoints de `/api/foros` y `/api/chat-pacientes` devuelven 403 si no se usa un token de paciente.
- [ ] Se puede obtener la lista de foros con un token de paciente.
- [ ] Se puede crear y obtener una sesión de chat entre dos pacientes.

---

## 📈 **PRÓXIMOS PASOS (FASE 6)**

Según el plan original en `TODO_ALMA_FASES.md`:

### **FASE 6: Recursos Multimedia e Informes** (Pendiente)
- Podcasts, videos, música.
- Registro de uso de recursos.
- Informes emocionales automáticos.

---

## ✨ **Conclusión**

La Fase 5 está **lista para ser testeada**. Todos los componentes han sido implementados, documentados y securizados. ¡Éxito con los tests! 🚀
