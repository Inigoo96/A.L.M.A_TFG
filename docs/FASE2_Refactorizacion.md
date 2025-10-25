# Fase 2 · Calidad y refactorización (backend)

## 1. Resumen ejecutivo
- La arquitectura mantiene la separación controller → service → repository, pero varios controladores concentran lógica de negocio, manejo manual de excepciones y dependencias directas al repositorio, rompiendo la consistencia por capas.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/AuthController.java†L27-L82】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L31-L143】
- Los servicios cubren la mayoría de las reglas, aunque existen validaciones duplicadas, conversiones DTO repetidas y ausencia de transacciones semánticas claras en operaciones críticas (por ejemplo, chat y citas).【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L31-L174】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L37-L214】
- La configuración de seguridad mantiene buenas prácticas básicas, pero se versionan secretos en texto plano y hay reglas CORS rígidas que deberían externalizarse para despliegues reales.【F:alma_backend/alma_backend/src/main/resources/application-security.properties†L13-L21】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/config/SecurityConfig.java†L43-L87】
- El proyecto carece de suites automatizadas; no existen pruebas unitarias ni de integración que cubran los servicios o la configuración de seguridad.【997cf3†L1-L2】

## 2. Hallazgos por capa

| Capa | Observaciones | Impacto | Recomendaciones |
|------|---------------|---------|-----------------|
| Controllers | `AuthController` y `UsuarioController` atrapan excepciones genéricas y construyen respuestas a mano (Map/ResponseEntity). `UsuarioController` acepta `Usuario` como payload y usa `OrganizacionRepository` directamente.| Alta: Dificulta testeo y reutilización; riesgo de fugas de dominio y validaciones inconsistentes. | Trasladar la orquestación a servicios/facades, usar DTOs con `@Valid`, centralizar manejo de errores en `GlobalExceptionHandler` y aplicar inyección por constructor. |
| Services | `AuthServiceImpl`, `ChatServiceImpl`, `CitaServiceImpl` y `IAServiceImpl` duplican lógica de validación/mapeo y realizan múltiples escrituras JPA dentro de bucles (ej. marcar leídos). Hay `System.out.println` para contraseñas temporales.| Alta: Riesgo de inconsistencias y filtrado de credenciales; coste en mantenimiento.| Extraer mapeos a componentes (`Mapper`), encapsular validaciones en servicios especializados, reemplazar logs sensibles por eventos auditables y usar operaciones batch/repository personalizados. |
| Repositories | Métodos personalizados correctos, pero falta paginación en listados grandes (citas, mensajes, sesiones).| Media: respuestas potencialmente pesadas y sin límites.| Añadir variantes `Pageable` y exponer parámetros de paginación desde los controladores. |
| DTO/Validaciones | Algunos endpoints aceptan `Map<String,String>` o entidades completas, perdiendo validaciones declarativas.| Alta: entrada no validada que puede romper invariantes.| Definir DTOs específicos para cada acción (ej. notas de IA) y anotar campos con `jakarta.validation`. |
| Seguridad | Clave JWT y configuración en repositorio, CORS fijo y `PropertySource` apuntando a archivo sensible.| Alta: secretos expuestos, despliegue no portable.| Migrar a variables de entorno (Spring Cloud Config o AWS Secrets Manager) y parametrizar CORS por perfil. |

## 3. Análisis de principios SOLID y patrones

- **S (Single Responsibility)**: Controladores mezclan orquestación, validación y traducción de errores. Ejemplo: `AuthController` gestiona lógica de negocio y traducción de excepciones en lugar de delegar a `AuthService` y un handler común.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/AuthController.java†L27-L82】
- **O (Open/Closed)**: Servicios como `ChatServiceImpl` dependen directamente de repositorios concretos y no permiten extender validaciones (p. ej. políticas de sesión) sin modificar la clase.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L37-L214】 Se sugiere aplicar strategy para políticas de chat o interceptores de dominio.
- **L (Liskov)**: No se observan violaciones directas; sin embargo, el uso de entidades JPA en controladores puede romper contratos al exponer campos no preparados para serialización.
- **I (Interface Segregation)**: Las interfaces de servicio son coherentes; considerar dividir `AuthService` si aparecen más flujos (registro masivo, invitaciones) para mantenerlas específicas.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/AuthService.java†L1-L17】
- **D (Dependency Inversion)**: Hay mezcla de inyección por campo (`@Autowired`) y constructor. Adoptar constructor + Lombok `@RequiredArgsConstructor` en controladores/servicios para facilitar testeo y mocks.

## 4. Refactorizaciones propuestas (no destructivas)

