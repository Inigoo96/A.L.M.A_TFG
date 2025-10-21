import api from './api';
import {
  AsignacionRequestDTO,
  AsignacionResponseDTO,
} from '../types/api.types';

/**
 * Servicio para gestionar las asignaciones entre profesionales y pacientes
 * Requiere autenticación JWT para todos los endpoints
 */
class AsignacionService {
  /**
   * Crear una nueva asignación profesional-paciente
   * Requiere rol: ADMIN_ORGANIZACION
   * @param request Datos de la asignación (profesionalId, pacienteId, esPrincipal)
   * @returns Asignación creada con todos sus detalles
   */
  async createAsignacion(request: AsignacionRequestDTO): Promise<AsignacionResponseDTO> {
    try {
      console.log('Creando asignación:', request);
      const response = await api.post<AsignacionResponseDTO>('/asignaciones', request);
      console.log('Asignación creada exitosamente:', response.data);
      return response.data;
    } catch (error: any) {
      console.error('Error al crear asignación:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para crear asignaciones. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.status === 404) {
        throw new Error('Profesional o paciente no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al crear la asignación. Por favor, intenta de nuevo.');
    }
  }

  /**
   * Obtener todas las asignaciones de un paciente específico
   * Requiere rol: ADMIN_ORGANIZACION o PROFESIONAL
   * @param pacienteId ID del paciente
   * @returns Lista de asignaciones del paciente
   */
  async getAsignacionesByPaciente(pacienteId: number): Promise<AsignacionResponseDTO[]> {
    try {
      console.log(`Obteniendo asignaciones del paciente ID: ${pacienteId}`);
      const response = await api.get<AsignacionResponseDTO[]>(`/asignaciones/paciente/${pacienteId}`);
      console.log(`Se encontraron ${response.data.length} asignaciones`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener asignaciones del paciente:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estas asignaciones.');
      }

      if (error.response?.status === 404) {
        throw new Error('Paciente no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener las asignaciones del paciente.');
    }
  }

  /**
   * Obtener todas las asignaciones de un profesional específico
   * Requiere rol: ADMIN_ORGANIZACION o PROFESIONAL
   * @param profesionalId ID del profesional
   * @returns Lista de asignaciones del profesional
   */
  async getAsignacionesByProfesional(profesionalId: number): Promise<AsignacionResponseDTO[]> {
    try {
      console.log(`Obteniendo asignaciones del profesional ID: ${profesionalId}`);
      const response = await api.get<AsignacionResponseDTO[]>(`/asignaciones/profesional/${profesionalId}`);
      console.log(`Se encontraron ${response.data.length} asignaciones`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener asignaciones del profesional:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estas asignaciones.');
      }

      if (error.response?.status === 404) {
        throw new Error('Profesional no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener las asignaciones del profesional.');
    }
  }

  /**
   * Desactivar una asignación específica
   * Requiere rol: ADMIN_ORGANIZACION
   * @param asignacionId ID de la asignación
   * @returns Asignación desactivada
   */
  async deactivateAsignacion(asignacionId: number): Promise<AsignacionResponseDTO> {
    try {
      console.log(`Desactivando asignación ID: ${asignacionId}`);
      const response = await api.put<AsignacionResponseDTO>(`/asignaciones/${asignacionId}/deactivate`);
      console.log('Asignación desactivada exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al desactivar asignación:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para desactivar esta asignación. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.status === 404) {
        throw new Error('Asignación no encontrada.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al desactivar la asignación.');
    }
  }

  /**
   * Eliminar permanentemente una asignación
   * Requiere rol: ADMIN_ORGANIZACION
   * @param asignacionId ID de la asignación
   */
  async deleteAsignacion(asignacionId: number): Promise<void> {
    try {
      console.log(`Eliminando asignación ID: ${asignacionId}`);
      await api.delete(`/asignaciones/${asignacionId}`);
      console.log('Asignación eliminada exitosamente');
    } catch (error: any) {
      console.error('Error al eliminar asignación:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para eliminar esta asignación. Requiere rol ADMIN_ORGANIZACION.');
      }

      if (error.response?.status === 404) {
        throw new Error('Asignación no encontrada.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al eliminar la asignación.');
    }
  }
}

export default new AsignacionService();