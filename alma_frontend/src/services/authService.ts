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

      // Asegurarnos de que el objeto de la petición coincide exactamente con lo que espera el backend
      const loginData = {
        email: email.trim(),
        password: password
      };

      console.log('Datos a enviar:', JSON.stringify(loginData, null, 2));

      const response = await api.post<LoginResponse>('/auth/login', loginData);

      console.log('Respuesta del servidor:', JSON.stringify(response.data, null, 2));

      // Guardar el token y datos del usuario en AsyncStorage
      await AsyncStorage.setItem('jwt_token', response.data.access_token);
      await AsyncStorage.setItem('user_email', response.data.email);
      await AsyncStorage.setItem('user_type', response.data.role);
      await AsyncStorage.setItem('password_temporal', String(response.data.password_temporal === true));

      return response.data;
    } catch (error: any) {
      console.error('Error completo:', error);
      console.error('Error response:', JSON.stringify(error.response?.data, null, 2));
      console.error('Error status:', error.response?.status);
      console.error('Error headers:', JSON.stringify(error.response?.headers, null, 2));
      console.error('Error message:', error.message);
      console.error('Datos enviados:', JSON.stringify({ email, password: '***' }, null, 2));

      // Manejo específico de errores según el código de estado
      if (error.response?.status === 401) {
        throw new Error('Email o contraseña incorrectos. Por favor, verifica tus credenciales.');
      }

      if (error.response?.status === 500) {
        // Error 500 puede ser credenciales incorrectas (problema del backend)
        throw new Error('Email o contraseña incorrectos. Si el problema persiste, contacta con soporte.');
      }

      if (error.response?.status === 403) {
        throw new Error('Tu cuenta está deshabilitada. Contacta con el administrador.');
      }

      if (error.response?.status === 423) {
        throw new Error('Tu cuenta está bloqueada. Contacta con el administrador.');
      }

      // Si hay un mensaje de error específico del servidor, lo mostramos
      if (error.response?.data?.message) {
        throw new Error(error.response.data.message);
      }

      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
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
    await AsyncStorage.removeItem('password_temporal');
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
      await AsyncStorage.setItem('password_temporal', String(response.data.password_temporal === true));

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
