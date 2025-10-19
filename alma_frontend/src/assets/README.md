# Assets de A.L.M.A.

Esta carpeta contiene todos los recursos visuales de la aplicación.

## Estructura

### 📁 images/
Coloca aquí el logo y las imágenes de la aplicación:
- `logo.png` - Logo principal de A.L.M.A.
- `logo-white.png` - Logo en blanco para fondos oscuros
- Otras imágenes que necesites

### 📁 videos/
Coloca aquí los videos de transición:
- `loading.mp4` - Video de carga entre pantallas
- Otros videos que necesites

### 📁 fonts/
Coloca aquí las fuentes personalizadas si las usas:
- Archivos `.ttf` o `.otf`

## Formato recomendado para imágenes

- **Logo**: PNG con fondo transparente
- **Tamaño recomendado**: 512x512px o mayor (se escalará automáticamente)
- **Formato**: PNG, JPG, o SVG

## Formato recomendado para videos

- **Formato**: MP4, MOV
- **Resolución**: 1080x1920 (vertical) o 1920x1080 (horizontal)
- **Duración**: 2-5 segundos para transiciones
- **Tamaño**: Máximo 5MB para optimizar rendimiento

## Uso en el código

Para usar el logo en tus pantallas:

\`\`\`typescript
import {Image} from 'react-native';

// En tu componente:
<Image
  source={require('../assets/images/logo.png')}
  style={{width: 150, height: 150}}
/>
\`\`\`

Para videos, necesitarás instalar `react-native-video`:
\`\`\`bash
npm install react-native-video
\`\`\`