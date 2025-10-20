# Configuración de Conexión al Backend

## Detección Automática de IP

La aplicación ahora detecta **automáticamente** la IP del backend sin necesidad de configuración manual.

### ¿Cómo funciona?

La app intenta conectarse al backend en este orden:

1. **URL de Producción** (si está configurada en `.env` y no es localhost)
2. **Detección Automática en Red Local**:
   - Detecta la IP de tu dispositivo
   - Prueba IPs comunes en el mismo segmento de red (.70, .1, .100)
   - Se conecta a la primera que responda
3. **IP del archivo `.env`** (como fallback)
4. **Localhost** (solo funciona con `adb reverse`)

### Ventajas

- No necesitas cambiar la IP manualmente cuando cambias de ubicación
- Funciona automáticamente en diferentes redes Wi-Fi
- Se adapta automáticamente cuando cambias de red
- Ideal para desarrollo móvil

## Configuración para Desarrollo

### Primera vez

1. Copia el archivo `.env.example` a `.env`:
   ```bash
   cd alma_frontend
   copy .env.example .env
   ```

2. (Opcional) Actualiza la IP en `.env` como fallback:
   ```bash
   ipconfig
   # Busca la IPv4 de tu Wi-Fi (ej: 172.17.21.70)
   ```

3. Edita `.env`:
   ```
   API_BASE_URL=http://TU_IP_AQUI:8080/api
   ```

### Cuando cambies de red

**¡No necesitas hacer nada!** La app detectará automáticamente la nueva IP.

Si quieres actualizar el fallback manualmente:
1. Ejecuta `ipconfig`
2. Actualiza la IP en `.env`
3. Recarga la app (presiona `r` + `r` en Metro Bundler)

## Configuración para Producción

1. Edita `.env`:
   ```
   API_BASE_URL=https://api.tudominio.com/api
   ```

2. La app usará esta URL directamente (sin detección automática)

## Logs de Depuración

Al iniciar la app, verás en la consola:
- `✅ Usando URL de producción:` - Cuando usa la URL de producción
- `✅ Backend detectado automáticamente en:` - Cuando detecta automáticamente
- `✅ Usando URL configurada en .env:` - Cuando usa el fallback del .env
- `⚠️ Usando fallback a localhost:` - Solo como último recurso

## Requisitos del Backend

Para que la detección automática funcione, tu backend debe responder a:
```
GET http://[IP]:8080/api/health
```

Si no tienes este endpoint, la detección automática usará el fallback del `.env`.

## Troubleshooting

### La app no se conecta al backend

1. Verifica que tu dispositivo y PC están en la misma red Wi-Fi
2. Verifica que el backend está corriendo: `http://TU_IP:8080/api`
3. Revisa los logs en la consola para ver qué URL se está usando
4. Actualiza manualmente la IP en `.env` como fallback

### Quiero forzar una IP específica

Simplemente edita `.env` con la IP deseada:
```
API_BASE_URL=http://192.168.1.100:8080/api
```

La detección automática la usará como fallback si no encuentra el backend automáticamente.
