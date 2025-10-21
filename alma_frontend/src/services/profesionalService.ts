import api from './api';
import {
  ProfesionalResponseDTO,
  ProfesionalEstadisticasDTO,
  ProfesionalDetalleDTO,
  PacienteDetalleDTO,
} from '../types/api.types';

/**
 * Servicio para gestionar profesionales
 * Requiere autenticación JWT para todos los endpoints
 */
class ProfesionalService {
  /**
   * Obtener todos los profesionales
   * Requiere autenticación
   * @returns Lista de todos los profesionales
   */
  async getAllProfesionales(): Promise<ProfesionalResponseDTO[]> {
    try {
      console.log('Obteniendo todos los profesionales...');
      const response = await api.get<ProfesionalResponseDTO[]>('/profesionales');
      console.log(`Se encontraron ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener profesionales:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los profesionales.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los profesionales.');
    }
  }

  /**
   * Obtener un profesional por su ID
   * Requiere autenticación
   * @param id ID del profesional
   * @returns Datos del profesional
   */
  async getProfesionalById(id: number): Promise<ProfesionalResponseDTO> {
    try {
      console.log(`Obteniendo profesional ID: ${id}`);
      const response = await api.get<ProfesionalResponseDTO>(`/profesionales/${id}`);
      console.log('Profesional encontrado:', response.data.usuario.email);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener profesional:', error);

      if (error.response?.status === 404) {
        throw new Error('Profesional no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener el profesional.');
    }
  }

  /**
   * Obtener profesionales de una organización específica
   * Requiere autenticación
   * @param organizacionId ID de la organización
   * @returns Lista de profesionales de la organización
   */
  async getProfesionalesByOrganizacion(organizacionId: number): Promise<ProfesionalResponseDTO[]> {
    try {
      console.log(`Obteniendo profesionales de organización ID: ${organizacionId}`);
      const response = await api.get<ProfesionalResponseDTO[]>(`/profesionales/organizacion/${organizacionId}`);
      console.log(`Se encontraron ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener profesionales:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver profesionales de esta organización.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los profesionales de la organización.');
    }
  }

  /**
   * Obtener profesionales de la organización del usuario actual
   * Requiere autenticación
   * @returns Lista de profesionales de mi organización
   */
  async getProfesionalesFromMyOrganization(): Promise<ProfesionalResponseDTO[]> {
    try {
      console.log('Obteniendo profesionales de mi organización...');
      const response = await api.get<ProfesionalResponseDTO[]>('/profesionales/my-organization');
      console.log(`Se encontraron ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener profesionales:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los profesionales de tu organización.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los profesionales de tu organización.');
    }
  }

  /**
   * Actualizar datos de un profesional
   * Requiere rol: ADMIN_ORGANIZACION
   * @param id ID del profesional
   * @param data Datos a actualizar
   * @returns Profesional actualizado
   */
  async updateProfesional(id: number, data: Partial<ProfesionalResponseDTO>): Promise<ProfesionalResponseDTO> {
    try {
      console.log(`Actualizando profesional ID: ${id}`);
      const response = await api.put<ProfesionalResponseDTO>(`/profesionales/${id}`, data);
      console.log('Profesional actualizado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al actualizar profesional:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para actualizar profesionales.');
      }

      if (error.response?.status === 404) {
        throw new Error('Profesional no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al actualizar el profesional.');
    }
  }

  /**
   * Obtener estadísticas de un profesional
   * Requiere autenticación
   * @param id ID del profesional
   * @returns Estadísticas del profesional
   */
  async getEstadisticasProfesional(id: number): Promise<ProfesionalEstadisticasDTO> {
    try {
      console.log(`Obteniendo estadísticas del profesional ID: ${id}`);
      const response = await api.get<ProfesionalEstadisticasDTO>(`/profesional/${id}/estadisticas`);
      console.log('Estadísticas obtenidas exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);

      if (error.response?.status === 404) {
        throw new Error('Profesional no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener las estadísticas del profesional.');
    }
  }

  /**
   * Obtener estadísticas del profesional actual (usuario autenticado)
   * Requiere rol: PROFESIONAL
   * @returns Estadísticas del profesional autenticado
   */
  async getMisEstadisticas(): Promise<ProfesionalEstadisticasDTO> {
    try {
      console.log('Obteniendo mis estadísticas como profesional...');
      const response = await api.get<ProfesionalEstadisticasDTO>('/profesional/mis-estadisticas');
      console.log('Mis estadísticas obtenidas exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener mis estadísticas:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estas estadísticas. Requiere rol PROFESIONAL.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener tus estadísticas.');
    }
  }

  /**
   * Obtener mis pacientes (versión detallada optimizada)
   * Requiere rol: PROFESIONAL
   * @param soloActivos Filtrar solo pacientes activos (default: true)
   * @returns Lista de pacientes asignados al profesional autenticado
   */
  async getMisPacientesDetalle(soloActivos: boolean = true): Promise<PacienteDetalleDTO[]> {
    try {
      console.log('Obteniendo mis pacientes (detalle)...');
      const response = await api.get<PacienteDetalleDTO[]>(
        `/profesional/mis-pacientes-detalle?soloActivos=${soloActivos}`
      );
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener mis pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver pacientes. Requiere rol PROFESIONAL.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener tus pacientes.');
    }
  }

  /**
   * Obtener mi perfil profesional
   * Requiere rol: PROFESIONAL
   * @returns Datos del perfil profesional del usuario autenticado
   */
  async getMiPerfil(): Promise<ProfesionalDetalleDTO> {
    try {
      console.log('Obteniendo mi perfil profesional...');
      const response = await api.get<ProfesionalDetalleDTO>('/profesional/mi-perfil');
      console.log('Perfil obtenido exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener mi perfil:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver este perfil. Requiere rol PROFESIONAL.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener tu perfil profesional.');
    }
  }

  /**
   * Obtener todos los profesionales de mi organización (admin)
   * Requiere rol: ADMIN_ORGANIZACION
   * @returns Lista de profesionales con detalle
   */
  async getProfesionalesDeOrganizacion(): Promise<ProfesionalDetalleDTO[]> {
    try {
      console.log('Obteniendo todos los profesionales de la organización...');
      const response = await api.get<ProfesionalDetalleDTO[]>('/profesional/organizacion/todos');
      console.log(`Se encontraron ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener profesionales:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver profesionales. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los profesionales de la organización.');
    }
  }

  /**
   * Obtener estadísticas de todos los profesionales de la organización
   * Requiere rol: ADMIN_ORGANIZACION
   * @returns Lista de estadísticas de profesionales
   */
  async getEstadisticasOrganizacion(): Promise<ProfesionalEstadisticasDTO[]> {
    try {
      console.log('Obteniendo estadísticas de profesionales de la organización...');
      const response = await api.get<ProfesionalEstadisticasDTO[]>('/profesional/organizacion/estadisticas');
      console.log(`Se encontraron estadísticas de ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estas estadísticas. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener las estadísticas de la organización.');
    }
  }

  /**
   * Buscar profesionales por especialidad
   * Requiere rol: ADMIN_ORGANIZACION
   * @param especialidad Especialidad a buscar
   * @returns Lista de profesionales con esa especialidad
   */
  async buscarPorEspecialidad(especialidad: string): Promise<ProfesionalDetalleDTO[]> {
    try {
      console.log(`Buscando profesionales con especialidad: ${especialidad}`);
      const response = await api.get<ProfesionalDetalleDTO[]>(
        `/profesional/organizacion/buscar-especialidad?especialidad=${encodeURIComponent(especialidad)}`
      );
      console.log(`Se encontraron ${response.data.length} profesionales`);
      return response.data;
    } catch (error: any) {
      console.error('Error al buscar profesionales:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para buscar profesionales. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al buscar profesionales por especialidad.');
    }
  }

  /**
   * Obtener detalle de un profesional específico
   * Requiere rol: ADMIN_ORGANIZACION
   * @param id ID del profesional
   * @returns Detalle del profesional
   */
  async getDetalleProfesional(id: number): Promise<ProfesionalDetalleDTO> {
    try {
      console.log(`Obteniendo detalle del profesional ID: ${id}`);
      const response = await api.get<ProfesionalDetalleDTO>(`/profesional/${id}/detalle`);
      console.log('Detalle obtenido exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener detalle del profesional:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver este detalle. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.status === 404) {
        throw new Error('Profesional no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener el detalle del profesional.');
    }
  }
}

export default new ProfesionalService();