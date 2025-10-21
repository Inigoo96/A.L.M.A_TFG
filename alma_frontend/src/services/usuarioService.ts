import api from './api';
import {
  UsuarioResponseDTO,
  ProfesionalRegistroDTO,
  PacienteRegistroDTO,
  ResetPasswordRequestDTO,
} from '../types/api.types';

/**
 * Servicio para gestionar usuarios del sistema
 * Requiere autenticación JWT para todos los endpoints
 */
class UsuarioService {
  /**
   * Registrar un nuevo profesional
   * Requiere rol: ADMIN_ORGANIZACION
   * @param data Datos del profesional a registrar
   * @returns Usuario profesional creado
   */
  async registerProfesional(data: ProfesionalRegistroDTO): Promise<UsuarioResponseDTO> {
    try {
      console.log('Registrando profesional:', data.email);
      const response = await api.post<UsuarioResponseDTO>('/auth/register/profesional', data);
      console.log('✅ Profesional registrado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al registrar profesional:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para registrar profesionales. Requiere rol ADMIN_ORGANIZACION.');
      }

      // Priorizar el mensaje del backend
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      if (error.response?.status === 409) {
        throw new Error('El email o DNI ya está en uso.');
      }

      throw new Error('Error al registrar el profesional. Por favor, intenta de nuevo.');
    }
  }

  /**
   * Registrar un nuevo paciente
   * Requiere rol: ADMIN_ORGANIZACION
   * @param data Datos del paciente a registrar
   * @returns Usuario paciente creado
   */
  async registerPaciente(data: PacienteRegistroDTO): Promise<UsuarioResponseDTO> {
    try {
      console.log('Registrando paciente:', data.email);
      const response = await api.post<UsuarioResponseDTO>('/auth/register/paciente', data);
      console.log('✅ Paciente registrado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al registrar paciente:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para registrar pacientes. Requiere rol ADMIN_ORGANIZACION.');
      }

      // Priorizar el mensaje del backend
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      if (error.response?.status === 409) {
        throw new Error('El email o DNI ya está en uso.');
      }

      throw new Error('Error al registrar el paciente. Por favor, intenta de nuevo.');
    }
  }

  /**
   * Actualizar la contraseña del usuario actual
   * Requiere autenticación
   * @param oldPassword Contraseña actual
   * @param newPassword Nueva contraseña
   */
  async updatePassword(oldPassword: string, newPassword: string): Promise<void> {
    try {
      console.log('Actualizando contraseña del usuario actual');
      await api.put('/usuarios/me/password', {
        oldPassword,
        newPassword,
      });
      console.log('Contraseña actualizada exitosamente');
    } catch (error: any) {
      console.error('Error al actualizar contraseña:', error);

      if (error.response?.status === 400) {
        throw new Error('La contraseña actual es incorrecta.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al actualizar la contraseña. Por favor, intenta de nuevo.');
    }
  }

  /**
   * Obtener información del usuario actual
   * Requiere autenticación
   * @returns Datos del usuario autenticado
   */
  async getCurrentUser(): Promise<UsuarioResponseDTO> {
    try {
      console.log('Obteniendo información del usuario actual');
      const response = await api.get<UsuarioResponseDTO>('/usuarios/me');
      console.log('Usuario actual:', response.data.email);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener usuario actual:', error);

      if (error.response?.status === 401) {
        throw new Error('No estás autenticado. Por favor, inicia sesión.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener información del usuario.');
    }
  }

  /**
   * Obtener un usuario por su ID
   * Requiere autenticación
   * @param id ID del usuario
   * @returns Datos del usuario
   */
  async getUserById(id: number): Promise<UsuarioResponseDTO> {
    try {
      console.log(`Obteniendo usuario ID: ${id}`);
      const response = await api.get<UsuarioResponseDTO>(`/usuarios/${id}`);
      console.log('Usuario encontrado:', response.data.email);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener usuario:', error);

      if (error.response?.status === 404) {
        throw new Error('Usuario no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener el usuario.');
    }
  }

  /**
   * Obtener todos los usuarios de la organización del usuario actual
   * Requiere rol: ADMIN_ORGANIZACION
   * @returns Lista de usuarios de la organización
   */
  async getUsersFromMyOrganization(): Promise<UsuarioResponseDTO[]> {
    try {
      console.log('Obteniendo usuarios de mi organización...');
      const response = await api.get<UsuarioResponseDTO[]>('/usuarios/my-organization');
      console.log(`Se encontraron ${response.data.length} usuarios`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener usuarios:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los usuarios. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los usuarios de la organización.');
    }
  }

  /**
   * Activar o desactivar un usuario
   * Requiere rol: ADMIN_ORGANIZACION
   * @param id ID del usuario
   * @param activo Estado activo/inactivo
   * @returns Usuario actualizado
   */
  async toggleUserStatus(id: number, activo: boolean): Promise<UsuarioResponseDTO> {
    try {
      console.log(`${activo ? 'Activando' : 'Desactivando'} usuario ID: ${id}`);
      const response = await api.put<UsuarioResponseDTO>(`/usuarios/${id}/status`, { activo });
      console.log('Estado del usuario actualizado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al cambiar estado del usuario:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para cambiar el estado de usuarios.');
      }

      if (error.response?.status === 404) {
        throw new Error('Usuario no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al cambiar el estado del usuario.');
    }
  }

  /**
   * Obtener todos los usuarios de mi organización
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @returns Lista de usuarios de la organización
   */
  async getAllUsuarios(): Promise<UsuarioResponseDTO[]> {
    try {
      console.log('Obteniendo todos los usuarios de la organización...');
      const response = await api.get<UsuarioResponseDTO[]>('/usuarios');
      console.log(`Se encontraron ${response.data.length} usuarios`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener usuarios:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los usuarios. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los usuarios de la organización.');
    }
  }

  /**
   * Actualizar un usuario
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @param id ID del usuario
   * @param data Datos a actualizar (parcial)
   * @returns Usuario actualizado
   */
  async updateUsuario(id: number, data: Partial<UsuarioResponseDTO>): Promise<UsuarioResponseDTO> {
    try {
      console.log(`Actualizando usuario ID: ${id}`);
      const response = await api.put<UsuarioResponseDTO>(`/usuarios/${id}`, data);
      console.log('Usuario actualizado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al actualizar usuario:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para actualizar usuarios.');
      }

      if (error.response?.status === 404) {
        throw new Error('Usuario no encontrado.');
      }

      if (error.response?.status === 409) {
        throw new Error('El email ya está en uso por otro usuario.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al actualizar el usuario.');
    }
  }

  /**
   * Eliminar un usuario
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @param id ID del usuario
   */
  async deleteUsuario(id: number): Promise<void> {
    try {
      console.log(`Eliminando usuario ID: ${id}`);
      await api.delete(`/usuarios/${id}`);
      console.log('Usuario eliminado exitosamente');
    } catch (error: any) {
      console.error('Error al eliminar usuario:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para eliminar usuarios.');
      }

      if (error.response?.status === 404) {
        throw new Error('Usuario no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al eliminar el usuario.');
    }
  }

  /**
   * Resetear contraseña de un usuario (solo admin)
   * Requiere rol: ADMIN_ORGANIZACION
   * @param id ID del usuario
   * @param newPassword Nueva contraseña
   */
  async resetUserPassword(id: number, newPassword: string): Promise<void> {
    try {
      console.log(`Reseteando contraseña del usuario ID: ${id}`);
      const data: ResetPasswordRequestDTO = { newPassword };
      await api.post(`/usuarios/${id}/reset-password`, data);
      console.log('Contraseña reseteada exitosamente');
    } catch (error: any) {
      console.error('Error al resetear contraseña:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para resetear contraseñas. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.status === 404) {
        throw new Error('Usuario no encontrado.');
      }

      if (error.response?.status === 400) {
        throw new Error('La contraseña no cumple con los requisitos mínimos (mínimo 8 caracteres).');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al resetear la contraseña del usuario.');
    }
  }
}

export default new UsuarioService();