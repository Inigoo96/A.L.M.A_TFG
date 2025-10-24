/**
 * Tipos TypeScript para la API del backend A.L.M.A v2.0
 * Sincronizados con los DTOs de Spring Boot
 */

// ============================================
// ENUMS
// ============================================

export enum TipoUsuario {
  ADMIN_ORGANIZACION = 'ADMIN_ORGANIZACION',
  PROFESIONAL = 'PROFESIONAL',
  PACIENTE = 'PACIENTE',
  SUPER_ADMIN = 'SUPER_ADMIN',
}

export enum Genero {
  MASCULINO = 'MASCULINO',
  FEMENINO = 'FEMENINO',
  NO_BINARIO = 'NO_BINARIO',
  PREFIERO_NO_DECIR = 'PREFIERO_NO_DECIR',
}

export enum EstadoVerificacion {
  PENDIENTE_VERIFICACION = 'PENDIENTE_VERIFICACION',
  EN_REVISION = 'EN_REVISION',
  VERIFICADA = 'VERIFICADA',
  RECHAZADA = 'RECHAZADA',
}

export enum EstadoOrganizacion {
  ACTIVA = 'ACTIVA',
  SUSPENDIDA = 'SUSPENDIDA',
  BAJA = 'BAJA',
}

// ============================================
// ORGANIZACION DTOs
// ============================================

export interface OrganizacionDTO {
  id: number;
  cif: string;
  numeroSeguridadSocial?: string;
  nombreOficial: string;
  direccion?: string;
  codigoRegcess?: string;
  emailCorporativo: string;
  telefonoContacto?: string;
  documentoCifUrl?: string;
  documentoSeguridadSocialUrl?: string;
  estadoVerificacion: EstadoVerificacion;
  estado: EstadoOrganizacion;
  motivoRechazo?: string;
  fechaRegistro: string; // ISO 8601 date string
}

export interface CambioEstadoOrganizacionDTO {
  nuevoEstado: EstadoOrganizacion;
  motivo: string;
  observaciones?: string;
}

// ============================================
// AUDITORIA DTOs
// ============================================

export enum TipoAccionAuditoria {
  VERIFICAR_ORGANIZACION = 'VERIFICAR_ORGANIZACION',
  RECHAZAR_ORGANIZACION = 'RECHAZAR_ORGANIZACION',
  SUSPENDER_ORGANIZACION = 'SUSPENDER_ORGANIZACION',
  ACTIVAR_ORGANIZACION = 'ACTIVAR_ORGANIZACION',
  DAR_BAJA_ORGANIZACION = 'DAR_BAJA_ORGANIZACION',
  MODIFICAR_ORGANIZACION = 'MODIFICAR_ORGANIZACION',
  CREAR_SUPER_ADMIN = 'CREAR_SUPER_ADMIN',
  ELIMINAR_USUARIO = 'ELIMINAR_USUARIO',
  MODIFICAR_PERMISOS = 'MODIFICAR_PERMISOS',
}

export interface AuditoriaDTO {
  id: number;
  tipoAccion: TipoAccionAuditoria;
  tablaAfectada: string;
  idRegistroAfectado: number;
  datosAnteriores?: any;
  datosNuevos?: any;
  motivo: string;
  ipOrigen: string;
  fechaAccion: string; // ISO 8601 date string
  emailAdmin: string;
  nombreAdmin: string;
  apellidosAdmin: string;
}

export interface OrganizacionEstadisticasDTO {
  idOrganizacion: number;
  nombreOrganizacion: string;
  cif: string;
  estadoVerificacion: EstadoVerificacion;
  activa: boolean;
  totalUsuarios: number;
  admins: number;
  profesionales: number;
  pacientes: number;
  superAdmins: number;
}

// ============================================
// USUARIO DTOs
// ============================================

export interface UsuarioResponseDTO {
  id: number;
  dni?: string;
  email: string;
  nombre: string;
  apellidos: string;
  telefono?: string;
  tipoUsuario: TipoUsuario;
  organizacion: OrganizacionDTO;
  activo: boolean;
  fechaRegistro: string; // ISO 8601 date string
  ultimoAcceso?: string; // ISO 8601 date string
  passwordTemporal: boolean;
  cargo?: string;
  documentoCargoUrl?: string;
}

// ============================================
// PROFESIONAL DTOs
// ============================================

