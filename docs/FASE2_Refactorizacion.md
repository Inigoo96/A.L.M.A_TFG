# Fase 2 · Calidad y refactorización (backend)

## 1. Resumen ejecutivo
- La arquitectura mantiene la separación controller → service → repository; los controladores ahora operan con DTOs unificados + mappers dedicados y delegan en servicios, pero conservan bloques `try/catch` y construcción manual de respuestas que deberían centralizarse en el handler global o en `ApiResponseAdvice`.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/AuthController.java†L27-L82】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L31-L143】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/advice/ApiResponseAdvice.java†L1-L74】
- Los servicios cubren la mayoría de las reglas y comparten DTOs/mappers, aunque persisten validaciones duplicadas y ausencia de transacciones semánticas claras en operaciones críticas (por ejemplo, chat y citas).【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L31-L174】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L37-L214】
- La configuración de seguridad mantiene buenas prácticas básicas; los secretos se cargan desde variables de entorno y Swagger UI quedó protegido tras la incorporación de `ApiResponseAdvice`, aunque las reglas CORS siguen siendo rígidas y manuales para orígenes locales.【F:alma_backend/alma_backend/src/main/resources/application-security.properties†L5-L21】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/config/SecurityConfig.java†L43-L87】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/advice/ApiResponseAdvice.java†L1-L74】
- El proyecto carece de suites automatizadas; no existen pruebas unitarias ni de integración que cubran los servicios o la configuración de seguridad.【997cf3†L1-L2】

## 2. Hallazgos por capa

