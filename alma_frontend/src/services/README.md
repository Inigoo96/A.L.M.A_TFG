# 📚 Servicios API de A.L.M.A Frontend

Guía rápida de uso de los servicios del frontend.

---

## 🎯 Servicios Disponibles

### 1. **authService.ts**
Autenticación y gestión de sesión.

```typescript
import authService from './services/authService';

// Login
const response = await authService.login(email, password);
// response: { access_token, email, role, token_type, password_temporal }

// Registro de organización
await authService.registerOrganization({
  nombreOrganizacion: 'Hospital Central',
  cif: 'A12345678',
  nombre: 'Juan',
  apellidos: 'Pérez',
  email: 'admin@hospital.com',
  password: 'SecurePass123!'
});

// Cambiar contraseña
await authService.updatePassword(oldPassword, newPassword);

// Logout
await authService.logout();

// Verificar autenticación
const isAuth = await authService.isAuthenticated();
```

---

### 2. **usuarioService.ts**
Registro y gestión de usuarios.

```typescript
import usuarioService from './services/usuarioService';

// Registrar profesional (requiere ADMIN_ORGANIZACION)
const profesional = await usuarioService.registerProfesional({
  nombre: 'María',
  apellidos: 'García López',
  email: 'maria.garcia@hospital.com',
  dni: '12345678A',
  telefono: '+34 600 123 456',
  numeroColegiado: 'COL12345',
  especialidad: 'Psicología Clínica',
  centroSalud: 'Centro Norte'
});

// Registrar paciente (requiere ADMIN_ORGANIZACION)
const paciente = await usuarioService.registerPaciente({
  nombre: 'Pedro',
  apellidos: 'Martínez',
  email: 'pedro@email.com',
  dni: '87654321B',
  tarjetaSanitaria: 'TSI123456789',
  fechaNacimiento: '1990-05-15',
  genero: Genero.MASCULINO
});

// Cambiar contraseña
await usuarioService.updatePassword('oldPass', 'newPass123!');

// Obtener usuario actual
const currentUser = await usuarioService.getCurrentUser();

// Obtener usuarios de mi organización
const users = await usuarioService.getUsersFromMyOrganization();
```

---

### 3. **asignacionService.ts**
Gestión de asignaciones profesional-paciente.

```typescript
import asignacionService from './services/asignacionService';

// Crear asignación (requiere ADMIN_ORGANIZACION)
const asignacion = await asignacionService.createAsignacion({
  profesionalId: 5,
  pacienteId: 12,
  esPrincipal: true // ⭐ NUEVO CAMPO
});

// Obtener asignaciones de un paciente
const asignaciones = await asignacionService.getAsignacionesByPaciente(12);

// Obtener asignaciones de un profesional
const asignaciones = await asignacionService.getAsignacionesByProfesional(5);

// Desactivar asignación (requiere ADMIN_ORGANIZACION)
await asignacionService.deactivateAsignacion(1);

// Eliminar asignación (requiere ADMIN_ORGANIZACION)
await asignacionService.deleteAsignacion(1);
```

---

### 4. **profesionalService.ts**
Gestión de profesionales.

```typescript
import profesionalService from './services/profesionalService';

// Obtener todos los profesionales
const profesionales = await profesionalService.getAllProfesionales();

// Obtener profesional por ID
const profesional = await profesionalService.getProfesionalById(5);

// Obtener profesionales de mi organización
const misProfesionales = await profesionalService.getProfesionalesFromMyOrganization();

// Obtener mis estadísticas (requiere PROFESIONAL)
const estadisticas = await profesionalService.getMisEstadisticas();
// estadisticas: { totalPacientesAsignados, asignacionesPrincipales, asignacionesSecundarias }

// Actualizar profesional
await profesionalService.updateProfesional(5, {
  especialidad: 'Psicología Infantil',
  centroSalud: 'Centro Sur'
});
```

---

### 5. **pacienteService.ts**
Gestión de pacientes.

```typescript
import pacienteService from './services/pacienteService';

// Obtener todos los pacientes
const pacientes = await pacienteService.getAllPacientes();

// Obtener paciente por ID
const paciente = await pacienteService.getPacienteById(12);

// Obtener pacientes de mi organización
const misPacientes = await pacienteService.getPacientesFromMyOrganization();

// Obtener MIS pacientes asignados (requiere PROFESIONAL)
const pacientesAsignados = await pacienteService.getMisPacientes();

// Obtener pacientes de un profesional específico
const pacientes = await pacienteService.getPacientesByProfesional(5);

// Buscar pacientes
const resultados = await pacienteService.searchPacientes('Juan');

// Actualizar paciente
await pacienteService.updatePaciente(12, {
  telefono: '+34 600 999 888'
});
```

---

### 6. **organizacionService.ts**
Gestión de organizaciones.

```typescript
import organizacionService from './services/organizacionService';

// Obtener todas las organizaciones (requiere ADMIN_ORGANIZACION o SUPER_ADMIN)
const organizaciones = await organizacionService.getAllOrganizaciones();

// Obtener organización por ID
const org = await organizacionService.getOrganizacionById(1);

// Buscar por CIF
const org = await organizacionService.getOrganizacionByCif('A12345678');

// Crear organización (requiere SUPER_ADMIN)
const nuevaOrg = await organizacionService.createOrganizacion({
  nombreOficial: 'Hospital General',
  cif: 'B87654321',
  direccion: 'Calle Principal 123',
  telefono: '+34 900 123 456',
  email: 'info@hospital.com',
  activo: true
});

// Obtener estadísticas (requiere SUPER_ADMIN)
const stats = await organizacionService.getEstadisticasOrganizaciones();
```

