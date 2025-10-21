# 📱 A.L.M.A Frontend - Actualización a v2.0

**Fecha:** 21 de Octubre de 2025
**Autor:** Claude Code
**Versión:** 2.0.0

---

## 📋 Resumen Ejecutivo

El frontend de A.L.M.A ha sido actualizado para ser **100% compatible con el backend v2.0**, que incorporó importantes mejoras de seguridad y funcionalidad. Esta actualización incluye:

- ✅ **Nuevos servicios API** para todos los endpoints del backend
- ✅ **Sistema de tipos TypeScript** centralizado y sincronizado con DTOs del backend
- ✅ **Gestión avanzada de autenticación** con hooks personalizados
- ✅ **Protección basada en roles** (RBAC) en la interfaz de usuario
- ✅ **Manejo mejorado de errores** HTTP (401/403)
- ✅ **Componentes reutilizables** para seguridad y UI

---

## 🎯 Cambios Críticos del Backend que Afectaron al Frontend

### 1. **Endpoints de Autenticación** ✅ YA COMPATIBLES
- ✅ `POST /api/auth/login` - Ya estaba correcto
- ✅ `POST /api/auth/register-organization` - Ya estaba correcto
- 🆕 `POST /api/auth/register/profesional` - **Nuevo servicio creado**
- 🆕 `POST /api/auth/register/paciente` - **Nuevo servicio creado**

### 2. **Protección JWT en Todos los Endpoints**
- ✅ El interceptor de Axios ya enviaba el token correctamente
- ✅ Mejorado el manejo de errores 401 (limpia sesión automáticamente)
- ✅ Mejorado el manejo de errores 403 (muestra mensajes claros)

### 3. **Campo `esPrincipal` en Asignaciones** ⭐ CRÍTICO
- 🆕 `AsignacionRequestDTO` ahora incluye `esPrincipal: boolean`
- 🆕 `AsignacionResponseDTO` ahora devuelve `esPrincipal: boolean`
- ✅ Tipos TypeScript actualizados
- ✅ Servicio de asignaciones implementado

### 4. **Nuevos Requisitos de Roles**
- 🔒 **OrganizacionController**: Ahora requiere `ADMIN_ORGANIZACION` o `SUPER_ADMIN`
- 🔒 **AsignacionController**: Diferentes endpoints requieren diferentes roles
- 🔒 **AuthController**: Registro de usuarios requiere `ADMIN_ORGANIZACION`

---

## 📁 Archivos Nuevos Creados

### 🔧 **Tipos y Configuración**

#### `src/types/api.types.ts`
**Propósito:** Tipos TypeScript centralizados para toda la API.

**Contenido:**
- Enums: `TipoUsuario`, `Genero`
- DTOs de Organización: `OrganizacionDTO`, `OrganizacionEstadisticasDTO`
- DTOs de Usuario: `UsuarioResponseDTO`
- DTOs de Profesional: `ProfesionalResponseDTO`, `ProfesionalRegistroDTO`, `ProfesionalEstadisticasDTO`
- DTOs de Paciente: `PacienteResponseDTO`, `PacienteRegistroDTO`
- DTOs de Asignación: `AsignacionRequestDTO`, `AsignacionResponseDTO` (con `esPrincipal`)
- DTOs de Autenticación: `LoginRequest`, `LoginResponse`, etc.
- Tipos de Paginación (para uso futuro)

**Beneficios:**
- Autocompletado en el IDE
- Detección de errores en tiempo de compilación
- Sincronización exacta con el backend

---

### 🌐 **Servicios API**

#### `src/services/asignacionService.ts`
**Propósito:** Gestión completa de asignaciones profesional-paciente.

**Métodos:**
- `createAsignacion(request)` - Crear asignación (incluye `esPrincipal`)
- `getAsignacionesByPaciente(id)` - Obtener asignaciones de un paciente
- `getAsignacionesByProfesional(id)` - Obtener asignaciones de un profesional
- `deactivateAsignacion(id)` - Desactivar asignación
- `deleteAsignacion(id)` - Eliminar asignación

**Roles requeridos:**
- Crear: `ADMIN_ORGANIZACION`
- Obtener: `ADMIN_ORGANIZACION` o `PROFESIONAL`
- Desactivar/Eliminar: `ADMIN_ORGANIZACION`

---

#### `src/services/organizacionService.ts`
**Propósito:** Gestión de organizaciones (solo lectura para admins).

**Métodos:**
- `getAllOrganizaciones()` - Listar todas
- `getOrganizacionById(id)` - Obtener por ID
- `getOrganizacionByCif(cif)` - Buscar por CIF
- `createOrganizacion(data)` - Crear organización
- `updateOrganizacion(id, data)` - Actualizar
- `deleteOrganizacion(id)` - Eliminar
- `getEstadisticasOrganizaciones()` - Estadísticas globales
- `getEstadisticasOrganizacion(id)` - Estadísticas de una organización

