import { useState, useEffect, useCallback } from 'react';
import AsyncStorage from '@react-native-async-storage/async-storage';
import { TipoUsuario } from '../types/api.types';
import authService from '../services/authService';

/**
 * Hook personalizado para gestionar la autenticación y roles del usuario
 */
export const useAuth = () => {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [userEmail, setUserEmail] = useState<string | null>(null);
  const [userRole, setUserRole] = useState<TipoUsuario | null>(null);
  const [passwordTemporal, setPasswordTemporal] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);

  /**
   * Cargar datos del usuario desde AsyncStorage
   */
  const loadUserData = useCallback(async () => {
    try {
      setIsLoading(true);
      const token = await AsyncStorage.getItem('jwt_token');
      const email = await AsyncStorage.getItem('user_email');
      const role = await AsyncStorage.getItem('user_type');
      const passwordTemp = await AsyncStorage.getItem('password_temporal');

      if (token && email && role) {
        setIsAuthenticated(true);
        setUserEmail(email);
        setUserRole(role as TipoUsuario);
        setPasswordTemporal(passwordTemp === 'true');
      } else {
        setIsAuthenticated(false);
        setUserEmail(null);
        setUserRole(null);
        setPasswordTemporal(false);
      }
    } catch (error) {
      console.error('Error al cargar datos del usuario:', error);
      setIsAuthenticated(false);
    } finally {
      setIsLoading(false);
    }
  }, []);

  /**
   * Cerrar sesión
   */
  const logout = useCallback(async () => {
    try {
      await authService.logout();
      setIsAuthenticated(false);
      setUserEmail(null);
      setUserRole(null);
      setPasswordTemporal(false);
    } catch (error) {
      console.error('Error al cerrar sesión:', error);
    }
  }, []);

  /**
   * Verificar si el usuario tiene un rol específico
   */
  const hasRole = useCallback(
    (requiredRole: TipoUsuario): boolean => {
      return userRole === requiredRole;
    },
    [userRole]
  );

  /**
   * Verificar si el usuario tiene alguno de los roles especificados
   */
  const hasAnyRole = useCallback(
    (requiredRoles: TipoUsuario[]): boolean => {
      return userRole !== null && requiredRoles.includes(userRole);
    },
    [userRole]
  );

  /**
   * Verificar si el usuario es ADMIN_ORGANIZACION
   */
  const isAdminOrganizacion = useCallback((): boolean => {
    return userRole === TipoUsuario.ADMIN_ORGANIZACION;
  }, [userRole]);

  /**
   * Verificar si el usuario es PROFESIONAL
   */
  const isProfesional = useCallback((): boolean => {
    return userRole === TipoUsuario.PROFESIONAL;
  }, [userRole]);

  /**
   * Verificar si el usuario es PACIENTE
   */
  const isPaciente = useCallback((): boolean => {
    return userRole === TipoUsuario.PACIENTE;
  }, [userRole]);

  /**
   * Verificar si el usuario es SUPER_ADMIN
   */
  const isSuperAdmin = useCallback((): boolean => {
    return userRole === TipoUsuario.SUPER_ADMIN;
  }, [userRole]);

  /**
   * Obtener el nombre legible del rol
   */
  const getRoleName = useCallback((): string => {
    switch (userRole) {
      case TipoUsuario.ADMIN_ORGANIZACION:
        return 'Administrador de Organización';
      case TipoUsuario.PROFESIONAL:
        return 'Profesional';
      case TipoUsuario.PACIENTE:
        return 'Paciente';
      case TipoUsuario.SUPER_ADMIN:
        return 'Super Administrador';
      default:
        return 'Sin rol';
    }
  }, [userRole]);

  // Cargar datos del usuario al montar el componente
  useEffect(() => {
    loadUserData();
  }, [loadUserData]);

  return {
    // Estado
    isAuthenticated,
    isLoading,
    userEmail,
    userRole,
    passwordTemporal,

    // Métodos
    loadUserData,
    logout,
    hasRole,
    hasAnyRole,
    isAdminOrganizacion,
    isProfesional,
    isPaciente,
    isSuperAdmin,
    getRoleName,
  };
};