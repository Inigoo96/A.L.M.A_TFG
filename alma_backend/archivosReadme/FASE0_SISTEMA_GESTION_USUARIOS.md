# ✅ FASE 0: SISTEMA DE GESTIÓN DE USUARIOS - BASE FUNDAMENTAL

## 📊 **RESUMEN DE IMPLEMENTACIÓN**

La Fase 0 representa la base sobre la que se construye toda la aplicación A.L.M.A. Establece un sistema robusto y seguro para la gestión de organizaciones, usuarios y los diferentes roles dentro de la plataforma (Super Admin, Admin de Organización, Profesional y Paciente). Esta fase está contenida en los scripts de migración `V1` y `V2`.

---

## 🗄️ **BASE DE DATOS**

### **Archivos SQL**
- `bd/V1__Sistema_Gestion_Usuarios_Inicial.sql`
- `bd/V2__Sistema_Gestion_Usuarios_Roles.sql`

### **Estructura Implementada**

#### **1. Gestión de Organizaciones (`ORGANIZACION`)**
- Almacena la información de las entidades sanitarias (hospitales, clínicas, etc.).
- Incluye un sistema de **verificación anti-suplantación** con estados (`PENDIENTE_VERIFICACION`, `EN_REVISION`, `VERIFICADA`, `RECHAZADA`).
- Campos clave: `CIF`, `NUMERO_SEGURIDAD_SOCIAL`, `CODIGO_REGCESS` para una validación rigurosa.

#### **2. Gestión de Usuarios (`USUARIO`)**
- Tabla central que sirve como base para todos los tipos de usuarios.
- Define los roles principales a través del campo `TIPO_USUARIO` (`SUPER_ADMIN`, `ADMIN_ORGANIZACION`, `PROFESIONAL`, `PACIENTE`).
- Implementa un sistema de **contraseña temporal** (`PASSWORD_TEMPORAL`) que fuerza al usuario a cambiar su clave en el primer inicio de sesión.

#### **3. Roles Específicos (Tablas de Extensión)**
- `PROFESIONAL`: Extiende `USUARIO` con datos específicos como `NUMERO_COLEGIADO` y `ESPECIALIDAD`.
- `PACIENTE`: Extiende `USUARIO` con datos como `TARJETA_SANITARIA` y `FECHA_NACIMIENTO`.
- `ADMIN_ORGANIZACION`: Tabla de relación que vincula un `USUARIO` a una `ORGANIZACION` con rol de administrador.

#### **4. Relaciones y Asignaciones**
- `ASIGNACION_PROFESIONAL_PACIENTE`: Tabla clave que define la relación terapéutica entre un profesional y un paciente. Incluye un estado `activo` y la posibilidad de marcar una asignación como `es_principal`.

#### **5. Vistas y Auditoría**
- **Función de Auditoría (`actualizar_fecha_modificacion`)**: Un trigger automático que actualiza el campo `FECHA_ULTIMA_MODIFICACION` en todas las tablas principales, garantizando la trazabilidad de los cambios.
- **Vistas (`v_usuarios_completo`, `v_profesionales_completo`, `v_pacientes_completo`)**: Vistas SQL predefinidas que simplifican las consultas comunes y unen la información de varias tablas para presentar datos completos y coherentes.

---

## 🏗️ **BACKEND (Java Spring Boot)**

La implementación de esta fase en el backend se centra en:

- **Entidades JPA:** Mapeo de todas las tablas de la base de datos a clases Java (`Usuario.java`, `Organizacion.java`, `Paciente.java`, etc.).
- **Repositorios Spring Data JPA:** Interfaces para el acceso a datos (`UsuarioRepository`, `OrganizacionRepository`, etc.).
- **Servicios:** Lógica de negocio para la gestión de usuarios y roles (`UsuarioService`, `AuthService`).
- **Controladores REST:** Endpoints para la autenticación, registro y gestión de usuarios (`AuthController`, `UsuarioController`).
- **Seguridad con Spring Security:** Configuración inicial de la seguridad basada en JWT y roles.

---

## 📚 **FUNCIONALIDADES CLAVE ESTABLECIDAS**

1.  **Registro Seguro de Organizaciones:** Un `SUPER_ADMIN` debe verificar manualmente cada nueva organización antes de que pueda operar, previniendo el registro de entidades falsas.
2.  **Creación de Usuarios por Roles:**
    - Los `ADMIN_ORGANIZACION` son los únicos que pueden registrar nuevos `PROFESIONALES` y `PACIENTES` dentro de su propia organización.
    - El `SUPER_ADMIN` tiene una visión global y puede gestionar administradores y organizaciones.
3.  **Asignación Profesional-Paciente:** Los `ADMIN_ORGANIZACION` pueden crear y gestionar los vínculos terapéuticos, que son la base para futuras interacciones como el chat o las citas.
4.  **Autenticación y Autorización:** Sistema de login robusto que genera un token JWT con el rol del usuario, utilizado para proteger todos los endpoints de la aplicación.

---

## ✨ **Conclusión**

La Fase 0 es el cimiento de A.L.M.A. Establece un modelo de datos normalizado, seguro y escalable, y una lógica de negocio que respeta una jerarquía de roles clara. Toda funcionalidad futura se construirá sobre esta sólida base.