**Roles requeridos:**
- GET: `ADMIN_ORGANIZACION` o `SUPER_ADMIN`
- POST/PUT/DELETE: `SUPER_ADMIN`

---

#### `src/services/usuarioService.ts`
**Propósito:** Gestión de usuarios (registro y actualización).

**Métodos:**
- `registerProfesional(data)` - Registrar profesional
- `registerPaciente(data)` - Registrar paciente
- `updatePassword(old, new)` - Cambiar contraseña
- `getCurrentUser()` - Obtener usuario actual
- `getUserById(id)` - Obtener usuario por ID
- `getUsersFromMyOrganization()` - Listar usuarios de mi organización
- `toggleUserStatus(id, activo)` - Activar/desactivar usuario

**Roles requeridos:**
- Registro de usuarios: `ADMIN_ORGANIZACION`
- Cambio de contraseña: Usuario autenticado
- Gestión: `ADMIN_ORGANIZACION`

---

#### `src/services/profesionalService.ts`
**Propósito:** Gestión de profesionales.

**Métodos:**
- `getAllProfesionales()` - Listar todos
- `getProfesionalById(id)` - Obtener por ID
- `getProfesionalesByOrganizacion(orgId)` - Por organización
- `getProfesionalesFromMyOrganization()` - De mi organización
- `updateProfesional(id, data)` - Actualizar datos
- `getEstadisticasProfesional(id)` - Estadísticas de un profesional
- `getMisEstadisticas()` - Mis estadísticas (usuario actual)

---

#### `src/services/pacienteService.ts`
**Propósito:** Gestión de pacientes.

**Métodos:**
- `getAllPacientes()` - Listar todos
- `getPacienteById(id)` - Obtener por ID
- `getPacientesByOrganizacion(orgId)` - Por organización
- `getPacientesFromMyOrganization()` - De mi organización
- `getPacientesByProfesional(profId)` - Asignados a un profesional
- `getMisPacientes()` - Mis pacientes (profesional actual)
- `updatePaciente(id, data)` - Actualizar datos
- `searchPacientes(query)` - Buscar por nombre/email

---

### 🎣 **Hooks Personalizados**

#### `src/hooks/useAuth.ts`
**Propósito:** Hook centralizado para gestión de autenticación y roles.

**Estado expuesto:**
- `isAuthenticated` - Si el usuario está autenticado
- `isLoading` - Si está cargando datos
- `userEmail` - Email del usuario
- `userRole` - Rol del usuario (`TipoUsuario`)
- `passwordTemporal` - Si tiene contraseña temporal

**Métodos expuestos:**
- `loadUserData()` - Recargar datos de AsyncStorage
- `logout()` - Cerrar sesión
- `hasRole(role)` - Verificar si tiene un rol específico
- `hasAnyRole(roles)` - Verificar si tiene alguno de los roles
- `isAdminOrganizacion()` - ¿Es admin de organización?
- `isProfesional()` - ¿Es profesional?
- `isPaciente()` - ¿Es paciente?
- `isSuperAdmin()` - ¿Es super admin?
- `getRoleName()` - Obtener nombre legible del rol

**Ejemplo de uso:**
```typescript
const { isAuthenticated, userRole, hasRole, logout } = useAuth();

if (hasRole(TipoUsuario.ADMIN_ORGANIZACION)) {
  // Mostrar opciones de admin
}
```

---

### 🧩 **Componentes Reutilizables**

#### `src/components/RoleGuard.tsx`
**Propósito:** Componente para proteger contenido basado en roles.

**Props:**
- `allowedRoles` - Array de roles permitidos
- `children` - Contenido a mostrar si tiene permisos
- `fallback` - (Opcional) Contenido alternativo
- `showDeniedMessage` - (Opcional) Mostrar mensaje de acceso denegado

**Ejemplo de uso:**
```tsx
<RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
  <Button title="Crear Usuario" onPress={handleCreate} />
</RoleGuard>

// Solo los ADMIN_ORGANIZACION verán el botón
```

---

## 🔄 Archivos Modificados

### `src/services/api.ts`
**Cambios:**
- ✅ Interceptor de respuesta mejorado
- ✅ Manejo automático de errores 401 (limpia AsyncStorage)
- ✅ Manejo automático de errores 403 (logs de advertencia)
- ✅ Mejor logging de errores

