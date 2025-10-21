import React from 'react';
import { View, Text, StyleSheet } from 'react-native';
import { useAuth } from '../hooks/useAuth';
import { TipoUsuario } from '../types/api.types';
import { colors } from '../theme';

interface RoleGuardProps {
  /**
   * Roles permitidos para ver este componente
   */
  allowedRoles: TipoUsuario[];

  /**
   * Contenido a renderizar si el usuario tiene el rol adecuado
   */
  children: React.ReactNode;

  /**
   * Contenido alternativo a mostrar si el usuario no tiene permisos
   * Si no se proporciona, no se renderiza nada
   */
  fallback?: React.ReactNode;

  /**
   * Si es true, muestra un mensaje de "sin permisos" por defecto
   * Solo se aplica si no hay fallback personalizado
   */
  showDeniedMessage?: boolean;
}

/**
 * Componente para proteger contenido basado en roles de usuario
 * Solo renderiza el contenido si el usuario tiene uno de los roles permitidos
 *
 * @example
 * ```tsx
 * <RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
 *   <Button title="Crear Usuario" onPress={handleCreate} />
 * </RoleGuard>
 * ```
 */
export const RoleGuard: React.FC<RoleGuardProps> = ({
  allowedRoles,
  children,
  fallback,
  showDeniedMessage = false,
}) => {
  const { userRole, isLoading } = useAuth();

  // Mientras carga, no mostramos nada
  if (isLoading) {
    return null;
  }

  // Si el usuario no tiene rol, no mostramos nada
  if (!userRole) {
    return null;
  }

  // Verificar si el usuario tiene alguno de los roles permitidos
  const hasPermission = allowedRoles.includes(userRole);

  // Si tiene permisos, renderizar el contenido
  if (hasPermission) {
    return <>{children}</>;
  }

  // Si no tiene permisos, renderizar el fallback o mensaje de denegación
  if (fallback) {
    return <>{fallback}</>;
  }

  if (showDeniedMessage) {
    return (
      <View style={styles.deniedContainer}>
        <Text style={styles.deniedText}>
          No tienes permisos para ver este contenido
        </Text>
      </View>
    );
  }

  // Por defecto, no renderizar nada
  return null;
};

const styles = StyleSheet.create({
  deniedContainer: {
    padding: 16,
    backgroundColor: colors.error + '20', // 20% de opacidad
    borderRadius: 8,
    borderWidth: 1,
    borderColor: colors.error,
    marginVertical: 8,
  },
  deniedText: {
    color: colors.error,
    textAlign: 'center',
    fontSize: 14,
  },
});