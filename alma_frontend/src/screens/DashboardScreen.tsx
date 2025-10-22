import React, {useState, useEffect} from 'react';
import {
  View,
  Text,
  Alert,
  ActivityIndicator,
} from 'react-native';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {TipoUsuario} from '../types/api.types';
import {styles} from '../styles/screens/DashboardScreen.styles';

// Import role-specific dashboards
import PacienteDashboard from './Dashboard/PacienteDashboard';
import ProfesionalDashboard from './Dashboard/ProfesionalDashboard';
import AdminOrgDashboard from './Dashboard/AdminOrgDashboard';
import SuperAdminDashboard from './Dashboard/SuperAdminDashboard';
import authService from '../services/authService';

const DashboardScreen = ({navigation}: any) => {
  const [loading, setLoading] = useState(true); // Start with loading true
  const [userType, setUserType] = useState<TipoUsuario | '' | null>('');
  const [isPasswordTemporary, setIsPasswordTemporary] = useState(false);

  useEffect(() => {
    const initialize = async () => {
      await loadUserData();
      await checkTemporaryPassword();
      setLoading(false);
    };
    initialize();
  }, []);

  useEffect(() => {
    let alertInterval: ReturnType<typeof setInterval> | null = null;
    if (isPasswordTemporary) {
      alertInterval = setInterval(() => {
        showAlert();
      }, 300000); // 5 minutes
    }
    return () => {
      if (alertInterval) clearInterval(alertInterval);
    };
  }, [isPasswordTemporary, navigation]);

  const showAlert = () => {
    Alert.alert(
      '⚠️ Contraseña Temporal - Acción Requerida',
      'Por seguridad, es necesario cambiar tu contraseña temporal.',
      [{text: 'Cambiar ahora', onPress: () => navigation.navigate('ChangePassword')}],
      {cancelable: false}
    );
  };

  const checkTemporaryPassword = async () => {
    try {
      const isTemp = await AsyncStorage.getItem('password_temporal');
      if (isTemp === 'true') {
        showAlert();
        setIsPasswordTemporary(true);
      }
    } catch (error) {
      console.error('Error al verificar contraseña temporal:', error);
    }
  };

  const loadUserData = async () => {
    const type = await AsyncStorage.getItem('user_type') as TipoUsuario;
    setUserType(type || null);
  };

  const handleLogout = async () => {
    Alert.alert(
      'Cerrar sesión',
      '¿Estás seguro de que deseas cerrar sesión?',
      [
        {text: 'Cancelar', style: 'cancel'},
        {
          text: 'Cerrar sesión',
          onPress: async () => {
            await authService.logout();
            navigation.replace('UserTypeSelection');
          },
        },
      ],
    );
  };

  const renderDashboardByRole = () => {
    const props = {navigation, handleLogout, isPasswordTemporary};
    switch (userType) {
      case TipoUsuario.PACIENTE:
        return <PacienteDashboard {...props} />;
      case TipoUsuario.PROFESIONAL:
        return <ProfesionalDashboard {...props} />;
      case TipoUsuario.ADMIN_ORGANIZACION:
        return <AdminOrgDashboard {...props} />;
      case TipoUsuario.SUPER_ADMIN:
        return <SuperAdminDashboard {...props} />;
      default:
        return (
          <View style={styles.centeredContainer}>
            <Text>No se pudo determinar el rol del usuario.</Text>
          </View>
        );
    }
  };

  if (loading) {
    return (
      <View style={styles.centeredContainer}>
        <ActivityIndicator size="large" color="#ACD467" />
      </View>
    );
  }

  return (
    <View style={{flex: 1}}>
        {renderDashboardByRole()}
    </View>
  );
};

export default DashboardScreen;