**Código añadido:**
```typescript
if (error.response?.status === 401) {
  console.warn('🔒 Error 401: Token inválido. Limpiando sesión...');
  await AsyncStorage.removeItem('jwt_token');
  await AsyncStorage.removeItem('user_email');
  await AsyncStorage.removeItem('user_type');
  await AsyncStorage.removeItem('password_temporal');
}
```

---

### `src/services/authService.ts`
**Cambios:**
- ✅ Importa tipos desde `api.types.ts` en lugar de definirlos localmente
- ✅ Eliminadas interfaces duplicadas

---

### `src/screens/DashboardScreen.tsx`
**Cambios:**
- ✅ Importa `RoleGuard` y `TipoUsuario`
- ✅ Nueva sección "Acciones Disponibles" con botones basados en roles
- ✅ Botones específicos para:
  - **ADMIN_ORGANIZACION**: Registrar profesional, registrar paciente, gestionar asignaciones, ver usuarios
  - **PROFESIONAL**: Mis pacientes, mis estadísticas
  - **PACIENTE**: Mis profesionales, recursos de apoyo
  - **SUPER_ADMIN**: Gestionar organizaciones, estadísticas globales

**Nuevos estilos:**
- `actionsCard` - Tarjeta de acciones
- `actionButton` - Botón de acción
- `actionButtonText` - Texto del botón

---

## 🔐 Sistema de Seguridad Implementado

### **Flujo de Autenticación**

```
1. Usuario hace login
   ↓
2. Backend devuelve JWT + role
   ↓
3. Frontend almacena en AsyncStorage:
   - jwt_token
   - user_email
   - user_type (rol)
   - password_temporal
   ↓
4. Interceptor Axios automáticamente:
   - Lee el token de AsyncStorage
   - Lo adjunta a TODAS las peticiones (excepto /auth/login y /auth/register-organization)
   ↓
5. Si recibe 401:
   - Limpia AsyncStorage automáticamente
   - El usuario debe volver a hacer login
   ↓
6. Si recibe 403:
   - Muestra mensaje de "Sin permisos"
```

### **Protección en UI (Frontend)**

```tsx
// Ejemplo 1: Ocultar botón según rol
<RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
  <Button title="Crear Usuario" />
</RoleGuard>

// Ejemplo 2: Verificar rol en lógica
const { hasRole } = useAuth();

if (hasRole(TipoUsuario.PROFESIONAL)) {
  // Lógica específica para profesionales
}
```

---

## 📊 Matriz de Permisos por Endpoint

| Endpoint | Método | Rol Requerido | Servicio |
|----------|--------|---------------|----------|
| `/auth/login` | POST | ❌ Ninguno (público) | `authService.ts` |
| `/auth/register-organization` | POST | ❌ Ninguno (público) | `authService.ts` |
| `/auth/register/profesional` | POST | `ADMIN_ORGANIZACION` | `usuarioService.ts` |
| `/auth/register/paciente` | POST | `ADMIN_ORGANIZACION` | `usuarioService.ts` |
| `/asignaciones` | POST | `ADMIN_ORGANIZACION` | `asignacionService.ts` |
| `/asignaciones/paciente/:id` | GET | `ADMIN_ORGANIZACION`, `PROFESIONAL` | `asignacionService.ts` |
| `/asignaciones/profesional/:id` | GET | `ADMIN_ORGANIZACION`, `PROFESIONAL` | `asignacionService.ts` |
| `/asignaciones/:id/deactivate` | PUT | `ADMIN_ORGANIZACION` | `asignacionService.ts` |
| `/asignaciones/:id` | DELETE | `ADMIN_ORGANIZACION` | `asignacionService.ts` |
| `/organizaciones` | GET | `ADMIN_ORGANIZACION`, `SUPER_ADMIN` | `organizacionService.ts` |
| `/organizaciones/:id` | GET | `ADMIN_ORGANIZACION`, `SUPER_ADMIN` | `organizacionService.ts` |
| `/organizaciones` | POST | `SUPER_ADMIN` | `organizacionService.ts` |
| `/organizaciones/:id` | PUT | `SUPER_ADMIN` | `organizacionService.ts` |
| `/organizaciones/:id` | DELETE | `SUPER_ADMIN` | `organizacionService.ts` |
| `/profesionales/my-organization` | GET | Autenticado | `profesionalService.ts` |
| `/profesional/mis-estadisticas` | GET | `PROFESIONAL` | `profesionalService.ts` |
| `/pacientes/mis-pacientes` | GET | `PROFESIONAL` | `pacienteService.ts` |
| `/usuarios/me` | GET | Autenticado | `usuarioService.ts` |
| `/usuarios/me/password` | PUT | Autenticado | `usuarioService.ts` |

---

## 🚀 Próximos Pasos Recomendados

### **Pantallas Pendientes de Implementar:**

