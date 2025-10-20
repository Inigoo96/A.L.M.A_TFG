/**
 * Configuración del servicio API con detección automática de IP
 *
 * En DESARROLLO:
 * - Intenta conectarse automáticamente probando IPs comunes en tu red
 * - Usa la IP configurada directamente como fallback
 *
 * En PRODUCCIÓN:
 * - Cambia FALLBACK_API_URL por tu dominio de producción
 */

const BACKEND_PORT = '8080';
const API_PATH = '/api';

// Configuración de la IP del backend (cambiar según tu red)
// Para obtener tu IP ejecuta: ipconfig (Windows) o ifconfig (Mac/Linux)
const FALLBACK_API_URL = 'http://10.242.170.39:8080/api';

// Lista de URLs a intentar en orden de prioridad
let cachedBaseURL: string | null = null;

/**
 * Detecta automáticamente la URL del backend
 * Intenta diferentes estrategias en este orden:
 * 1. URL de producción (si está configurada)
 * 2. IPs comunes en redes privadas (probando múltiples rangos)
 * 3. IP configurada como fallback
 * 4. Fallback a localhost
 */
export async function detectBackendURL(): Promise<string> {
  // Si ya tenemos una URL en caché, usarla
  if (cachedBaseURL) {
    return cachedBaseURL;
  }

  // 1. Si hay una URL de producción configurada (no localhost), usarla directamente
  // Descomenta y configura esto para producción:
  // const PRODUCTION_URL = 'https://api.tudominio.com/api';
  // if (PRODUCTION_URL && !PRODUCTION_URL.includes('localhost')) {
  //   console.log('✅ Usando URL de producción:', PRODUCTION_URL);
  //   cachedBaseURL = PRODUCTION_URL;
  //   return PRODUCTION_URL;
  // }

  // 2. Intentar detectar automáticamente la IP del backend en la red local
  // Probamos rangos comunes de IPs privadas
  const commonIPRanges = [
    '172.17.21',  // Tu red actual
    '192.168.1',  // Red doméstica común
    '10.242.170.39',  // Otra red doméstica común
    '10.0.0',     // Red corporativa común
  ];

  const commonHosts = [70, 1, 100, 10, 2]; // Terminaciones comunes

  console.log('🔍 Buscando backend en la red local...');

  for (const range of commonIPRanges) {
    for (const host of commonHosts) {
      const testURL = `http://${range}.${host}:${BACKEND_PORT}${API_PATH}`;
      const isReachable = await testConnection(testURL);

      if (isReachable) {
        console.log('✅ Backend detectado automáticamente en:', testURL);
        cachedBaseURL = testURL;
        return testURL;
      }
    }
  }

  console.warn('⚠️ No se pudo detectar el backend automáticamente');

  // 3. Usar la IP configurada como fallback
  if (FALLBACK_API_URL) {
    console.log('✅ Usando URL configurada como fallback:', FALLBACK_API_URL);
    cachedBaseURL = FALLBACK_API_URL;
    return FALLBACK_API_URL;
  }

  // 4. Fallback final a localhost (solo funciona con adb reverse)
  const fallbackURL = `http://localhost:${BACKEND_PORT}${API_PATH}`;
  console.log('⚠️ Usando fallback a localhost (requiere adb reverse):', fallbackURL);
  cachedBaseURL = fallbackURL;
  return fallbackURL;
}

/**
 * Prueba si una URL es alcanzable
 * Intenta una petición GET simple al endpoint /health o la raíz
 */
async function testConnection(url: string): Promise<boolean> {
  try {
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 2000); // 2 segundos timeout

    const response = await fetch(`${url}/health`, {
      method: 'GET',
      signal: controller.signal,
    });

    clearTimeout(timeoutId);
    return response.ok || response.status < 500; // Acepta cualquier respuesta que no sea error de servidor
  } catch (error) {
    return false;
  }
}

/**
 * Limpia la URL en caché para forzar una nueva detección
 * Útil cuando cambia la red del dispositivo
 */
export function resetBackendURL(): void {
  cachedBaseURL = null;
  console.log('🔄 Cache de URL del backend limpiado');
}
