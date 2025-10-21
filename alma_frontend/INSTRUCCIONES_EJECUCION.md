# 🚀 INSTRUCCIONES DE EJECUCIÓN - A.L.M.A v2.0

## ⚠️ ERROR COMÚN: @react-native-picker/picker

Si obtienes el error:
```
ERROR  Error: Unable to resolve module @react-native-picker/picker
```

### Solución Rápida

**1. Detén Metro Bundler** (Ctrl+C en la consola donde está corriendo)

**2. Limpia la caché y reinstala:**

```bash
cd alma_frontend

# Limpia caché de npm
npm cache clean --force

# Reinstala node_modules (opcional pero recomendado)
rm -rf node_modules
npm install

# Limpia caché de Metro
npx react-native start --reset-cache
```

**3. En OTRA terminal, ejecuta la app:**

```bash
cd alma_frontend
npx react-native run-android
```

---

## 📱 EJECUCIÓN NORMAL

### Primera vez

```bash
cd alma_frontend

# Instalar dependencias
npm install

# Ejecutar en Android
npx react-native run-android

# O ejecutar en iOS
npx react-native run-ios
```

### Ejecuciones posteriores

```bash
cd alma_frontend

# Iniciar Metro Bundler
npx react-native start

# En otra terminal, ejecutar la app
npx react-native run-android
```

---

## 🔧 SOLUCIÓN DE PROBLEMAS COMUNES

### 1. Error de módulo no encontrado

```bash
# Limpia todo y reinstala
cd alma_frontend
rm -rf node_modules
npm install
npx react-native start --reset-cache
```

### 2. Error de Gradle (Android)

```bash
cd alma_frontend/android
./gradlew clean
cd ..
npx react-native run-android
```

### 3. Error de Pod (iOS)

```bash
cd alma_frontend/ios
pod deintegrate
pod install
cd ..
npx react-native run-ios
```

### 4. Metro Bundler no responde

```bash
# Mata el proceso de Metro
npx react-native start --reset-cache

# O manualmente:
# Windows:
taskkill /F /IM node.exe

# Linux/Mac:
killall node

# Luego reinicia:
npx react-native start
```

---

## 📦 DEPENDENCIAS INSTALADAS

El proyecto incluye:

- ✅ `@react-native-picker/picker` v2.11.4 - Para selección de género y asignaciones
- ✅ `@react-native-async-storage/async-storage` v2.2.0 - Para almacenamiento local
- ✅ `@react-navigation/native` v7.1.18 - Navegación
- ✅ `@react-navigation/native-stack` v7.3.28 - Stack navigator
- ✅ `axios` v1.12.2 - HTTP requests
- ✅ `react-native-safe-area-context` v5.6.1 - SafeArea
- ✅ `react-native-screens` v4.17.1 - Screens optimization
- ✅ `react-native-video` v6.17.0 - Video player

---

## 🔗 CONFIGURACIÓN DEL BACKEND

Antes de ejecutar la app, asegúrate de configurar la IP del backend:

**Archivo:** `alma_frontend/.env`

```env
API_BASE_URL=http://TU_IP_AQUI:8080
```

**Ejemplo:**
```env
API_BASE_URL=http://192.168.1.100:8080
```

Para encontrar tu IP:

**Windows:**
```bash
ipconfig
# Busca "IPv4 Address" en tu adaptador de red activo
```

**Linux/Mac:**
```bash
ifconfig
# O
ip addr show
```

---

## 🧪 VERIFICACIÓN DE INSTALACIÓN

Para verificar que todo está correcto:

```bash
cd alma_frontend

# Verifica las dependencias instaladas
npm list @react-native-picker/picker
npm list @react-native-async-storage/async-storage

# Debería mostrar las versiones instaladas
```

---

## 🏗️ ESTRUCTURA DE ARCHIVOS IMPORTANTE

```
alma_frontend/
├── .env                    # ⚠️ Configurar IP del backend aquí
├── src/
│   ├── screens/           # Pantallas de la app
│   ├── styles/            # ✨ Estilos separados (NUEVO)
│   ├── services/          # Servicios API
│   ├── types/             # TypeScript types
│   ├── utils/             # Utilidades (validaciones)
│   ├── navigation/        # Navegación
│   └── theme/             # Tema global
└── android/               # Proyecto Android nativo
```

---

## 📊 COMANDOS ÚTILES

```bash
# Ver logs de Android
npx react-native log-android

# Ver logs de iOS
npx react-native log-ios

# Limpiar todo y empezar de cero
cd alma_frontend
rm -rf node_modules
rm -rf android/app/build
rm -rf ios/build
npm install
npx react-native start --reset-cache

# Luego en otra terminal:
npx react-native run-android
```

---

## ✅ CHECKLIST PRE-EJECUCIÓN

Antes de ejecutar la app, verifica:

- [ ] Node.js instalado (>=20)
- [ ] Android Studio instalado (para Android)
- [ ] Xcode instalado (para iOS, solo Mac)
- [ ] Emulador/Dispositivo conectado
- [ ] Backend corriendo en el puerto 8080
- [ ] IP del backend configurada en `.env`
- [ ] Dependencias instaladas (`npm install`)

---

## 🆘 AYUDA ADICIONAL

Si los problemas persisten:

1. **Verifica la versión de Node:**
   ```bash
   node --version
   # Debe ser >= 20
   ```

2. **Verifica React Native:**
   ```bash
   npx react-native --version
   ```

3. **Reinstala React Native CLI:**
   ```bash
   npm uninstall -g react-native-cli
   npm install -g @react-native-community/cli
   ```

4. **Revisa los logs completos:**
   ```bash
   npx react-native run-android --verbose
   ```

---

**Nota:** Después de instalar cualquier paquete nuevo con `npm install`, SIEMPRE reinicia Metro Bundler con `--reset-cache`.

---

**Última actualización:** 21 de Octubre de 2025