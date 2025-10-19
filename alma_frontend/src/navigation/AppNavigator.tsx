import React from 'react';
import {NavigationContainer} from '@react-navigation/native';
import {createNativeStackNavigator} from '@react-navigation/native-stack';
import SplashScreen from '../screens/SplashScreen';
import UserTypeSelectionScreen from '../screens/Auth/UserTypeSelectionScreen';
import LoginScreen from '../screens/Auth/LoginScreen';
import RegisterOrganizationScreen from '../screens/Auth/RegisterOrganizationScreen';
import ChangePasswordScreen from '../screens/Auth/ChangePasswordScreen';
import ForgotPasswordScreen from '../screens/Auth/ForgotPasswordScreen';
import DashboardScreen from '../screens/DashboardScreen';

export type RootStackParamList = {
  Splash: undefined;
  UserTypeSelection: undefined;
  Login: undefined;
  RegisterOrganization: undefined;
  ChangePassword: undefined;
  ForgotPassword: undefined;
  Dashboard: undefined;
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
        <Stack.Screen name="RegisterOrganization" component={RegisterOrganizationScreen} />
        <Stack.Screen name="ChangePassword" component={ChangePasswordScreen} />
        <Stack.Screen name="ForgotPassword" component={ForgotPasswordScreen} />
        <Stack.Screen name="Dashboard" component={DashboardScreen} />
      </Stack.Navigator>
    </NavigationContainer>
  );
};

export default AppNavigator;