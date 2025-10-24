import React from 'react';
import {
  ScrollView,
  Text
} from 'react-native';
import Icon from 'react-native-vector-icons/MaterialCommunityIcons';
import {
  Container,
  Header,
  HeartIcon,
  Separator,
  Content,
  Title,
  ActionButton,
  ActionButtonText,
  LogoutButton,
  LogoutButtonText,
  ChangePasswordButton,
  theme
} from '../../styles/screens/Dashboard/SuperAdminDashboard.styles';

const SuperAdminDashboard = ({navigation, handleLogout, isPasswordTemporary}: any) => {

  const handleNavigation = (screen: string) => {
    if (screen) {
      navigation.navigate(screen);
    }
  };

  const actions = [
    { icon: 'domain', text: 'Gestionar Organizaciones', screen: 'GestionOrganizaciones' },
    { icon: 'account-supervisor-outline', text: 'Gestionar Usuarios', screen: '' }, // TODO: Implementar
    { icon: 'chart-bar', text: 'Estadísticas Globales', screen: '' }, // TODO: Implementar
    { icon: 'format-list-bulleted', text: 'Logs de Actividad', screen: 'Auditoria' },
    { icon: 'cog-outline', text: 'Mantenimiento', screen: '' }, // TODO: Implementar
  ];

  return (
    <Container>
      <Header>
        <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
      </Header>
      <Separator/>
      <ScrollView>
        <Content>
          <Title>Panel del Super Administrador</Title>
          {actions.map((item, index) => (
            <ActionButton key={index} onPress={() => handleNavigation(item.screen)}>
              <Icon name={item.icon} size={24} color={theme.primaryGreen} />
              <ActionButtonText>{item.text}</ActionButtonText>
            </ActionButton>
          ))}
        </Content>
        {isPasswordTemporary && (
          <ChangePasswordButton onPress={() => navigation.navigate('ChangePassword')}>
            <LogoutButtonText>Cambiar Contraseña Temporal</LogoutButtonText>
          </ChangePasswordButton>
        )}
        <LogoutButton onPress={handleLogout}>
            <LogoutButtonText>Cerrar Sesión</LogoutButtonText>
        </LogoutButton>
      </ScrollView>
    </Container>
  );
};

export default SuperAdminDashboard;