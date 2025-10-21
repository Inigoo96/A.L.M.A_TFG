import api from './api';
import {
  OrganizacionDTO,
  OrganizacionEstadisticasDTO,
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
      console.log('Obteniendo todas las organizaciones...');
      const response = await api.get<OrganizacionDTO[]>('/organizaciones');
      console.log(`Se encontraron ${response.data.length} organizaciones`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organizaciones:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver las organizaciones. Requiere rol ADMIN_ORGANIZACION o SUPER_ADMIN.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener las organizaciones.');
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
      console.log(`Obteniendo organización ID: ${id}`);
      const response = await api.get<OrganizacionDTO>(`/organizaciones/${id}`);
      console.log('Organización encontrada:', response.data.nombreOficial);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organización:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver esta organización.');
      }

      if (error.response?.status === 404) {
        throw new Error('Organización no encontrada.');
      }

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
      console.log(`Obteniendo organización con CIF: ${cif}`);
      const response = await api.get<OrganizacionDTO>(`/organizaciones/cif/${cif}`);
      console.log('Organización encontrada:', response.data.nombreOficial);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener organización por CIF:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver esta organización.');
      }

      if (error.response?.status === 404) {
        throw new Error('Organización no encontrada con ese CIF.');
      }

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
  async createOrganizacion(organizacion: Partial<OrganizacionDTO>): Promise<OrganizacionDTO> {
    try {
      console.log('Creando organización:', organizacion.nombreOficial);
      const response = await api.post<OrganizacionDTO>('/organizaciones', organizacion);
      console.log('Organización creada exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al crear organización:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para crear organizaciones. Requiere rol SUPER_ADMIN.');
      }

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
  async updateOrganizacion(id: number, organizacion: Partial<OrganizacionDTO>): Promise<OrganizacionDTO> {
    try {
      console.log(`Actualizando organización ID: ${id}`);
      const response = await api.put<OrganizacionDTO>(`/organizaciones/${id}`, organizacion);
      console.log('Organización actualizada exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al actualizar organización:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para actualizar organizaciones. Requiere rol SUPER_ADMIN.');
      }

      if (error.response?.status === 404) {
        throw new Error('Organización no encontrada.');
      }

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
      console.log(`Eliminando organización ID: ${id}`);
      await api.delete(`/organizaciones/${id}`);
      console.log('Organización eliminada exitosamente');
    } catch (error: any) {
      console.error('Error al eliminar organización:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para eliminar organizaciones. Requiere rol SUPER_ADMIN.');
      }

      if (error.response?.status === 404) {
        throw new Error('Organización no encontrada.');
      }

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
  async getEstadisticasOrganizaciones(): Promise<OrganizacionEstadisticasDTO[]> {
    try {
      console.log('Obteniendo estadísticas de organizaciones...');
      const response = await api.get<OrganizacionEstadisticasDTO[]>('/organizaciones/estadisticas');
      console.log(`Estadísticas obtenidas para ${response.data.length} organizaciones`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estadísticas. Requiere rol SUPER_ADMIN.');
      }

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
  async getEstadisticasOrganizacion(id: number): Promise<OrganizacionEstadisticasDTO> {
    try {
      console.log(`Obteniendo estadísticas de organización ID: ${id}`);
      const response = await api.get<OrganizacionEstadisticasDTO>(`/organizaciones/${id}/estadisticas`);
      console.log('Estadísticas obtenidas exitosamente');
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estadísticas. Requiere rol SUPER_ADMIN.');
      }

      if (error.response?.status === 404) {
        throw new Error('Organización no encontrada.');
      }

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
  async getEstadisticasOrganizacionesActivas(): Promise<OrganizacionEstadisticasDTO[]> {
    try {
      console.log('Obteniendo estadísticas de organizaciones activas...');
      const response = await api.get<OrganizacionEstadisticasDTO[]>('/organizaciones/estadisticas/activas');
      console.log(`Estadísticas obtenidas para ${response.data.length} organizaciones activas`);
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener estadísticas:', error);

      if (error.response?.status === 403) {
        throw new Error('No tienes permisos para ver estadísticas. Requiere rol SUPER_ADMIN.');
      }

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      throw new Error('Error al obtener estadísticas de organizaciones activas.');
    }
  }
}

export default new OrganizacionService();