### 4.1 Normalizar controladores
```java
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthFacade authFacade; // encapsula llamadas a servicios + validaciones contextuales

    @PostMapping("/register/profesional")
    @PreAuthorize("hasRole('ADMIN_ORGANIZACION')")
    public ResponseEntity<UsuarioResponseDTO> registrarProfesional(@Valid @RequestBody ProfesionalRegistroDTO dto,
                                                                   Authentication auth) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(authFacade.registrarProfesional(dto, auth));
    }
}
```
- Mover la obtención de organización y manejo de excepciones a `AuthFacade` o `AuthService`, aprovechando `GlobalExceptionHandler` para mapear `IllegalArgumentException`/`IllegalStateException`.

### 4.2 Centralizar mapeo y validaciones
```java
@Component
public class UsuarioMapper {
    public UsuarioResponseDTO toDto(Usuario entity) { ... }
    public void updateEntity(Usuario entity, UsuarioUpdateDTO dto) { ... }
}
```
- Evita duplicación de `mapToUsuarioResponseDTO` en `AuthServiceImpl` y `UsuarioController`.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/AuthServiceImpl.java†L208-L225】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L146-L167】

### 4.3 Sanitizar credenciales y eventos
```java
String tempPassword = passwordGenerator.generate();
auditService.emitirEventoResetPassword(usuarioGuardado.getId());
notificationService.enviarCredencialesTemporales(usuarioGuardado.getEmail(), tempPassword);
```
- Sustituir `System.out.println` de contraseñas por servicios de notificación/auditoría.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/AuthServiceImpl.java†L138-L194】

### 4.4 Optimizar operaciones masivas
- Implementar un método en `MensajeChatRepository` para marcar todos los mensajes como leídos via `@Modifying` query en lugar de iterar guardando individualmente.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L168-L175】
- Añadir métodos `Page<MensajeChat>` y `Page<Cita>` para listas extensas; exponer parámetros `page`, `size`, `sort` en controladores.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L65-L93】

### 4.5 DTOs dedicados y validaciones
- Crear `NotasProfesionalDTO` con `@NotBlank` para evitar `Map<String,String>` en `IAController` y asegurar validación automática.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/IAController.java†L60-L74】
- Reemplazar uso de `Usuario` en `UsuarioController.updateUsuario` por `UsuarioUpdateDTO` con reglas explícitas.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L72-L143】

## 5. Seguridad y configuración
- **Secretos**: Mover `jwt.secret.key` a `application-security.properties.example` y consumirlo mediante variables de entorno (`spring.config.import=optional:file:.env[.properties]`).【F:alma_backend/alma_backend/src/main/resources/application-security.properties†L13-L21】
- **CORS**: Exponer dominios mediante `@Value` o `CorsConfigurationProperties`, permitiendo configurar orígenes por perfil (`dev`, `prod`).【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/config/SecurityConfig.java†L43-L87】
- **Build portable**: Eliminar la ruta absoluta de `javac` en `maven-compiler-plugin` para permitir compilación en CI/CD Linux/Mac.【F:alma_backend/alma_backend/pom.xml†L112-L138】

## 6. Gestión de excepciones y consistencia
- `GlobalExceptionHandler` ya cubre `ResourceNotFound`, `MethodArgumentNotValid` y `IllegalState`, por lo que los controladores deberían delegar errores sin `try/catch`; aprovechar mensajes internacionales si se añade `MessageSource`.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/exceptions/GlobalExceptionHandler.java†L20-L59】
- Añadir excepciones específicas (p. ej. `DomainRuleViolationException`) para reglas de negocio como límite de mensajes IA y solapamiento de citas.

## 7. Testing y cobertura recomendada
- **Estado actual**: no existe `src/test/java`, por lo que no hay pruebas automatizadas ni configuración de `@SpringBootTest`.【997cf3†L1-L2】
- **Plan mínimo**:
  - Tests unitarios para `AuthServiceImpl`, `ChatServiceImpl`, `CitaServiceImpl` usando mocks de repositorios (Mockito).
  - Tests de integración slice (`@WebMvcTest`) para verificar seguridad de endpoints clave (`/api/auth/**`, `/api/usuarios/**`).
  - Tests de configuración para asegurar lectura de variables (`SecurityConfig`).

## 8. Priorización de mejoras
1. **Remover filtrado de contraseñas y secretos expuestos** (riesgo alto, cumplimiento).【F:alma_backend/alma_backend/service/AuthServiceImpl.java†L138-L194】【F:alma_backend/alma_backend/src/main/resources/application-security.properties†L13-L21】
2. **Normalizar controladores y DTOs** para recuperar consistencia por capas y validaciones automáticas.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L72-L143】
3. **Agregar paginación y operaciones batch** en chat/citas para prevenir cargas pesadas en producción.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L147-L181】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L65-L109】
4. **Cubrir con pruebas unitarias** los servicios críticos antes de refactorizar para asegurar regresión controlada.

---
**Estado:** Fase 2 completada. Listo para iniciar la Fase 3 (Frontend y conexión API).
