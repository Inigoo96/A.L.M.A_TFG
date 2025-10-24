# PLAN DE IMPLEMENTACIÓN FRONTEND - A.L.M.A.

Este documento detalla la hoja de ruta para el desarrollo de todas las funcionalidades del frontend de A.L.M.A., desde la Fase 1 hasta la Fase 6. Sirve como guía técnica y funcional para la implementación.

---

## ✅ FASE 0: ARQUITECTURA Y AUTENTICACIÓN - COMPLETADA

-   **Estado:** Implementado.
-   **Descripción:** Base del proyecto, navegación, servicios de API, autenticación, dashboards por rol y gestión de roles en la UI. Ver `FASE0_ARQUITECTURA_Y_AUTENTICACION.md` para más detalles.

---

## 🎯 FASE 1: GESTIÓN DE ORGANIZACIONES (SUPER_ADMIN)

-   **Estado:** Pendiente.

*   **Objetivo:** Implementar las interfaces para que el `SUPER_ADMIN` pueda ver y gestionar el estado de las organizaciones (activar, suspender, dar de baja) y consultar la auditoría del sistema.
*   **Servicios a Crear/Modificar:**
    *   **`organizacionService.ts` (Modificar):**
        *   `getOrganizacionesPorEstado(estado: string): Promise<OrganizacionDTO[]>` -> Llama a `GET /api/organizaciones/estado/{estado}`.
        *   `cambiarEstadoOrganizacion(id: number, data: CambioEstadoOrganizacionDTO): Promise<OrganizacionDTO>` -> Llama a `PUT /api/organizaciones/{id}/cambiar-estado`.
        *   `getAuditoriaOrganizacion(id: number): Promise<AuditoriaDTO[]>` -> Llama a `GET /api/organizaciones/{id}/auditoria`.
    *   **`auditoriaService.ts` (Nuevo):**
        *   `getAuditoriaReciente(limit: number): Promise<AuditoriaDTO[]>` -> Llama a `GET /api/organizaciones/auditoria/recientes?limit={limit}`.
*   **Componentes a Crear/Modificar:**
    *   `SuperAdminDashboard.tsx` (Modificar): Añadir botones para "Gestionar Organizaciones" y "Ver Auditoría".
    *   `GestionOrganizacionesScreen.tsx` (Nuevo): Pantalla principal que mostrará listas de organizaciones por pestañas (Pendientes, Activas, Suspendidas).
    *   `OrganizacionListItem.tsx` (Nuevo): Componente para mostrar una organización en la lista, con botones de acción.
    *   `CambiarEstadoModal.tsx` (Nuevo): Modal para que el `SUPER_ADMIN` introduzca el motivo del cambio de estado.
    *   `AuditoriaScreen.tsx` (Nuevo): Pantalla para mostrar el log de auditoría reciente.
*   **Navegación a Añadir (Rutas):**
    *   Añadir `GestionOrganizacionesScreen` y `AuditoriaScreen` al `AppNavigator.tsx`.
    *   La navegación a estas pantallas se originará desde los nuevos botones en el `SuperAdminDashboard.tsx`.
*   **Lógica de UI/UX:**
    *   En `GestionOrganizacionesScreen`, el `SUPER_ADMIN` podrá filtrar organizaciones por estado.
    *   Al pulsar en "Suspender" o "Dar de Baja" en un `OrganizacionListItem`, se abrirá el `CambiarEstadoModal` para confirmar la acción y escribir un motivo.
*   **Gestión de Roles:**
    *   El acceso a `GestionOrganizacionesScreen` y `AuditoriaScreen` debe estar protegido. Se puede envolver la navegación en el `DashboardScreen` con: `<RoleGuard allowedRoles={[TipoUsuario.SUPER_ADMIN]}>`.

---

## 🎯 FASE 2: PROGRESO EMOCIONAL Y GESTIÓN DE CITAS

-   **Estado:** Pendiente.

*   **Objetivo:** Permitir a pacientes y profesionales registrar y ver el progreso emocional. Permitir a profesionales y administradores crear y gestionar citas, y a los pacientes verlas.
*   **Servicios a Crear/Modificar:**
    *   **`progresoDueloService.ts` (Nuevo):**
        *   `registrarProgreso(data: ProgresoDueloRequestDTO): Promise<ProgresoDueloResponseDTO>` -> `POST /api/progreso-duelo/`.
        *   `getHistorialPaciente(idPaciente: number): Promise<ProgresoDueloResponseDTO[]>` -> `GET /api/progreso-duelo/paciente/{idPaciente}`.
    *   **`citaService.ts` (Nuevo):**
        *   `crearCita(data: CitaRequestDTO): Promise<CitaResponseDTO>` -> `POST /api/citas`.
        *   `getCitasPaciente(id: number): Promise<CitaResponseDTO[]>` -> `GET /api/citas/paciente/{id}`.
        *   `getCitasProfesional(id: number): Promise<CitaResponseDTO[]>` -> `GET /api/citas/profesional/{id}`.
        *   `cancelarCita(id: number): Promise<void>` -> `PUT /api/citas/{id}/cancelar`.
        *   `actualizarEstadoCita(id: number, data: ActualizarEstadoCitaDTO): Promise<CitaResponseDTO>` -> `PUT /api/citas/{id}/estado`.