| Capa | Observaciones | Impacto | Recomendaciones |
|------|---------------|---------|-----------------|
| Controllers | `AuthController` y `UsuarioController` atrapan excepciones genéricas y construyen respuestas a mano (Map/ResponseEntity) pese a delegar en servicios y usar DTOs/mappers comunes y `ApiResponseAdvice`.| Media-Alta: dificulta testeo y reutilización; riesgo de respuestas inconsistentes.| Trasladar la orquestación a facades/servicios, aprovechar DTOs con `@Valid` y centralizar errores en `GlobalExceptionHandler`/`ApiResponseAdvice`. |
| Services | `AuthServiceImpl`, `ChatServiceImpl`, `CitaServiceImpl` y `IAServiceImpl` comparten DTOs/mappers pero aún duplican validaciones y realizan múltiples escrituras JPA dentro de bucles (ej. marcar leídos). Aunque usan `Logger`, faltan mappers compartidos y control de trazas sensibles.| Alta: Riesgo de inconsistencias y trazas verbosas; coste en mantenimiento.| Consolidar reglas en servicios especializados, reforzar validaciones con excepciones de dominio y usar operaciones batch/repository personalizados. |
| Repositories | Métodos personalizados correctos, pero falta paginación en listados grandes (citas, mensajes, sesiones).| Media: respuestas potencialmente pesadas y sin límites.| Añadir variantes `Pageable` y exponer parámetros de paginación desde los controladores. |
| DTO/Validaciones | Algunos endpoints aceptan `Map<String,String>` o entidades completas, perdiendo validaciones declarativas.| Alta: entrada no validada que puede romper invariantes.| Definir DTOs específicos para cada acción (ej. notas de IA) y anotar campos con `jakarta.validation`. |
| Seguridad | Clave JWT y configuración se cargan mediante variables de entorno, pero CORS sigue fijo y la parametrización depende de propiedades locales.| Media-Alta: despliegue no portable para dominios reales.| Parametrizar CORS por perfil (`CorsConfigurationProperties`) y documentar variables requeridas. |

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
log.info("Enviando credenciales temporales para el usuario {}", usuarioGuardado.getId());
notificationService.enviarCredencialesTemporales(usuarioGuardado.getEmail(), tempPassword);
```
- Mantener el uso de `Logger` evitando imprimir contraseñas y delegar el envío en servicios dedicados de notificación/auditoría.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/AuthServiceImpl.java†L138-L206】

### 4.4 Optimizar operaciones masivas
- Implementar un método en `MensajeChatRepository` para marcar todos los mensajes como leídos via `@Modifying` query en lugar de iterar guardando individualmente.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L168-L175】
- Añadir métodos `Page<MensajeChat>` y `Page<Cita>` para listas extensas; exponer parámetros `page`, `size`, `sort` en controladores.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L65-L93】

### 4.5 DTOs dedicados y validaciones
- Crear `NotasProfesionalDTO` con `@NotBlank` para evitar `Map<String,String>` en `IAController` y asegurar validación automática.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/IAController.java†L60-L74】
- Reemplazar uso de `Usuario` en `UsuarioController.updateUsuario` por `UsuarioUpdateDTO` con reglas explícitas.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L72-L143】

## 5. Seguridad y configuración
- **Secretos**: `application-*.properties` consumen `jwt.secret.key` y credenciales mediante variables de entorno (`spring.config.import=optional:file:.env[.properties]`). Mantener los `.example` actualizados con los nombres requeridos.【F:alma_backend/alma_backend/src/main/resources/application-security.properties†L5-L21】
- **CORS**: Exponer dominios mediante `@Value` o `CorsConfigurationProperties`, permitiendo configurar orígenes por perfil (`dev`, `prod`).【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/config/SecurityConfig.java†L43-L87】
- **Build portable**: Eliminar la ruta absoluta de `javac` en `maven-compiler-plugin` para permitir compilación en CI/CD Linux/Mac.【F:alma_backend/alma_backend/pom.xml†L112-L138】

## 6. Gestión de excepciones y consistencia
- `GlobalExceptionHandler` ya cubre `ResourceNotFound`, `MethodArgumentNotValid` y `IllegalState`, por lo que los controladores deberían delegar errores sin `try/catch`; aprovechar mensajes internacionales si se añade `MessageSource`.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/exceptions/GlobalExceptionHandler.java†L20-L59】
- Añadir excepciones específicas (p. ej. `DomainRuleViolationException`) para reglas de negocio como límite de mensajes IA y solapamiento de citas.

## 7. Testing y cobertura recomendada
- **Estado actual**: no existe `src/test/java`, por lo que no hay pruebas automatizadas ni configuración de `@SpringBootTest`, aunque las validaciones declarativas están completadas y sincronizadas con la base de datos mediante migraciones Flyway.【997cf3†L1-L2】【F:bd/V9__Fase6_Recursos_Multimedia_Informes.sql†L11-L179】
- **Plan mínimo**:
  - Tests unitarios para `AuthServiceImpl`, `ChatServiceImpl`, `CitaServiceImpl` usando mocks de repositorios (Mockito).
  - Tests de integración slice (`@WebMvcTest`) para verificar seguridad de endpoints clave (`/api/auth/**`, `/api/usuarios/**`).
  - Tests de configuración para asegurar lectura de variables (`SecurityConfig`).

## 8. Priorización de mejoras
1. **Consolidar manejo de excepciones y respuestas en controladores** para delegar completamente en servicios y el handler global/`ApiResponseAdvice`.【F:alma_backend/alma_backend/controller/UsuarioController.java†L72-L143】【F:alma_backend/alma_backend/controller/AuthController.java†L27-L82】【F:alma_backend/alma_backend/advice/ApiResponseAdvice.java†L1-L74】
2. **Normalizar controladores y DTOs** para recuperar consistencia por capas, validaciones automáticas y trazabilidad con `LoggingConfig`.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/controller/UsuarioController.java†L72-L143】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/logging/LoggingConfig.java†L1-L78】
3. **Agregar paginación y operaciones batch** en chat/citas para prevenir cargas pesadas en producción.【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/ChatServiceImpl.java†L147-L181】【F:alma_backend/alma_backend/src/main/java/com/alma/alma_backend/service/CitaServiceImpl.java†L65-L109】
4. **Cubrir con pruebas unitarias** los servicios críticos antes de refactorizar para asegurar regresión controlada.

---
**Estado:** Fase completada.
