import axios from 'axios';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {getBaseURL, testBackendConnection, logConnectionInfo} from './apiConfig';

// ============================================
// CONFIGURACIÓN DE AXIOS
// ============================================

const api = axios.create({
  baseURL: getBaseURL(),
  headers: {
    'Content-Type': 'application/json',
    Accept: 'application/json',
  },
  timeout: 10000, // 10 segundos
});

// ============================================
// TEST DE CONEXIÓN AL INICIAR
// ============================================

let connectionTested = false;

async function testInitialConnection() {
  if (!connectionTested) {
    logConnectionInfo();

    const result = await testBackendConnection();

    if (result.success) {
      console.log('✅ Backend disponible:', result.url);
    } else {
      console.error('❌ Backend NO disponible:', result.message);
      console.error('URL intentada:', result.url);
    }

    connectionTested = true;
  }
}

// Probar conexión al iniciar
testInitialConnection();

// ============================================
// INTERCEPTOR DE REQUEST
// ============================================

api.interceptors.request.use(
  async config => {
    try {
      // Rutas que NO necesitan token
      const authRoutes = ['/auth/login', '/auth/register/organization'];
      const isAuthRoute = authRoutes.some(route => config.url?.includes(route));

      // Agregar token si no es ruta de autenticación
      if (!isAuthRoute) {
        const token = await AsyncStorage.getItem('jwt_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }

      // Log de petición (solo en desarrollo)
      if (__DEV__) {
        console.log('\n📤 REQUEST:', config.method?.toUpperCase(), config.url);
        if (config.data) {
          console.log('📦 Data:', JSON.stringify(config.data, null, 2));
        }
      }

      return config;
    } catch (error) {
      console.error('❌ Error en interceptor de request:', error);
      return config;
    }
  },
  error => {
    console.error('❌ Error antes de enviar request:', error);
    return Promise.reject(error);
  },
);

// ============================================
// INTERCEPTOR DE RESPONSE
// ============================================

api.interceptors.response.use(
  response => {
    // Log de respuesta exitosa (solo en desarrollo)
    if (__DEV__) {
      console.log('✅ RESPONSE:', response.status, response.config.url);
    }
    return response;
  },
  async error => {
    // Extraer información del error
    const status = error.response?.status;
    const url = error.config?.url;
    const method = error.config?.method?.toUpperCase();
    const errorMessage = error.response?.data?.message || error.message;

    // Log detallado del error
    console.error('\n❌ ERROR en petición API');
    console.error('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━');
    console.error('URL:', method, url);
    console.error('Status:', status || 'SIN RESPUESTA');
    console.error('Mensaje:', errorMessage);

    if (!status) {
      console.error('');
      console.error('⚠️ PROBLEMA DE RED:');
      console.error('  • El backend no está respondiendo');
      console.error('  • Verifica que esté corriendo en el puerto 8080');
      console.error('  • Verifica la IP en apiConfig.ts');
      console.error('  • Verifica tu conexión de red');
    }

    console.error('━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n');

    // Manejo específico de errores por código
    switch (status) {
      case 400:
        console.warn('🔴 Datos inválidos o parámetros incorrectos');
        break;

      case 401:
        console.warn('🔒 Token inválido o expirado. Limpiando sesión...');
        try {
          await AsyncStorage.multiRemove([
            'jwt_token',
            'user_email',
            'user_type',
            'password_temporal',
          ]);
        } catch (storageError) {
          console.error('Error al limpiar sesión:', storageError);
        }
        break;

      case 403:
        console.warn('🚫 Acceso denegado - Permisos insuficientes');
        break;

      case 404:
        console.warn('🔍 Recurso no encontrado');
        break;

      case 409:
        console.warn('⚠️ Conflicto - Recurso duplicado');
        break;

      case 500:
        console.error('💥 Error interno del servidor');
        break;

      default:
        if (!status) {
          console.error('🌐 Error de red - Backend no alcanzable');
        }
    }

    return Promise.reject(error);
  },
);

// ============================================
// EXPORTAR API
// ============================================

export default api;

// Exportar funciones útiles
export {testBackendConnection, logConnectionInfo};