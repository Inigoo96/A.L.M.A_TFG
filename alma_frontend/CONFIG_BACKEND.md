# ⚙️ CONFIGURACIÓN DE CONEXIÓN AL BACKEND

## 🎯 Configuración en 3 Pasos

### 1️⃣ Obtén tu IP

**En Windows:**
```bash
ipconfig
```

Busca la línea que dice **"IPv4 Address"** en tu adaptador Wi-Fi o Ethernet activo.

Ejemplo:
```
Wireless LAN adapter Wi-Fi:
   IPv4 Address. . . . . . . . . . . : 172.17.21.70
```

---

### 2️⃣ Cambia la IP en el código

Abre el archivo:
```
alma_frontend/src/services/apiConfig.ts
```

Cambia la línea 13:
```typescript
const API_BASE_URL = 'http://TU_IP_AQUI:8080/api';
```

Por ejemplo:
```typescript
const API_BASE_URL = 'http://172.17.21.70:8080/api';
```

---

### 3️⃣ Reinicia Metro Bundler

En la terminal donde corre Metro, presiona `Ctrl+C` y luego:

```bash
npx react-native start --reset-cache
```

En otra terminal:
```bash
npx react-native run-android
```

---

## ✅ Verificación

Cuando la app inicie, verás en los logs:

### Si está bien configurado:
```
============================================
📡 CONFIGURACIÓN DE CONEXIÓN API
============================================
URL del Backend: http://172.17.21.70:8080/api
...
============================================

🔍 Probando conexión con backend: http://172.17.21.70:8080/api
✅ Backend conectado exitosamente
✅ Backend disponible: http://172.17.21.70:8080/api
```

### Si hay error:
```
🔍 Probando conexión con backend: http://172.17.21.70:8080/api
❌ Error de conexión con backend: Network request failed
❌ Backend NO disponible: Error de red: Verifica que el backend esté corriendo y la IP sea correcta
URL intentada: http://172.17.21.70:8080/api
```

---

## 🔧 Solución de Problemas

### Error: "Backend NO disponible"

**Verifica que el backend esté corriendo:**

```bash
cd alma_backend/alma_backend
mvn spring-boot:run
```

Espera a ver:
```
Tomcat started on port(s): 8080 (http)
```

---

### Error: "Network request failed"

**Posibles causas:**

1. **Backend no está corriendo** → Inicia el backend
2. **IP incorrecta** → Verifica con `ipconfig` y actualiza `apiConfig.ts`
3. **Firewall bloqueando** → Permite el puerto 8080 en Windows Firewall
4. **Diferentes redes** → Tu PC y móvil deben estar en la misma red WiFi

---

### Error: "Timeout"

El backend está muy lento o no responde:

1. Verifica que el backend no tenga errores en los logs
2. Aumenta el timeout en `api.ts` línea 15:
   ```typescript
   timeout: 30000, // 30 segundos
   ```

---

## 🌐 Usando ADB Reverse (Alternativa)

Si tienes problemas con IPs, puedes usar ADB reverse:

```bash
# Redirige el puerto 8080 del emulador al puerto 8080 de tu PC
adb reverse tcp:8080 tcp:8080
```

Luego cambia la IP en `apiConfig.ts`:
```typescript
const API_BASE_URL = 'http://localhost:8080/api';
```

**Nota:** Esto solo funciona con emulador/dispositivo conectado por USB.

---

## 📝 Ejemplo Completo

### Tu configuración actual:
- **PC con backend:** IP `172.17.21.70`
- **Backend corriendo en:** Puerto `8080`
- **Móvil/Emulador:** En la misma red WiFi

### Configuración en apiConfig.ts:
```typescript
const API_BASE_URL = 'http://172.17.21.70:8080/api';
```

### Verificación:
1. Backend corriendo: ✅
2. IP configurada: ✅
3. Metro reiniciado: ✅
4. App cargada: ✅

**Resultado esperado:**
```
✅ Backend disponible: http://172.17.21.70:8080/api
```

---

## 🆘 Si Nada Funciona

1. **Verifica el endpoint `/health`** del backend:

   Abre en el navegador de tu PC:
   ```
   http://localhost:8080/api/health
   ```

   Si no funciona, el backend tiene un problema.

2. **Ping desde el móvil** (si tienes acceso):

   Instala una app de terminal y ejecuta:
   ```bash
   ping 172.17.21.70
   ```

3. **Revisa el firewall de Windows**:
   - Panel de Control → Firewall
   - Configuración avanzada
   - Reglas de entrada
   - Nueva regla → Puerto TCP 8080 → Permitir

---

**Última actualización:** 21 de Octubre de 2025