*   **Componentes a Crear/Modificar:**
    *   `PacienteDashboard.tsx` (Modificar): Añadir botones para "Mi Progreso" y "Mis Citas".
    *   `ProfesionalDashboard.tsx` (Modificar): Añadir botones para "Agenda de Citas".
    *   `MiProgresoScreen.tsx` (Nuevo): Pantalla para que el `PACIENTE` vea su historial de progreso (gráficos, etc.) y registre un nuevo estado.
    *   `RegistroProgresoForm.tsx` (Nuevo): Formulario para registrar el estado emocional.
    *   `MisCitasScreen.tsx` (Nuevo): Pantalla para que `PACIENTE` y `PROFESIONAL` vean su lista de citas (próximas y pasadas).
    *   `CitaListItem.tsx` (Nuevo): Componente para mostrar una cita.
    *   `CrearCitaScreen.tsx` (Nuevo): Formulario para que `PROFESIONAL` o `ADMIN_ORGANIZACION` creen una nueva cita para un paciente.
*   **Navegación a Añadir (Rutas):**
    *   Añadir `MiProgresoScreen`, `MisCitasScreen` y `CrearCitaScreen` al `AppNavigator.tsx`.
    *   Navegar a ellas desde los dashboards correspondientes.
*   **Gestión de Roles:**
    *   Usar `RoleGuard` o `isPaciente()`/`isProfesional()` para mostrar los botones correctos en los dashboards.
    *   El botón "Crear Cita" solo será visible para `PROFESIONAL` y `ADMIN_ORGANIZACION`.

---

## 🎯 FASE 3: CHAT PROFESIONAL-PACIENTE

-   **Estado:** Pendiente.

*   **Objetivo:** Implementar una interfaz de chat en tiempo real (o por sondeo) entre un profesional y un paciente asignado.
*   **Servicios a Crear/Modificar:**
    *   **`chatService.ts` (Nuevo):**
        *   `getSesionActiva(idPaciente: number, idProfesional: number): Promise<SesionChatResponseDTO>` -> `GET /api/chat/sesion/activa`.
        *   `getMensajes(idSesion: number): Promise<MensajeChatResponseDTO[]>` -> `GET /api/chat/sesion/{idSesion}/mensajes`.
        *   `enviarMensaje(data: MensajeChatRequestDTO): Promise<MensajeChatResponseDTO>` -> `POST /api/chat/mensaje`.
        *   `marcarLeidos(idSesion: number): Promise<void>` -> `PUT /api/chat/sesion/{idSesion}/marcar-leidos`.
*   **Componentes a Crear/Modificar:**
    *   `MisPacientesScreen.tsx` (Modificar): Añadir un botón de "Chat" en cada paciente de la lista.
    *   `ChatScreen.tsx` (Nuevo): Pantalla principal del chat, que muestra los mensajes y un campo de texto para enviar.
    *   `MensajeBubble.tsx` (Nuevo): Componente para renderizar una burbuja de mensaje (diferenciando entre emisor y receptor).
    *   `ChatInput.tsx` (Nuevo): Componente con el campo de texto y el botón de enviar.
*   **Navegación a Añadir (Rutas):**
    *   Añadir `ChatScreen` al `AppNavigator.tsx`.
    *   La navegación se iniciará desde `MisPacientesScreen` (para el `PROFESIONAL`) o desde un nuevo apartado "Mi Profesional" en el `PacienteDashboard.tsx`.

---

## 🎯 FASE 4: CHATBOT IA Y METAS DIARIAS

-   **Estado:** Pendiente.

*   **Objetivo:** Crear una interfaz de chat para que el paciente interactúe con la IA. Implementar un sistema para que el paciente gestione sus metas diarias.
*   **Servicios a Crear/Modificar:**
    *   **`iaService.ts` (Nuevo):**
        *   `iniciarSesionIA(): Promise<SesionInteraccionResponseDTO>` -> `POST /api/ia/sesion`.
        *   `enviarMensajeIA(data: EnviarMensajeIARequestDTO): Promise<MensajeIAResponseDTO>` -> `POST /api/ia/mensaje`.
    *   **`metaDiariaService.ts` (Nuevo):**
        *   `crearMeta(data: MetaDiariaRequestDTO): Promise<MetaDiariaResponseDTO>` -> `POST /api/metas`.
        *   `getMetasHoy(idPaciente: number): Promise<MetaDiariaResponseDTO[]>` -> `GET /api/metas/paciente/{id}/hoy`.
        *   `actualizarEstadoMeta(data: ActualizarMetaRequestDTO): Promise<MetaDiariaResponseDTO>` -> `PUT /api/metas/actualizar-estado`.
