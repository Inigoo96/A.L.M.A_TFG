import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  TouchableOpacity,
  Alert,
  ActivityIndicator,
  ScrollView,
  Image,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import authService from '../services/authService';
import {colors} from '../theme';
import {RoleGuard} from '../components/RoleGuard';
import {TipoUsuario} from '../types/api.types';
import {styles} from '../styles/screens/DashboardScreen.styles';

const DashboardScreen = ({navigation}: any) => {
  const [loading, setLoading] = useState(false);
  const [userEmail, setUserEmail] = useState('');
  const [userType, setUserType] = useState('');
  const [isPasswordTemporary, setIsPasswordTemporary] = useState(false);

  useEffect(() => {
    loadUserData();
    checkTemporaryPassword();
  }, []);

  useEffect(() => {
    let alertInterval: ReturnType<typeof setInterval> | null = null;
    
    if (isPasswordTemporary) {
      alertInterval = setInterval(() => {
        Alert.alert(
          '⚠️ Contraseña Temporal - Acción Requerida',
          'Por seguridad, es necesario cambiar tu contraseña temporal antes de continuar usando la aplicación. Esta es una medida de seguridad obligatoria.',
          [
            {
              text: 'Cambiar ahora',
              onPress: () => navigation.navigate('ChangePassword'),
              style: 'default',
            },
          ],
          { 
            cancelable: false,
          }
        );
      }, 300000); // Mostrar alerta cada 5 minutos
    }

    return () => {
      if (alertInterval) {
        clearInterval(alertInterval);
      }
    };
  }, [isPasswordTemporary, navigation]);

  const checkTemporaryPassword = async () => {
    try {
      const isTemp = await AsyncStorage.getItem('password_temporal');
      if (isTemp === 'true') {
        Alert.alert(
          '⚠️ Contraseña Temporal - Acción Requerida',
          'Por seguridad, es necesario cambiar tu contraseña temporal antes de continuar usando la aplicación. Esta es una medida de seguridad obligatoria.',
          [
            {
              text: 'Cambiar ahora',
              onPress: () => navigation.navigate('ChangePassword'),
              style: 'default',
            },
          ],
          { 
            cancelable: false,
          }
        );
        setIsPasswordTemporary(true);
      }
    } catch (error) {
      console.error('Error al verificar contraseña temporal:', error);
    }
  };

  const loadUserData = async () => {
    const email = await AsyncStorage.getItem('user_email');
    const type = await AsyncStorage.getItem('user_type');
    setUserEmail(email || '');
    setUserType(type || '');
  };

  const getRoleLabel = (role: string): string => {
    const roleLabels: {[key: string]: string} = {
      ADMIN_ORGANIZACION: 'Administrador de Organización',
      PROFESIONAL: 'Profesional',
      PACIENTE: 'Paciente',
      SUPERADMIN: 'Super Administrador',
    };
    return roleLabels[role] || role;
  };

  const handleLogout = async () => {
    Alert.alert(
      'Cerrar sesión',
      '¿Estás seguro de que deseas cerrar sesión?',
      [
        {
          text: 'Cancelar',
          style: 'cancel',
        },
        {
          text: 'Cerrar sesión',
          onPress: async () => {
            setLoading(true);
            try {
              await authService.logout();
              navigation.replace('UserTypeSelection');
            } catch (error) {
              Alert.alert('Error', 'No se pudo cerrar la sesión');
            } finally {
              setLoading(false);
            }
          },
        },
      ],
    );
  };

  return (
    <View style={styles.container}>
      <ScrollView contentContainerStyle={styles.scrollContent}>
        {/* Header */}
        <View style={styles.header}>
          <Image
            source={require('../assets/images/alma_logo.png')}
            style={styles.logo}
            resizeMode="contain"
          />
          <Text style={styles.title}>A.L.M.A.</Text>
          <Text style={styles.subtitle}>Panel Principal</Text>
        </View>

        {/* Welcome Card */}
        <View style={styles.welcomeCard}>
          <Text style={styles.welcomeTitle}>¡Bienvenido!</Text>
          <Text style={styles.welcomeText}>
            Gracias por confiar en nosotros para acompañarte en este proceso
          </Text>
        </View>

        {/* User Info Card */}
        <View style={styles.infoCard}>
          <Text style={styles.cardTitle}>Información de Usuario</Text>

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Email:</Text>
            <Text style={styles.infoValue}>{userEmail}</Text>
          </View>

          <View style={styles.divider} />

          <View style={styles.infoRow}>
            <Text style={styles.infoLabel}>Rol:</Text>
            <View style={styles.roleBadge}>
              <Text style={styles.roleBadgeText}>{getRoleLabel(userType)}</Text>
            </View>
          </View>
        </View>

        {/* Actions Card - Based on Role */}
        <View style={styles.actionsCard}>
          <Text style={styles.cardTitle}>Acciones Disponibles</Text>

          {/* ADMIN_ORGANIZACION Actions */}
          <RoleGuard allowedRoles={[TipoUsuario.ADMIN_ORGANIZACION]}>
            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => navigation.navigate('RegisterProfesional')}>
              <Text style={styles.actionButtonText}>➕ Registrar Profesional</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => navigation.navigate('RegisterPaciente')}>
              <Text style={styles.actionButtonText}>➕ Registrar Paciente</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => navigation.navigate('GestionAsignaciones')}>
              <Text style={styles.actionButtonText}>🔗 Gestionar Asignaciones</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => Alert.alert('Próximamente', 'Pantalla de gestión de usuarios')}>
              <Text style={styles.actionButtonText}>👥 Ver Usuarios</Text>
            </TouchableOpacity>
          </RoleGuard>

          {/* PROFESIONAL Actions */}
          <RoleGuard allowedRoles={[TipoUsuario.PROFESIONAL]}>
            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => navigation.navigate('MisPacientes')}>
              <Text style={styles.actionButtonText}>👥 Mis Pacientes</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => navigation.navigate('MisEstadisticas')}>
              <Text style={styles.actionButtonText}>📊 Mis Estadísticas</Text>
            </TouchableOpacity>
          </RoleGuard>

          {/* PACIENTE Actions */}
          <RoleGuard allowedRoles={[TipoUsuario.PACIENTE]}>
            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => Alert.alert('Próximamente', 'Pantalla de mis profesionales')}>
              <Text style={styles.actionButtonText}>👨‍⚕️ Mis Profesionales</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => Alert.alert('Próximamente', 'Pantalla de recursos')}>
              <Text style={styles.actionButtonText}>📚 Recursos de Apoyo</Text>
            </TouchableOpacity>
          </RoleGuard>

          {/* SUPER_ADMIN Actions */}
          <RoleGuard allowedRoles={[TipoUsuario.SUPER_ADMIN]}>
            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => Alert.alert('Próximamente', 'Pantalla de organizaciones')}>
              <Text style={styles.actionButtonText}>🏢 Gestionar Organizaciones</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.actionButton}
              onPress={() => Alert.alert('Próximamente', 'Pantalla de estadísticas globales')}>
              <Text style={styles.actionButtonText}>📊 Estadísticas Globales</Text>
            </TouchableOpacity>
          </RoleGuard>
        </View>

        {/* Change Password Button (if temporary) */}
        {isPasswordTemporary && (
          <TouchableOpacity
            style={[styles.changePasswordButton]}
            onPress={() => navigation.navigate('ChangePassword')}>
            <Text style={styles.changePasswordButtonText}>
              Cambiar Contraseña Temporal
            </Text>
          </TouchableOpacity>
        )}

        {/* Logout Button */}
        <TouchableOpacity
          style={[styles.logoutButton, loading && styles.buttonDisabled]}
          onPress={handleLogout}
          disabled={loading}>
          {loading ? (
            <ActivityIndicator color={colors.white} />
          ) : (
            <Text style={styles.logoutButtonText}>Cerrar Sesión</Text>
          )}
        </TouchableOpacity>
      </ScrollView>
    </View>
  );
};

export default DashboardScreen;