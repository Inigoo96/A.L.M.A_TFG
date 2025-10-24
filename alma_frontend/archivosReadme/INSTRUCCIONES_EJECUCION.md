# 🚀 Instrucciones de Ejecución - A.L.M.A. Frontend

Esta guía describe los pasos para levantar el entorno de desarrollo completo de la aplicación A.L.M.A. (backend y frontend).

**¡Buena noticia!** La configuración de la dirección IP para conectar el frontend con el backend ahora es **100% automática** en modo de desarrollo. Ya no necesitas buscar tu IP ni modificar ningún archivo de configuración.

---

## 📋 Prerrequisitos

Asegúrate de tener instalado y configurado:

1.  **Node.js** (versión 20 o superior)
2.  **JDK 17** (o superior)
3.  **Maven**
4.  **Android Studio** (para desarrollo en Android) con un emulador configurado o un dispositivo físico con depuración USB habilitada.
5.  **Xcode** (para desarrollo en iOS, solo en macOS).

---

## ⚙️ Pasos para la Ejecución (Flujo Simplificado)

Necesitarás **3 terminales** abiertas en la raíz del proyecto (`c:\SValero\TFG\A.L.M.A_TFG\`).

### 1. Levantar el Backend

En la **primera terminal**, inicia el servidor de Spring Boot:

```bash
# Navega a la carpeta del backend
cd alma_backend\alma_backend

# Ejecuta el backend
mvn spring-boot:run
```

> Deja esta terminal corriendo. El backend estará disponible en `http://localhost:8080`.

### 2. Levantar el Servidor de Desarrollo (Metro)

En la **segunda terminal**, inicia el servidor de React Native (Metro).

```bash
# Navega a la carpeta del frontend
cd alma_frontend

# (Si es la primera vez) Instala las dependencias
npm install

# Inicia el servidor Metro
npx react-native start
```

> Deja esta terminal abierta. Verás que muestra un **código QR**. La app se conectará a este servidor para obtener el código en tiempo real.

### 3. Lanzar la Aplicación Móvil

En la **tercera terminal**, lanza la aplicación en tu emulador o dispositivo físico.

```bash
# Navega a la carpeta del frontend
cd alma_frontend

# Lanza la app en Android
npx react-native run-android

# O en iOS (solo en macOS)
# npx react-native run-ios
```

---


## ✨ ¡Y listo!

La aplicación se instalará y abrirá en tu dispositivo. Se conectará automáticamente al servidor Metro y al backend sin ninguna configuración manual.

---

## 🔧 Solución de Problemas Comunes

- **Error: `Unable to resolve module...` o similar:**
  - Detén todo y limpia la caché de Metro. En la terminal de Metro (la 2ª), pulsa `Ctrl+C` y vuelve a ejecutar:
    ```bash
    npx react-native start --reset-cache
    ```

- **La app no conecta con Metro (pantalla en blanco o error rojo):**
  - Asegúrate de que tu ordenador y tu móvil están **en la misma red Wi-Fi**.
  - A veces, los firewalls pueden bloquear la conexión. Desactívalo temporalmente para probar.

- **Error de Gradle (Android):**
  ```bash
  cd alma_frontend/android
  ./gradlew clean
  cd ..
  npx react-native run-android
  ```

- **La app no conecta con el Backend (errores de red en la app):**
  - Verifica que la terminal del backend (la 1ª) no muestra ningún error y sigue corriendo.
  - Asegúrate de que nada más está usando el puerto `8080` en tu ordenador.

---

## 🛠️ Script de inicio rápido (opcional)

Si quieres simplificar los pasos diarios he añadido un script PowerShell que automatiza la conexión ADB (wireless), arranca Metro y compila/instala el APK.

Archivo: `alma_frontend/scripts/dev-start.ps1`

Uso básico (desde la carpeta `alma_frontend`):

```powershell
# Arranca Metro en una nueva ventana y compila+instala el APK en el dispositivo (pasa la IP si usas adb wireless)
.\scripts\dev-start.ps1 -DeviceIP "192.168.1.43:46319" -Install -StartMetro

# Solo iniciar Metro en una nueva ventana
.\scripts\dev-start.ps1 -StartMetro

# Ejecutar `npx react-native run-android` completo (build + install)
.\scripts\dev-start.ps1 -UseRunAndroid
```

Notas:
- El script intentará localizar `adb.exe` automáticamente. Si no lo encuentra añade platform-tools a tu PATH o asegúrate de que está instalado en `%LOCALAPPDATA%\Android\Sdk\platform-tools`.
- Si tu dispositivo requiere emparejamiento wireless (pair code) hazlo una vez mediante `adb pair` como lo hiciste antes; el script asumirá que el dispositivo acepta conexiones en la IP:PUERTO que le pases.
- El script es opcional pero pensado para que el equipo lance todo con un solo comando.

---

**Última actualización:** 24 de Octubre de 2025
