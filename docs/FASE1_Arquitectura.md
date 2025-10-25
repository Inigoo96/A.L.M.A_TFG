# Fase 1 · Informe de arquitectura

## 1. Visión general del repositorio
- **alma_backend/**: API REST en Spring Boot 3.5 con autenticación JWT, estructura clásica controller → service → repository, DTOs y configuración de seguridad/Jackson.
- **alma_frontend/**: Aplicación móvil React Native (Expo) con estructura modular (navigation, screens, services, hooks, theme).
- **bd/**: Scripts SQL versionados (estilo Flyway) que reflejan la evolución del modelo de datos PostgreSQL.
- **README.md / LICENSE**: documentación base del proyecto.

## 2. Diagrama lógico por capas
```mermaid
graph TD
    subgraph Cliente
        RN[React Native · alma_frontend]
    end
    subgraph Backend
        Controller[Controllers REST]
        Service[Services]
        Repo[Repositories JPA]
        Security[Security & JWT]
        DTO[DTOs]
        Entity[Entities JPA]
    end
    DB[(PostgreSQL · scripts en /bd)]

    RN -->|HTTP/JSON| Controller
    Controller --> Service
    Service --> Repo
    Repo --> Entity
    Controller --> DTO
    Security -.-> Controller
    Security -.-> Service
    Repo -->|Hibernate| DB
```

## 3. Backend · descripción de paquetes principales (`com.alma.alma_backend`)
| Paquete | Responsabilidad | Clases destacadas |
|---------|-----------------|-------------------|
| `controller` | Endpoints REST organizados por dominio (autenticación, pacientes, citas, recursos, foros, IA, etc.). | `AuthenticationController`, `PacienteController`, `ChatPacientesController`, `RecursoController`, `MetaDiariaController`, `IAController`, `PruebaController` (debug/test).
| `service` | Interfaces + implementaciones que concentran la lógica de negocio (gestión de usuarios, citas, foros, progreso del duelo, informes emocionales, IA, recursos multimedia, auditoría). | `AuthServiceImpl`, `UsuarioServiceImpl`, `ProgresoDueloServiceImpl`, `IAServiceImpl`, `RecursoServiceImpl`, `AsignacionProfesionalPacienteServiceImpl`.
| `repository` | Repositorios Spring Data JPA para todas las entidades persistentes. | `UsuarioRepository`, `ProfesionalRepository`, `ProgresoDueloRepository`, `MensajeChatRepository`, `InformeEmocionalRepository`, etc.
| `entity` | Modelo de dominio con anotaciones JPA, enums para catálogos y estados. | `Usuario`, `Paciente`, `Profesional`, `Cita`, `MetaDiaria`, `ProgresoDuelo`, `MensajeChat`, `Organizacion`, `AuditoriaAdmin`.
| `dto` | Objetos de transferencia usados por los controladores para requests/responses. Incluye DTOs específicos para registros, autenticación, reporting y métricas. |
| `config` | Configuración de Spring Security y personalización de Jackson. | `SecurityConfig`, `JacksonConfig`.
| `security` | Infraestructura JWT: filtro, entry point, utilidades y `UserDetailsService` personalizado. | `JwtRequestFilter`, `JwtAuthenticationEntryPoint`, `JwtUtil`, `UserDetailsServiceImpl`.
| `exceptions` | Manejo global de errores y excepciones personalizadas. | `GlobalExceptionHandler`, `ResourceNotFoundException`.
| `util` | Utilidades compartidas (por ejemplo validaciones). | `ValidationUtils`.

## 4. Dependencias clave (`alma_backend/pom.xml`)
- **Spring Boot Starters**: `data-jpa`, `security`, `validation`, `web` para la base del backend REST.
- **JWT**: `io.jsonwebtoken` (`jjwt-api`, `jjwt-impl`, `jjwt-jackson`) para generación y validación de tokens.
- **Jackson**: módulo `jackson-datatype-hibernate6` para serialización segura de entidades lazy.
- **PostgreSQL driver** y `spring-boot-devtools` (runtime, desarrollo).
- **Lombok** para reducir boilerplate (opcional).
- **OpenAPI**: `springdoc-openapi-starter-webmvc-ui` para documentación Swagger.
- **Testing**: `spring-boot-starter-test`, `spring-security-test`.
- **Plugin Maven**: `maven-compiler-plugin` fijado a Java 17, con `annotationProcessorPaths` para Lombok y `spring-boot-maven-plugin`.

## 5. Configuración de seguridad
- **`SecurityConfig`** habilita seguridad web y métodos, desactiva CSRF para APIs y registra CORS manual con orígenes permitidos `http://localhost:3000` / `4200` (React / Angular). Añade protección XSS y CSP básica.
- **Autenticación JWT**: filtro `JwtRequestFilter` intercepta solicitudes, valida tokens con `JwtUtil` y delega en `UserDetailsServiceImpl`. `JwtAuthenticationEntryPoint` gestiona errores 401.
- **Endpoints públicos**: `/api/auth/**`, `/api/authentication/**`, `/api/public/**`, `/swagger-ui/**`, `/v3/api-docs/**`, `/api/progreso-duelo/fases` (GET), `/actuator/health`.
- **PasswordEncoder**: `BCryptPasswordEncoder` con fuerza 12.

## 6. Configuración de base de datos y entorno
- **`application.properties`**: configuración común (nombre app, puerto 8080, dialecto PostgreSQL, `ddl-auto=validate`, logging DEBUG para `com.alma`).
- **Perfiles locales**: `application-inigoDev.properties` y `application-lauraDev.properties` con credenciales de PostgreSQL local (usuario `postgres`, contraseñas distintas).
- **Producción**: `application-prod.properties` externaliza URL y credenciales via variables (`DB_URL`, `DB_USERNAME`, `DB_PASSWORD`), reduce logs y mantiene `ddl-auto=validate`.
- **Seguridad**: `application-security.properties` contiene clave JWT y expiración (también existe versión `.example`).
- **Migraciones**: recursos en `src/main/resources/db` y scripts adicionales en `/bd` (nombrados `V#__...sql`, compatibles con Flyway).

## 7. Puntos de mejora y riesgos técnicos
1. **Clave JWT y credenciales comprometidas**: `application-security.properties` y perfiles locales contienen secretos reales versionados. Deben moverse a variables de entorno y excluirse del repositorio.
2. **Configuración Maven no portátil**: `maven-compiler-plugin` referencia una ruta absoluta de Windows para `javac`, lo que rompe builds en Linux/Mac y entornos CI. Reemplazar por configuración estándar.
3. **CORS rígido**: orígenes fijos limitan despliegue móvil/web en producción. Conviene parametrizar según entorno.
4. **Logging sensible**: `logging.level.org.springframework.security=DEBUG` puede exponer detalles de seguridad en producción si no se controla por perfil.
5. **Multiplicidad de scripts SQL**: coexistencia de migraciones en `src/main/resources/db` y `/bd` requiere alineación (definir fuente única o pipeline Flyway).
6. **Ausencia de infraestructura centralizada para variables**: falta `.env` o guía de variables para backend y frontend; recomendable documentar y usar `application-*.yml` con `@ConfigurationProperties`.
7. **Controladores muy numerosos**: se intuye un dominio amplio; será clave validar en Fase 2 que la lógica compleja está delegada a servicios y que existen pruebas que protejan la arquitectura.

---
**Estado:** Fase 1 completada. Listo para avanzar a la Fase 2 (calidad y refactorización).
