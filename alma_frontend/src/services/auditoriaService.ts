import api from './api';
import { AuditoriaDTO } from '../types/api.types';

/**
 * Servicio para gestionar la auditoría del sistema
 */
class AuditoriaService {
  /**
   * Obtener las N acciones de auditoría más recientes del sistema
   * Requiere rol: SUPER_ADMIN
   * @param limit El número de registros a obtener
   * @returns Lista de acciones de auditoría
   */
  async getAuditoriaReciente(limit: number = 50): Promise<AuditoriaDTO[]> {
    try {
      const response = await api.get<AuditoriaDTO[]>(
        `/organizaciones/auditoria/recientes?limit=${limit}`,
      );
      return response.data;
    } catch (error: any) {
      console.error('Error al obtener la auditoría reciente:', error);
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al obtener la auditoría del sistema.');
    }
  }
}

export default new AuditoriaService();
