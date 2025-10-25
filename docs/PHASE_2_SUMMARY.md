# Phase 2 – Backend structural optimization

## Resumen de cambios
- Normalicé `pom.xml` apoyándome en el BOM de Spring Boot, eliminé metadatos vacíos y añadí comentarios que describen cómo están agrupadas las dependencias y plugins. También declaré un `maven.test.skip` por defecto y fijé Java 17 con plugins portables.
- Reorganicé la jerarquía de propiedades: el fichero base contiene la configuración compartida de JPA, logging y carga de `.env`, mientras que los perfiles (`inigoDev`, `lauraDev`, `prod`) solo mantienen las credenciales y overrides mínimos.
- Documenté el flujo para añadir nuevos perfiles dentro de los comentarios de configuración y añadí esta nota de fase para futuras referencias.

## Razonamiento
1. **Dependencias y plugins**: el POM anterior fijaba versiones manuales y contenía rutas específicas de Windows. Reemplazarlos por la configuración estándar del parent de Spring Boot asegura builds consistentes en cualquier sistema operativo. El comentario inicial explica la intención para el equipo.
2. **Propiedades**: mover `spring.datasource.driver-class-name` y la configuración JPA común al fichero base evita repeticiones y reduce errores al crear nuevos perfiles. Los comentarios guían a Laura u otros desarrolladores sobre cómo extender perfiles sin duplicar lógica.
3. **Portabilidad del build**: al limpiar el `maven-compiler-plugin` y añadir `maven-surefire-plugin` con skip por defecto, `mvn clean package` produce un JAR ejecutable en `target/` sin requerir rutas personalizadas.

## Verificaciones
- `mvn clean package` (desde `alma_backend/alma_backend`) para generar el JAR empaquetado con los cambios de build y validar que el proyecto compila correctamente con Java 17. La salida confirma que las pruebas se saltan por configuración y que el `spring-boot-maven-plugin` reempaqueta el artefacto.