*   **Componentes a Crear/Modificar:**
    *   `PacienteDashboard.tsx` (Modificar): Añadir botones para "Chat con A.L.M.A." y "Mis Metas de Hoy".
    *   `ChatIaScreen.tsx` (Nuevo): Similar a `ChatScreen`, pero para la interacción con la IA.
    *   `MetasScreen.tsx` (Nuevo): Pantalla para ver, crear y marcar como completadas las metas del día.
    *   `MetaListItem.tsx` (Nuevo): Componente para una meta, con un checkbox para marcarla.
    *   `CrearMetaInput.tsx` (Nuevo): Campo de texto para añadir una nueva meta.
*   **Navegación a Añadir (Rutas):**
    *   Añadir `ChatIaScreen` y `MetasScreen` al `AppNavigator.tsx`.
    *   Navegar desde los nuevos botones en `PacienteDashboard.tsx`.
*   **Gestión de Roles:**
    *   Funcionalidades principalmente para el `PACIENTE`. Usar `RoleGuard` o `isPaciente()` para proteger el acceso.

---

## 🎯 FASE 5: FOROS Y CHAT ENTRE PACIENTES

-   **Estado:** Pendiente.

*   **Objetivo:** Crear las interfaces para que los pacientes puedan participar en foros de discusión y chatear entre ellos.
*   **Servicios a Crear/Modificar:**
    *   **`foroService.ts` (Nuevo):**
        *   `getForos(): Promise<ForoDTO[]>` -> `GET /api/foros`.
        *   `getMensajesForo(foroId: number): Promise<MensajeForoResponseDTO[]>` -> `GET /api/foros/{foroId}/mensajes`.
        *   `publicarMensajeForo(data: MensajeForoRequestDTO): Promise<MensajeForoResponseDTO>` -> `POST /api/foros/mensajes`.
    *   **`chatPacientesService.ts` (Nuevo):**
        *   `getSesionChat(idReceptor: number): Promise<ChatPacientesResponseDTO>` -> `POST /api/chat-pacientes/sesion`.
*   **Componentes a Crear/Modificar:**
    *   `PacienteDashboard.tsx` (Modificar): Añadir botón "Comunidad" o "Foros".
    *   `ForosListScreen.tsx` (Nuevo): Muestra la lista de foros disponibles.
    *   `ForoDetalleScreen.tsx` (Nuevo): Muestra los mensajes de un foro y permite publicar uno nuevo.
    *   `ContactosPacientesScreen.tsx` (Nuevo): Pantalla donde un paciente puede ver a otros para iniciar un chat.
    *   `ChatPacienteScreen.tsx` (Nuevo): Interfaz de chat privado entre dos pacientes.
*   **Navegación a Añadir (Rutas):**
    *   Añadir `ForosListScreen`, `ForoDetalleScreen`, `ContactosPacientesScreen` y `ChatPacienteScreen` al `AppNavigator.tsx`.
*   **Gestión de Roles:**
    *   Toda la sección está protegida por el rol `PACIENTE`. Usar `RoleGuard` en el punto de entrada.

---

## 🎯 FASE 6: RECURSOS MULTIMEDIA E INFORMES

-   **Estado:** Pendiente.

*   **Objetivo:** Permitir a los pacientes consumir recursos multimedia y ver sus informes. Permitir a los profesionales generar dichos informes.
*   **Servicios a Crear/Modificar:**
    *   **`recursoService.ts` (Nuevo):**
        *   `getRecursos(): Promise<RecursoDTO[]>` -> `GET /api/recursos`.
        *   `registrarUso(data: UsoRecursoRequestDTO): Promise<void>` -> `POST /api/recursos/uso`.
    *   **`informeService.ts` (Nuevo):**
        *   `generarInforme(data: InformeEmocionalRequestDTO): Promise<InformeEmocionalResponseDTO>` -> `POST /api/informes/generar-manual`.
        *   `getInformesPaciente(id: number): Promise<InformeEmocionalResponseDTO[]>` -> `GET /api/informes/paciente/{id}`.
*   **Componentes a Crear/Modificar:**
    *   `PacienteDashboard.tsx` (Modificar): Añadir "Recursos" y "Mis Informes".
    *   `ProfesionalDashboard.tsx` (Modificar): Añadir acceso a "Generar Informe" desde la ficha de un paciente.
    *   `RecursosScreen.tsx` (Nuevo): Muestra los recursos (videos, podcasts) en categorías.
    *   `RecursoPlayerModal.tsx` (Nuevo): Un modal para reproducir el recurso seleccionado.
    *   `InformesScreen.tsx` (Nuevo): Lista los informes generados para el paciente.
    *   `InformeDetalleScreen.tsx` (Nuevo): Muestra el contenido de un informe.
    *   `GenerarInformeScreen.tsx` (Nuevo): Formulario para que el `PROFESIONAL` redacte y genere un informe.
*   **Navegación a Añadir (Rutas):**
    *   Añadir las nuevas pantallas al `AppNavigator.tsx`.
*   **Gestión de Roles:**
    *   `PACIENTE` puede ver recursos e informes.
    *   `PROFESIONAL` puede generar informes. El botón "Generar Informe" debe estar protegido con `<RoleGuard allowedRoles={[TipoUsuario.PROFESIONAL]}>`.
