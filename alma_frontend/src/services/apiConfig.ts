import { NativeModules } from 'react-native';

// ============================================
// CONFIGURACIÓN DE LA API
// ============================================

// URL de producción (se usará en builds de release)
const PRODUCTION_URL = 'https://prod.alma-api.com/api';

// --- OPCIÓN: FORZAR IP MANUALMENTE EN DESARROLLO ---
// Si prefieres configurar la IP manualmente, asigna aquí la dirección
// en formato '192.168.1.43' o '192.168.1.43:8080' (si incluyes puerto).
// Déjalo vacío para usar la detección automática de Metro.
let MANUAL_DEV_HOST: string = '192.168.1.45:8080';

// Si quieres cambiar la IP desde código en tiempo de ejecución, puedes
// llamar a setManualDevHost('192.168.1.43') desde cualquier sitio.
export function setManualDevHost(host: string) {
  MANUAL_DEV_HOST = host || '';
}

let API_BASE_URL: string;

if (__DEV__) {
  // --- MODO DESARROLLO ---
  if (MANUAL_DEV_HOST && MANUAL_DEV_HOST.length > 0) {
    // Soporta host con o sin puerto. Si no incluye '/api', lo añadimos.
    const host = MANUAL_DEV_HOST.replace(/\/+$/g, '');
    const hasPort = host.includes(':');
    API_BASE_URL = `http://${host}${host.includes('/api') ? '' : '/api'}`;
    console.log(`🔧 Modo Desarrollo: Usando host MANUAL -> ${host}`);
  } else {
    // Intenta obtener la IP del host de Metro Bundler
    try {
      const scriptURL = NativeModules.SourceCode.scriptURL as string;
      const address = scriptURL?.split('://')[1]?.split(':')[0];

      if (address) {
        API_BASE_URL = `http://${address}:8080/api`;
        console.log(`🤖 Modo Desarrollo: IP del host detectada -> ${address}`);
      } else {
        console.warn('⚠️ No se pudo detectar la IP del host, usando localhost.');
        API_BASE_URL = 'http://localhost:8080/api';
      }
    } catch (e) {
      console.warn('⚠️ Error detectando scriptURL, usando localhost.');
      API_BASE_URL = 'http://localhost:8080/api';
    }
  }
} else {
  // --- MODO PRODUCCIÓN ---
  API_BASE_URL = PRODUCTION_URL;
  console.log(`🚀 Modo Producción: Usando API en ${API_BASE_URL}`);
}

// ============================================ 
// NO MODIFICAR DEBAJO DE ESTA LÍNEA
// ============================================ 

/**
 * Obtiene la URL base de la API.
 * En desarrollo, es dinámica. En producción, es fija.
 */
export function getBaseURL(): string {
  return API_BASE_URL;
}

/**
 * Verifica si el backend está disponible
 */
export async function testBackendConnection(): Promise<{
  success: boolean;
  message: string;
  url: string;
}> {
  const url = getBaseURL(); // Usa la función para obtener la URL dinámica/fija

  try {
    console.log('🔍 Probando conexión con backend:', url);

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000);

    const response = await fetch(`${url}/health`, {
      method: 'GET',
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    if (response.ok || response.status === 401) {
      return {
        success: true,
        message: 'Conexión exitosa con el backend',
        url,
      };
    }

    return {
      success: false,
      message: `Backend respondió con error: HTTP ${response.status}`,
      url,
    };
  } catch (error: any) {
    let errorMessage = 'No se puede conectar al backend';
    if (error.name === 'AbortError') {
      errorMessage = 'Timeout: El backend no responde (>5s)';
    } else if (error.message.includes('Network request failed')) {
      errorMessage = 'Error de red: Verifica que el backend esté corriendo';
    }
    return {
      success: false,
      message: errorMessage,
      url,
    };
  }
}

/**
 * Muestra información de diagnóstico en consola
 */
export function logConnectionInfo(): void {
  console.log('\n============================================');
  console.log('📡 CONFIGURACIÓN DE CONEXIÓN API');
  console.log('============================================');
  console.log('URL del Backend:', getBaseURL());
  if (__DEV__) {
    console.log('Modo: Desarrollo');
    if (MANUAL_DEV_HOST && MANUAL_DEV_HOST.length > 0) {
      console.log('  > Host manual configurado en `MANUAL_DEV_HOST` o vía setManualDevHost()');
    } else {
      console.log('  > IP automática (detectada desde Metro).');
    }
  } else {
    console.log('Modo: Producción');
  }

  console.log('\n🔧 Para forzar una IP en desarrollo, edita `MANUAL_DEV_HOST` en este archivo o llama a setManualDevHost("192.168.1.43").');
  console.log('============================================\n');
}
