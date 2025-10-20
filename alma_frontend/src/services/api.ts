import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { detectBackendURL } from './apiConfig';

// Instancia de Axios que se configurará dinámicamente
const api = axios.create({
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  },
  timeout: 10000, // 10 segundos de timeout
});

// Inicializar la detección automática de la URL del backend
let isInitialized = false;

async function initializeAPI() {
  if (!isInitialized) {
    const baseURL = await detectBackendURL();
    api.defaults.baseURL = baseURL;
    console.log('📡 API inicializada con URL:', baseURL);
    isInitialized = true;
  }
}

// Inicializar inmediatamente
initializeAPI();

// Interceptor para agregar el token JWT a todas las peticiones
api.interceptors.request.use(
  async (config) => {
    try {
      // No agregar token en rutas de autenticación
      const authRoutes = ['/auth/login', '/auth/register-organization'];
      const isAuthRoute = authRoutes.some(route => config.url?.includes(route));

      if (!isAuthRoute) {
        const token = await AsyncStorage.getItem('jwt_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }

      // Log de la petición para debugging
      console.log('========= REQUEST =========');
      console.log('URL:', (config.baseURL || '') + (config.url || ''));
      console.log('Method:', config.method);
      console.log('Headers:', JSON.stringify(config.headers, null, 2));
      console.log('Data:', JSON.stringify(config.data, null, 2));
      console.log('===========================');

      return config;
    } catch (error) {
      console.error('Error en el interceptor:', error);
      return config;
    }
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Interceptor para manejar las respuestas y errores
api.interceptors.response.use(
  (response) => {
    console.log('========= RESPONSE =========');
    console.log('Status:', response.status);
    console.log('Data:', JSON.stringify(response.data, null, 2));
    console.log('============================');
    return response;
  },
  (error) => {
    console.error('========= ERROR RESPONSE =========');
    console.error('Status:', error.response?.status);
    console.error('Error Data:', JSON.stringify(error.response?.data, null, 2));
    console.error('Error Message:', error.message);
    console.error('Request URL:', error.config?.url);
    console.error('Request Data:', JSON.stringify(error.config?.data, null, 2));
    console.error('==================================');
    return Promise.reject(error);
  }
);

export default api;
