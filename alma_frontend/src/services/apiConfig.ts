/**
 * ⚠️ CONFIGURACIÓN DEL BACKEND API - A.L.M.A v2.0
 *
 * INSTRUCCIONES:
 * 1. Ejecuta 'ipconfig' en Windows para obtener tu IPv4
 * 2. Cambia la IP en API_BASE_URL abajo
 * 3. Asegúrate de que el backend esté corriendo en el puerto 8080
 */

// ============================================
// CONFIGURACIÓN - CAMBIA ESTA IP
// ============================================
const API_BASE_URL = 'http://192.168.1.39:8080/api';

// ============================================
// NO MODIFICAR DEBAJO DE ESTA LÍNEA
// ============================================

/**
 * Obtiene la URL base del API
 * Simple y directo - sin detección automática
 */
export function getBaseURL(): string {
  return API_BASE_URL;
}

/**
 * Verifica si el backend está disponible
 * Retorna true si responde, false si no
 */
export async function testBackendConnection(): Promise<{
  success: boolean;
  message: string;
  url: string;
}> {
  const url = API_BASE_URL;

  try {
    console.log('🔍 Probando conexión con backend:', url);

    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 segundos

    const response = await fetch(`${url}/health`, {
      method: 'GET',
      signal: controller.signal,
    });

    clearTimeout(timeoutId);

    // Aceptamos respuestas 200-401 como "backend disponible"
    // 401 es normal si el endpoint /health requiere autenticación
    if (response.ok || response.status === 401) {
      console.log('✅ Backend conectado exitosamente');
      return {
        success: true,
        message: 'Conexión exitosa con el backend',
        url,
      };
    } else {
      console.error('❌ Backend respondió con error:', response.status);
      return {
        success: false,
        message: `Backend respondió con error: HTTP ${response.status}`,
        url,
      };
    }
  } catch (error: any) {
    console.error('❌ Error de conexión con backend:', error.message);

    let errorMessage = 'No se puede conectar al backend';

    if (error.name === 'AbortError') {
      errorMessage = 'Timeout: El backend no responde (>5s)';
    } else if (error.message.includes('Network request failed')) {
      errorMessage = 'Error de red: Verifica que el backend esté corriendo y la IP sea correcta';
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
  console.log('URL del Backend:', API_BASE_URL);
  console.log('');
  console.log('⚠️ SI NO PUEDES CONECTARTE:');
  console.log('1. Verifica que el backend esté corriendo: mvn spring-boot:run');
  console.log('2. Verifica tu IP con: ipconfig (Windows) o ifconfig (Mac/Linux)');
  console.log('3. Cambia API_BASE_URL en src/services/apiConfig.ts');
  console.log('4. Reinicia Metro: npx react-native start --reset-cache');
  console.log('============================================\n');
}