import api from './api';
import {
  OrganizacionDTO,
  OrganizacionEstadisticasDTO,
  CambioEstadoOrganizacionDTO,
  AuditoriaDTO,
  EstadoOrganizacion,
} from '../types/api.types';

/**
 * Servicio para gestionar las organizaciones
 * Requiere autenticación JWT para todos los endpoints
 */
class OrganizacionService {
  /**
   * Obtener todas las organizaciones
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @returns Lista de todas las organizaciones
   */
  async getAllOrganizaciones(): Promise<OrganizacionDTO[]> {
    try {
      const response = await api.get<OrganizacionDTO[]>('/organizaciones');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organizaciones:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener las organizaciones.');
    }
  }

  /**
   * Obtener organizaciones por su estado
   * Requiere rol: SUPER_ADMIN
   * @param estado El estado por el cual filtrar
   * @returns Lista de organizaciones
   */
  async getOrganizacionesPorEstado(
    estado: EstadoOrganizacion,
  ): Promise<OrganizacionDTO[]> {
    try {
      const response = await api.get<OrganizacionDTO[]>(
        `/organizaciones/estado/${estado}`,
      );
      return response.data;
    } catch (error: any) {
      console.error(`Error al obtener organizaciones por estado ${estado}:`, error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener las organizaciones por estado.');
    }
  }

  /**
   * Cambia el estado de una organización (activar, suspender, dar de baja)
   * Requiere rol: SUPER_ADMIN
   * @param id ID de la organización
   * @param data DTO con el nuevo estado y el motivo
   * @returns La organización actualizada
   */
  async cambiarEstadoOrganizacion(
    id: number,
    data: CambioEstadoOrganizacionDTO,
  ): Promise<OrganizacionDTO> {
    try {
      const response = await api.put<OrganizacionDTO>(
        `/organizaciones/${id}/cambiar-estado`,
        data,
      );
      return response.data;
    } catch (error: any) {
      console.error(`Error al cambiar el estado de la organización ${id}:`, error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al cambiar el estado de la organización.');
    }
  }

  /**
   * Obtiene el historial de auditoría de una organización específica
   * Requiere rol: SUPER_ADMIN
   * @param id ID de la organización
   * @returns Lista de registros de auditoría
   */
  async getAuditoriaOrganizacion(id: number): Promise<AuditoriaDTO[]> {
    try {
      const response = await api.get<AuditoriaDTO[]>(
        `/organizaciones/${id}/auditoria`,
      );
      return response.data;
    } catch (error: any) {
      console.error(
        `Error al obtener la auditoría de la organización ${id}:`,
        error,
      );
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener la auditoría de la organización.');
    }
  }

  /**
   * Obtener una organización por su ID
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @param id ID de la organización
   * @returns Datos de la organización
   */
  async getOrganizacionById(id: number): Promise<OrganizacionDTO> {
    try {
      const response = await api.get<OrganizacionDTO>(`/organizaciones/${id}`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organización:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener la organización.');
    }
  }

  /**
   * Obtener una organización por su CIF
   * Requiere rol: ADMIN_ORGANIZACION o SUPER_ADMIN
   * @param cif CIF de la organización
   * @returns Datos de la organización
   */
  async getOrganizacionByCif(cif: string): Promise<OrganizacionDTO> {
    try {
      const response = await api.get<OrganizacionDTO>(`/organizaciones/cif/${cif}`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organización por CIF:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener la organización.');
    }
  }

  /**
   * Crear una nueva organización
   * Requiere rol: SUPER_ADMIN
   * @param organizacion Datos de la organización
   * @returns Organización creada
   */
  async createOrganizacion(
    organizacion: Partial<OrganizacionDTO>,
  ): Promise<OrganizacionDTO> {
    try {
      const response = await api.post<OrganizacionDTO>(
        '/organizaciones',
        organizacion,
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al crear organización:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al crear la organización.');
    }
  }

  /**
   * Actualizar una organización existente
   * Requiere rol: SUPER_ADMIN
   * @param id ID de la organización
   * @param organizacion Datos actualizados
   * @returns Organización actualizada
   */
  async updateOrganizacion(
    id: number,
    organizacion: Partial<OrganizacionDTO>,
  ): Promise<OrganizacionDTO> {
    try {
      const response = await api.put<OrganizacionDTO>(
        `/organizaciones/${id}`,
        organizacion,
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al actualizar organización:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al actualizar la organización.');
    }
  }

  /**
   * Eliminar una organización
   * Requiere rol: SUPER_ADMIN
   * @param id ID de la organización
   */
  async deleteOrganizacion(id: number): Promise<void> {
    try {
      await api.delete(`/organizaciones/${id}`);
    } catch (error: any) {
      console.error('Error al eliminar organización:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al eliminar la organización.');
    }
  }

  /**
   * Obtener estadísticas de todas las organizaciones
   * Requiere rol: SUPER_ADMIN
   * @returns Estadísticas de todas las organizaciones
   */
  async getEstadisticasOrganizaciones(): Promise<
    OrganizacionEstadisticasDTO[]
  > {
    try {
      const response = await api.get<OrganizacionEstadisticasDTO[]>(
        '/organizaciones/estadisticas',
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener estadísticas de organizaciones.');
    }
  }

  /**
   * Obtener estadísticas de una organización específica
   * Requiere rol: SUPER_ADMIN
   * @param id ID de la organización
   * @returns Estadísticas de la organización
   */
  async getEstadisticasOrganizacion(
    id: number,
  ): Promise<OrganizacionEstadisticasDTO> {
    try {
      const response = await api.get<OrganizacionEstadisticasDTO>(
        `/organizaciones/${id}/estadisticas`,
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener estadísticas de la organización.');
    }
  }

  /**
   * Obtener estadísticas de organizaciones activas
   * Requiere rol: SUPER_ADMIN
   * @returns Estadísticas de organizaciones activas
   */
  async getEstadisticasOrganizacionesActivas(): Promise<
    OrganizacionEstadisticasDTO[]
  > {
    try {
      const response = await api.get<OrganizacionEstadisticasDTO[]>(
        '/organizaciones/estadisticas/activas',
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener estadísticas de organizaciones activas.');
    }
  }
}

export default new OrganizacionService();
