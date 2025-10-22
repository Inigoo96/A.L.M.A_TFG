import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import SplashScreen from '../screens/SplashScreen';
import UserTypeSelectionScreen from '../screens/Auth/UserTypeSelectionScreen';
import LoginScreen from '../screens/Auth/LoginScreen';
import RegisterOrganizationFormScreen from '../screens/Auth/RegisterOrganizationFormScreen';
import RegisterAdminOrgScreen from '../screens/Auth/RegisterAdminOrgScreen';
import ChangePasswordScreen from '../screens/Auth/ChangePasswordScreen';
import ForgotPasswordScreen from '../screens/Auth/ForgotPasswordScreen';
import DashboardScreen from '../screens/DashboardScreen';
import RegisterProfesionalScreen from '../screens/Admin/RegisterProfesionalScreen';
import RegisterPacienteScreen from '../screens/Admin/RegisterPacienteScreen';
import GestionAsignacionesScreen from '../screens/Admin/GestionAsignacionesScreen';
import MisPacientesScreen from '../screens/Profesional/MisPacientesScreen';
import MisEstadisticasScreen from '../screens/Profesional/MisEstadisticasScreen';

export type RootStackParamList = {
  Splash: undefined;
  UserTypeSelection: undefined;
  Login: undefined;
  RegisterOrganizationForm: undefined;
  RegisterAdminOrg: { organizationData: any };
  ChangePassword: undefined;
  ForgotPassword: undefined;
  Dashboard: undefined;
  RegisterProfesional: undefined;
  RegisterPaciente: undefined;
  GestionAsignaciones: undefined;
  MisPacientes: undefined;
  MisEstadisticas: undefined;
};

const Stack = createNativeStackNavigator<RootStackParamList>();

const AppNavigator = () => {
  return (
    <NavigationContainer>
      <Stack.Navigator
        initialRouteName="Splash"
        screenOptions={{
          headerShown: false,
        }}>
        <Stack.Screen name="Splash" component={SplashScreen} />
        <Stack.Screen name="UserTypeSelection" component={UserTypeSelectionScreen} />
        <Stack.Screen name="Login" component={LoginScreen} />
        <Stack.Screen name="RegisterOrganizationForm" component={RegisterOrganizationFormScreen} />
        <Stack.Screen name="RegisterAdminOrg" component={RegisterAdminOrgScreen} />
        <Stack.Screen name="ChangePassword" component={ChangePasswordScreen} />
        <Stack.Screen name="ForgotPassword" component={ForgotPasswordScreen} />
        <Stack.Screen name="Dashboard" component={DashboardScreen} />

        {/* Pantallas de Admin */}
        <Stack.Screen
          name="RegisterProfesional"
          component={RegisterProfesionalScreen}
          options={{ headerShown: true, title: 'Registrar Profesional' }}
        />
        <Stack.Screen
          name="RegisterPaciente"
          component={RegisterPacienteScreen}
          options={{ headerShown: true, title: 'Registrar Paciente' }}
        />
        <Stack.Screen
          name="GestionAsignaciones"
          component={GestionAsignacionesScreen}
          options={{ headerShown: true, title: 'Gestión de Asignaciones' }}
        />

        {/* Pantallas de Profesional */}
        <Stack.Screen
          name="MisPacientes"
          component={MisPacientesScreen}
          options={{ headerShown: true, title: 'Mis Pacientes' }}
        />
        <Stack.Screen
          name="MisEstadisticas"
          component={MisEstadisticasScreen}
          options={{ headerShown: true, title: 'Mis Estadísticas' }}
        />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;
