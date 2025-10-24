# FASE 0: ARQUITECTURA Y AUTENTICACIÓN - COMPLETADA ✅

## 📋 RESUMEN

La Fase 0 establece la base completa del frontend de A.L.M.A. sobre la que se construirán todas las futuras funcionalidades. Incluye una arquitectura sólida, un sistema de navegación, servicios de comunicación con la API, y un flujo completo de autenticación y gestión de usuarios y roles.

### 🎯 OBJETIVOS CUMPLIDOS

- ✅ Arquitectura de proyecto escalable en React Native.
- ✅ Sistema de navegación con `React Navigation`.
- ✅ Conexión centralizada a la API backend con `axios` y manejo de errores.
- ✅ Almacenamiento seguro de tokens JWT en `AsyncStorage`.
- ✅ Flujo de autenticación completo (Login, Logout, Selección de Rol).
- ✅ Flujo de registro de nuevas organizaciones y su administrador.
- ✅ Esqueleto de Dashboards diferenciados por rol de usuario.
- ✅ Componentes base para la gestión de roles en la UI (`RoleGuard`).
- ✅ Funcionalidades iniciales para el `ADMIN_ORGANIZACION` (registrar profesionales/pacientes, gestionar asignaciones).

---

## 📁 ESTRUCTURA DE ARCHIVOS CLAVE

La arquitectura del frontend se organiza en los siguientes directorios principales dentro de `src/`:

-   **`assets/`**: Contiene recursos estáticos como imágenes, fuentes y videos.
-   **`components/`**: Almacena componentes reutilizables. El más importante es:
    -   `RoleGuard.tsx`: Protege la renderización de componentes según el rol del usuario.
-   **`hooks/`**: Hooks de React personalizados para encapsular lógica compleja.
    -   `useAuth.ts`: Hook central que gestiona el estado de autenticación (token, rol, email) y lo provee al resto de la aplicación.
-   **`navigation/`**: Define la lógica de navegación de la aplicación.
    -   `AppNavigator.tsx`: Contiene el `StackNavigator` principal con todas las pantallas de la aplicación.
-   **`screens/`**: Contiene las pantallas completas de la aplicación, organizadas por funcionalidad.
    -   `Auth/`: Pantallas relacionadas con la autenticación (Login, Registro, etc.).
    -   `Admin/`: Pantallas para el rol `ADMIN_ORGANIZACION`.
    -   `Profesional/`: Pantallas para el rol `PROFESIONAL`.
    -   `Dashboard/`: Dashboards específicos para cada rol.
-   **`services/`**: Clases o módulos que se comunican con la API del backend.
    -   `api.ts`: Configuración central de `axios`, incluyendo interceptores para inyectar el token JWT y manejar errores de API globalmente.
    -   `authService.ts`, `organizacionService.ts`, etc.: Servicios específicos para cada conjunto de endpoints.
-   **`theme/`**: Define la paleta de colores, espaciado y estilos globales de la aplicación.
-   **`types/`**: Contiene las definiciones de tipos de TypeScript.
    -   `api.types.ts`: Interfaces y enums que se corresponden con los DTOs del backend.

---

## ✨ FUNCIONALIDADES IMPLEMENTADAS

### 1. Flujo de Autenticación y Sesión
-   **Login:** La pantalla `LoginScreen` utiliza `authService.ts` para enviar las credenciales al backend.
-   **Almacenamiento de Token:** Si el login es exitoso, el token JWT y los datos del usuario se guardan de forma segura en `AsyncStorage`.
-   **Inyección Automática de Token:** El interceptor de `axios` en `api.ts` adjunta automáticamente el token `Bearer` a todas las peticiones a endpoints protegidos.
-   **Gestión de Estado Global:** El hook `useAuth.ts` lee los datos de `AsyncStorage` y mantiene el estado de la sesión (si el usuario está autenticado, su rol, etc.), haciéndolo disponible para cualquier componente.
-   **Cierre de Sesión (Logout):** La función `logout` en `useAuth.ts` limpia los datos de `AsyncStorage` y reinicia el estado de autenticación.
-   **Manejo de Token Expirado:** El interceptor de `axios` detecta respuestas `401 Unauthorized`, ejecuta el `logout` automáticamente y podría redirigir al usuario a la pantalla de Login.

### 2. Registro de Nuevas Organizaciones
-   El flujo comienza en `RegisterOrganizationFormScreen` donde se recogen los datos de la organización.
-   Posteriormente, en `RegisterAdminOrgScreen`, se recogen los datos del primer administrador de esa organización.
-   El `authService.registerOrganization` envía la petición completa al backend.

### 3. Dashboards por Rol
-   La `DashboardScreen` actúa como un enrutador principal después del login.
-   Utilizando el `useAuth` hook, esta pantalla renderiza un dashboard diferente (`AdminOrgDashboard`, `ProfesionalDashboard`, `PacienteDashboard`, `SuperAdminDashboard`) según el `userRole`.

### 4. Gestión de Roles en la UI
-   **`RoleGuard.tsx`**: Permite ocultar o mostrar bloques de UI de forma declarativa. Por ejemplo, un botón solo visible para administradores se envuelve en `<RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>`.
-   **Funciones de `useAuth`**: Para lógica condicional más simple, se pueden usar funciones como `isSuperAdmin()` o `hasAnyRole([...])` directamente en los componentes.

---

## 🚀 PRÓXIMOS PASOS

Con esta sólida base, el proyecto está listo para la implementación de las fases funcionales:

-   **FASE 1:** Implementación de la gestión de organizaciones para el `SUPER_ADMIN`.
-   **FASE 2:** Desarrollo del seguimiento de progreso emocional y la gestión de citas.
-   **FASE 3:** Creación del chat entre profesional y paciente.
-   **Y así sucesivamente...**

---