export interface ProfesionalResponseDTO {
  id: number;
  usuario: UsuarioResponseDTO;
  numeroColegiado?: string;
  especialidad?: string;
  centroSalud?: string;
}

export interface ProfesionalRegistroDTO {
  nombre: string;
  apellidos: string;
  email: string;
  dni: string; // REQUERIDO en backend
  telefono?: string;
  numeroColegiado?: string;
  especialidad?: string;
  centroSalud?: string;
}

export interface ProfesionalEstadisticasDTO {
  idProfesional: number;
  nombreCompleto: string;
  email: string;
  numeroColegiado?: string;
  especialidad?: string;
  totalPacientesAsignados: number;
  pacientesActivos: number;
  pacientesInactivos: number;
  asignacionesPrincipales: number;
  idOrganizacion: number;
  nombreOrganizacion: string;
}

export interface ProfesionalDetalleDTO {
  idProfesional: number;
  numeroColegiado?: string;
  especialidad?: string;
  centroSalud?: string;
  idUsuario: number;
  email: string;
  nombre: string;
  apellidos: string;
  tipoUsuario: TipoUsuario;
  activo: boolean;
  fechaRegistro: string;
  ultimoAcceso?: string;
  idOrganizacion: number;
  nombreOrganizacion: string;
  cifOrganizacion: string;
}

// ============================================
// PACIENTE DTOs
// ============================================

export interface PacienteResponseDTO {
  id: number;
  usuario: UsuarioResponseDTO;
  tarjetaSanitaria?: string;
  fechaNacimiento?: string; // ISO 8601 date string
  genero?: Genero;
}

export interface PacienteRegistroDTO {
  nombre: string;
  apellidos: string;
  email: string;
  dni: string; // REQUERIDO en backend
  telefono?: string;
  tarjetaSanitaria?: string;
  fechaNacimiento?: string; // ISO 8601 date string (YYYY-MM-DD)
  genero?: Genero;
}

export interface PacienteDetalleDTO {
  idPaciente: number;
  tarjetaSanitaria?: string;
  fechaNacimiento?: string;
  genero?: Genero;
  idUsuario: number;
  email: string;
  nombre: string;
  apellidos: string;
  tipoUsuario: TipoUsuario;
  activo: boolean;
  fechaRegistro: string;
  ultimoAcceso?: string;
  idOrganizacion: number;
  nombreOrganizacion: string;
  cifOrganizacion: string;
}

// ============================================
// ASIGNACION DTOs
// ============================================

export interface AsignacionRequestDTO {
  profesionalId: number;
  pacienteId: number;
  esPrincipal: boolean;
}

export interface AsignacionResponseDTO {
  id: number;
  profesional: ProfesionalResponseDTO;
  paciente: PacienteResponseDTO;
  fechaAsignacion: string; // ISO 8601 date string
  activa: boolean;
  esPrincipal: boolean;
}

export interface AsignacionDetalleDTO {
  idAsignacion: number;
  esPrincipal: boolean;
  fechaAsignacion: string;
  activo: boolean;
  idProfesional: number;
  nombreProfesional: string;
  apellidosProfesional: string;
  emailProfesional: string;
  numeroColegiado?: string;
  especialidad?: string;
  idPaciente: number;
  nombrePaciente: string;
  apellidosPaciente: string;
  emailPaciente: string;
  fechaNacimientoPaciente?: string;
  idOrganizacion: number;
  nombreOrganizacion: string;
}

// ============================================
// AUTENTICACION DTOs
// ============================================

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  access_token: string;
  email: string;
  role: string;
  token_type: string;
  password_temporal: boolean;
}

export interface RegisterOrganizationRequest {
  // Datos organización
  cif: string;
  numeroSeguridadSocial?: string;
  nombreOficial: string;
  direccion?: string;
  codigoRegcess?: string;
  emailCorporativo: string;
  telefonoContacto?: string;

  // Datos admin - IMPORTANTE: el backend espera "administrador", no "admin"
  administrador: {
    dni: string;
    nombre: string;
    apellidos: string;
    email: string;
    telefono?: string;
    cargo: string;
    password: string;
  };
}

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface ResetPasswordRequestDTO {
  newPassword: string;
}

// ============================================
// ERROR RESPONSE
// ============================================

export interface ErrorResponse {
  error?: string;
  message: string;
  timestamp?: string;
  status?: number;
}

// ============================================
// PAGINACION (para futuras implementaciones)
// ============================================

export interface PageRequest {
  page: number;
  size: number;
  sort?: string;
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}