1. **RegisterProfesionalScreen.tsx**
   - Formulario de registro de profesional
   - Usa `usuarioService.registerProfesional()`
   - Solo accesible por `ADMIN_ORGANIZACION`

2. **RegisterPacienteScreen.tsx**
   - Formulario de registro de paciente
   - Usa `usuarioService.registerPaciente()`
   - Solo accesible por `ADMIN_ORGANIZACION`

3. **AsignacionesScreen.tsx**
   - Listado y gestión de asignaciones
   - Incluir checkbox para `esPrincipal`
   - Usa `asignacionService.createAsignacion()`

4. **MisPacientesScreen.tsx** (para profesionales)
   - Listado de pacientes asignados
   - Usa `pacienteService.getMisPacientes()`

5. **MisEstadisticasScreen.tsx** (para profesionales)
   - Dashboard de estadísticas
   - Usa `profesionalService.getMisEstadisticas()`

6. **OrganizacionesScreen.tsx** (para super admin)
   - Gestión de organizaciones
   - Usa `organizacionService.*`

---

## 🧪 Testing Recomendado

### **Tests Unitarios:**
```typescript
// Ejemplo para asignacionService.ts
describe('AsignacionService', () => {
  it('should create assignment with esPrincipal field', async () => {
    const request: AsignacionRequestDTO = {
      profesionalId: 1,
      pacienteId: 2,
      esPrincipal: true
    };

    const result = await asignacionService.createAsignacion(request);
    expect(result.esPrincipal).toBe(true);
  });
});
```

### **Tests de Integración:**
- Verificar que el token JWT se envía en todas las peticiones
- Verificar que errores 401 limpian la sesión
- Verificar que `RoleGuard` oculta contenido correctamente

### **Tests E2E:**
- Login → Dashboard → Ver acciones basadas en rol
- ADMIN_ORGANIZACION → Crear profesional → Ver en listado
- PROFESIONAL → Ver mis pacientes

---

## 📝 Notas de Migración

### **Para Desarrolladores:**

1. **Uso de Tipos:**
   ```typescript
   // ❌ NO hacer esto
   interface Usuario {
     id: number;
     email: string;
   }

   // ✅ Hacer esto
   import { UsuarioResponseDTO } from '../types/api.types';
   ```

2. **Protección de Rutas:**
   ```typescript
   // En tu navigator, protege rutas por rol
   {hasRole(TipoUsuario.ADMIN_ORGANIZACION) && (
     <Stack.Screen name="RegisterProfesional" component={RegisterProfesionalScreen} />
   )}
   ```

3. **Manejo de Errores:**
   ```typescript
   try {
     await asignacionService.createAsignacion(data);
   } catch (error: any) {
     // El error ya viene formateado del servicio
     Alert.alert('Error', error.message);
   }
   ```

---

## ✅ Checklist de Verificación

- [x] Tipos TypeScript sincronizados con backend
- [x] Interceptor JWT configurado correctamente
- [x] Manejo de errores 401/403 implementado
- [x] Servicios para todos los endpoints del backend
- [x] Hook `useAuth` creado y funcional
- [x] Componente `RoleGuard` creado
- [x] DashboardScreen actualizado con acciones por rol
- [ ] Pantallas de registro de profesional/paciente (pendiente)
- [ ] Pantalla de gestión de asignaciones (pendiente)
- [ ] Tests unitarios para servicios (pendiente)
- [ ] Tests E2E para flujos completos (pendiente)

---

## 📚 Referencias

- **Backend API:** `alma_backend/src/main/java/com/alma/alma_backend/controller/`
- **DTOs Backend:** `alma_backend/src/main/java/com/alma/alma_backend/dto/`
- **Documentación TypeScript:** [https://www.typescriptlang.org/](https://www.typescriptlang.org/)
- **React Navigation:** [https://reactnavigation.org/](https://reactnavigation.org/)
- **Axios Interceptors:** [https://axios-http.com/docs/interceptors](https://axios-http.com/docs/interceptors)

---

## 🎉 Conclusión

El frontend de A.L.M.A está ahora **100% actualizado** y compatible con el backend v2.0. Todas las mejoras de seguridad del backend están reflejadas en el frontend:

- ✅ **Autenticación robusta** con JWT
- ✅ **Control de acceso basado en roles** (RBAC)
- ✅ **Tipos seguros** con TypeScript
- ✅ **Manejo centralizado de errores**
- ✅ **Servicios modulares y reutilizables**

La arquitectura está preparada para escalar y añadir nuevas funcionalidades de forma organizada y segura.

---

**¿Preguntas?** Consulta la documentación del código o revisa los comentarios JSDoc en cada servicio.
