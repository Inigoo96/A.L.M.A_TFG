import api from './api';
import AsyncStorage from '@react-native-async-storage/async-storage';

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

export interface UpdatePasswordRequest {
  oldPassword: string;
  newPassword: string;
}

export interface RegisterOrganizationRequest {
  nombreOrganizacion: string;
  cif: string;
  nombre: string;
  apellidos: string;
  email: string;
  password: string;
}

class AuthService {
  async login(email: string, password: string): Promise<LoginResponse> {
    try {
      console.log('Intentando login con email:', email);
      console.log('URL completa:', 'http://localhost:8080/api/auth/login');

      const response = await api.post<LoginResponse>('/auth/login', {
        email,
        password,
      });

      console.log('Respuesta del servidor:', response.data);

      // Guardar el token en AsyncStorage
      await AsyncStorage.setItem('jwt_token', response.data.access_token);
      await AsyncStorage.setItem('user_email', response.data.email);
      await AsyncStorage.setItem('user_type', response.data.role);

      return response.data;
    } catch (error: any) {
      console.error('Error completo:', error);
      console.error('Error response:', error.response);
      console.error('Error message:', error.message);

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      if (error.message) {
        throw new Error(error.message);
      }
      throw new Error('Error al iniciar sesión. Por favor, intenta de nuevo.');
    }
  }

  async updatePassword(oldPassword: string, newPassword: string): Promise<void> {
    try {
      await api.put('/usuarios/me/password', {
        oldPassword,
        newPassword,
      });
    } catch (error: any) {
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      throw new Error('Error al actualizar la contraseña. Por favor, intenta de nuevo.');
    }
  }

  async logout(): Promise<void> {
    await AsyncStorage.removeItem('jwt_token');
    await AsyncStorage.removeItem('user_email');
    await AsyncStorage.removeItem('user_type');
  }

  async getToken(): Promise<string | null> {
    return await AsyncStorage.getItem('jwt_token');
  }

  async isAuthenticated(): Promise<boolean> {
    const token = await this.getToken();
    return token !== null;
  }

  async registerOrganization(data: RegisterOrganizationRequest): Promise<LoginResponse> {
    try {
      console.log('Registrando organización:', data.nombreOrganizacion);

      const response = await api.post<LoginResponse>('/auth/register-organization', data);

      console.log('Organización registrada exitosamente:', response.data);

      // Guardar el token automáticamente tras el registro
      await AsyncStorage.setItem('jwt_token', response.data.access_token);
      await AsyncStorage.setItem('user_email', response.data.email);
      await AsyncStorage.setItem('user_type', response.data.role);

      return response.data;
    } catch (error: any) {
      console.error('Error en registro:', error);
      console.error('Error response:', error.response);

      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }
      if (error.message) {
        throw new Error(error.message);
      }
      throw new Error('Error al registrar la organización. Por favor, intenta de nuevo.');
    }
  }
}

export default new AuthService();