---

## 🎣 Hooks Disponibles

### **useAuth()**
Hook para gestión de autenticación y roles.

```typescript
import { useAuth } from '../hooks/useAuth';
import { TipoUsuario } from '../types/api.types';

function MyComponent() {
  const {
    isAuthenticated,
    isLoading,
    userEmail,
    userRole,
    passwordTemporal,
    logout,
    hasRole,
    hasAnyRole,
    isAdminOrganizacion,
    isProfesional,
    isPaciente,
    isSuperAdmin,
    getRoleName
  } = useAuth();

  // Verificar un rol específico
  if (hasRole(TipoUsuario.ADMIN_ORGANIZACION)) {
    return <AdminPanel />;
  }

  // Verificar múltiples roles
  if (hasAnyRole([TipoUsuario.PROFESIONAL, TipoUsuario.ADMIN_ORGANIZACION])) {
    return <ProfesionalPanel />;
  }

  // Verificar rol con métodos helper
  if (isProfesional()) {
    return <MisPacientes />;
  }

  return <Text>No tienes permisos</Text>;
}
```

---

## 🧩 Componentes Disponibles

### **RoleGuard**
Componente para proteger contenido basado en roles.

```typescript
import { RoleGuard } from '../components/RoleGuard';
import { TipoUsuario } from '../types/api.types';

function Dashboard() {
  return (
    <View>
      {/* Solo ADMIN_ORGANIZACION verá este botón */}
      <RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
        <Button title="Crear Usuario" onPress={handleCreate} />
      </RoleGuard>

      {/* ADMIN o PROFESIONAL verán esto */}
      <RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION, TipoUsuario.PROFESIONAL]}>
        <Button title="Ver Pacientes" onPress={handleView} />
      </RoleGuard>

      {/* Con mensaje de acceso denegado */}
      <RoleGuard
        allowedRoles={[TipoUsuario.SUPER_ADMIN]}
        showDeniedMessage={true}
      >
        <AdminPanel />
      </RoleGuard>

      {/* Con fallback personalizado */}
      <RoleGuard
        allowedRoles={[TipoUsuario.PROFESIONAL]}
        fallback={<Text>Solo profesionales</Text>}
      >
        <ProfesionalPanel />
      </RoleGuard>
    </View>
  );
}
```

---

## 📋 Tipos TypeScript

Todos los tipos están en `src/types/api.types.ts`.

```typescript
import {
  TipoUsuario,
  Genero,
  AsignacionRequestDTO,
  AsignacionResponseDTO,
  ProfesionalResponseDTO,
  PacienteResponseDTO,
  OrganizacionDTO,
  // ... más tipos
} from '../types/api.types';
```

---

## ⚠️ Manejo de Errores

Todos los servicios lanzan errores con mensajes descriptivos:

```typescript
try {
  await asignacionService.createAsignacion(data);
  Alert.alert('Éxito', 'Asignación creada correctamente');
} catch (error: any) {
  // El error ya viene formateado desde el servicio
  Alert.alert('Error', error.message);
}
```

**Tipos de errores comunes:**
- **401 Unauthorized:** Token inválido o expirado (se limpia sesión automáticamente)
- **403 Forbidden:** Sin permisos suficientes
- **404 Not Found:** Recurso no encontrado
- **409 Conflict:** Datos duplicados (ej: email ya existe)

---

## 🔒 Permisos por Rol

| Rol | Permisos |
|-----|----------|
| **PACIENTE** | - Ver sus profesionales<br>- Acceder a recursos de apoyo |
| **PROFESIONAL** | - Ver sus pacientes asignados<br>- Ver asignaciones<br>- Ver estadísticas propias |
| **ADMIN_ORGANIZACION** | - Registrar profesionales y pacientes<br>- Crear/gestionar asignaciones<br>- Ver usuarios de su organización<br>- Ver organizaciones |
| **SUPER_ADMIN** | - Gestionar organizaciones<br>- Ver estadísticas globales<br>- Acceso total |

---

## 🚀 Ejemplo Completo

```typescript
import React, { useState } from 'react';
import { View, Button, Alert } from 'react-native';
import { useAuth } from '../hooks/useAuth';
import { RoleGuard } from '../components/RoleGuard';
import { TipoUsuario } from '../types/api.types';
import asignacionService from '../services/asignacionService';

function AsignacionScreen() {
  const { hasRole } = useAuth();
  const [loading, setLoading] = useState(false);

  const handleCreateAsignacion = async () => {
    setLoading(true);
    try {
      await asignacionService.createAsignacion({
        profesionalId: 5,
        pacienteId: 12,
        esPrincipal: true
      });
      Alert.alert('Éxito', 'Asignación creada correctamente');
    } catch (error: any) {
      Alert.alert('Error', error.message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <View>
      <RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
        <Button
          title="Crear Asignación"
          onPress={handleCreateAsignacion}
          disabled={loading}
        />
      </RoleGuard>
    </View>
  );
}
```

---

## 📚 Más Información

Consulta [FRONTEND_UPDATE_V2.md](../FRONTEND_UPDATE_V2.md) para la documentación completa.