/**
 * Paleta de colores A.L.M.A.
 * Diseño enfocado en tonos naturales suaves que transmiten calma y cercanía
 */

export const colors = {
  // Colores principales
  background: '#f1ebdd',        // Blanco roto - fondo base
  primary: '#b5c99a',           // Verde té - botones primarios, header
  secondary: '#ffc5a1',         // Melocotón - botones secundarios, alertas
  darkGreen: '#2d3a20',         // Verde oscuro - títulos y texto alto contraste
  mediumGreen: '#5a6b4a',       // Verde medio - subtítulos, navegación
  error: '#e74c3c',             // Rojo - errores y mensajes críticos

  // Variantes para estados
  primaryLight: '#c7ddb5',      // Verde claro para gradientes
  primaryDark: '#a0b385',       // Verde oscuro para hover

  // Colores para texto
  textPrimary: '#2d3a20',       // Texto principal
  textSecondary: '#5a6b4a',     // Texto secundario
  textTertiary: '#3d4a2e',      // Texto terciario

  // Colores neutros
  white: '#ffffff',
  border: '#e0d6c4',
  inputBackground: '#f1ebdd',

  // Estados de alerta
  success: '#b5c99a',
  warning: '#ffc5a1',
  info: '#c7b299',

  // Sombras
  shadowLight: 'rgba(0, 0, 0, 0.08)',
  shadowMedium: 'rgba(0, 0, 0, 0.1)',
};

export const gradients = {
  primaryDiagonal: 'linear-gradient(135deg, #b5c99a, #c7ddb5)',
};