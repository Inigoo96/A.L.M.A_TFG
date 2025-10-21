import api from './api';
import {
  PacienteResponseDTO,
  PacienteDetalleDTO,
} from '../types/api.types';

/**
 * Servicio para gestionar pacientes
 * Requiere autenticación JWT para todos los endpoints
 */
class PacienteService {
  /**
   * Obtener todos los pacientes
   * Requiere autenticación
   * @returns Lista de todos los pacientes
   */
  async getAllPacientes(): Promise<PacienteResponseDTO[]> {
    try {
      console.log('Obteniendo todos los pacientes...');
      const response = await api.get<PacienteResponseDTO[]>('/pacientes');
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los pacientes.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los pacientes.');
    }
  }

  /**
   * Obtener un paciente por su ID
   * Requiere autenticación
   * @param id ID del paciente
   * @returns Datos del paciente
   */
  async getPacienteById(id: number): Promise<PacienteResponseDTO> {
    try {
      console.log(`Obteniendo paciente ID: ${id}`);
      const response = await api.get<PacienteResponseDTO>(`/pacientes/${id}`);
      console.log('Paciente encontrado:', response.data.usuario.email);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener paciente:', error);

      if (error.response?.status === 404) {
        throw new Error('Paciente no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener el paciente.');
    }
  }

  /**
   * Obtener pacientes de una organización específica
   * Requiere autenticación
   * @param organizacionId ID de la organización
   * @returns Lista de pacientes de la organización
   */
  async getPacientesByOrganizacion(organizacionId: number): Promise<PacienteResponseDTO[]> {
    try {
      console.log(`Obteniendo pacientes de organización ID: ${organizacionId}`);
      const response = await api.get<PacienteResponseDTO[]>(`/pacientes/organizacion/${organizacionId}`);
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver pacientes de esta organización.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los pacientes de la organización.');
    }
  }

  /**
   * Obtener pacientes de la organización del usuario actual
   * Requiere autenticación
   * @returns Lista de pacientes de mi organización
   */
  async getPacientesFromMyOrganization(): Promise<PacienteResponseDTO[]> {
    try {
      console.log('Obteniendo pacientes de mi organización...');
      const response = await api.get<PacienteResponseDTO[]>('/pacientes/my-organization');
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los pacientes de tu organización.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los pacientes de tu organización.');
    }
  }

  /**
   * Obtener pacientes asignados a un profesional específico
   * Requiere autenticación
   * @param profesionalId ID del profesional
   * @returns Lista de pacientes asignados al profesional
   */
  async getPacientesByProfesional(profesionalId: number): Promise<PacienteResponseDTO[]> {
    try {
      console.log(`Obteniendo pacientes asignados al profesional ID: ${profesionalId}`);
      const response = await api.get<PacienteResponseDTO[]>(`/pacientes/profesional/${profesionalId}`);
      console.log(`Se encontraron ${response.data.length} pacientes asignados`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener pacientes del profesional:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los pacientes de este profesional.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los pacientes del profesional.');
    }
  }

  /**
   * Obtener mis pacientes asignados (usuario autenticado como profesional)
   * Requiere rol: PROFESIONAL
   * @returns Lista de pacientes asignados a mí
   */
  async getMisPacientes(): Promise<PacienteResponseDTO[]> {
    try {
      console.log('Obteniendo mis pacientes asignados...');
      const response = await api.get<PacienteResponseDTO[]>('/pacientes/mis-pacientes');
      console.log(`Se encontraron ${response.data.length} pacientes asignados`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener mis pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver pacientes. Requiere rol PROFESIONAL.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener tus pacientes asignados.');
    }
  }

  /**
   * Actualizar datos de un paciente
   * Requiere rol: ADMIN_ORGANIZACION
   * @param id ID del paciente
   * @param data Datos a actualizar
   * @returns Paciente actualizado
   */
  async updatePaciente(id: number, data: Partial<PacienteResponseDTO>): Promise<PacienteResponseDTO> {
    try {
      console.log(`Actualizando paciente ID: ${id}`);
      const response = await api.put<PacienteResponseDTO>(`/pacientes/${id}`, data);
      console.log('Paciente actualizado exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al actualizar paciente:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para actualizar pacientes.');
      }

      if (error.response?.status === 404) {
        throw new Error('Paciente no encontrado.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al actualizar el paciente.');
    }
  }

  /**
   * Buscar pacientes por nombre, apellidos o email
   * Requiere autenticación
   * @param query Texto de búsqueda
   * @returns Lista de pacientes que coinciden con la búsqueda
   */
  async searchPacientes(query: string): Promise<PacienteResponseDTO[]> {
    try {
      console.log(`Buscando pacientes con query: "${query}"`);
      const response = await api.get<PacienteResponseDTO[]>(`/pacientes/search?q=${encodeURIComponent(query)}`);
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al buscar pacientes:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para buscar pacientes.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al buscar pacientes.');
    }
  }

  /**
   * Obtener pacientes con detalle (optimizado sin N+1)
   * Requiere autenticación
   * @returns Lista de pacientes con detalle completo
   */
  async getPacientesDetalle(): Promise<PacienteDetalleDTO[]> {
    try {
      console.log('Obteniendo pacientes con detalle...');
      const response = await api.get<PacienteDetalleDTO[]>('/pacientes/detalle');
      console.log(`Se encontraron ${response.data.length} pacientes`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener pacientes detalle:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver los pacientes.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener los pacientes.');
    }
  }

  /**
   * Obtener detalle de un paciente específico
   * Requiere autenticación
   * @param id ID del paciente
   * @returns Detalle completo del paciente
   */
  async getDetallePaciente(id: number): Promise<PacienteDetalleDTO> {
    try {
      console.log(`Obteniendo detalle del paciente ID: ${id}`);
      const response = await api.get<PacienteDetalleDTO>(`/pacientes/${id}/detalle`);
      console.log('Detalle del paciente obtenido exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener detalle del paciente:', error);

      if (error.response?.status === 404) {
        throw new Error('Paciente no encontrado.');
      }

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver este paciente.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener el detalle del paciente.');
    }
  }
}

export default new PacienteService();