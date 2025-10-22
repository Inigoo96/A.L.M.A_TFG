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
} from '../../styles/screens/Dashboard/AdminOrgDashboard.styles';

const AdminOrgDashboard = ({navigation, handleLogout, isPasswordTemporary}: any) => {

  const actions = [
    { icon: 'account-cog-outline', text: 'Gestionar Usuarios' },
    { icon: 'account-group-outline', text: 'Gestionar Grupos' },
    { icon: 'account-tie-outline', text: 'Gestionar Profesionales' },
    { icon: 'view-dashboard-outline', text: 'Panel de Datos' },
    { icon: 'bell-outline', text: 'Notificaciones' },
  ];

  return (
    <Container>
      <Header>
        <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
      </Header>
      <Separator/>
      <ScrollView>
        <Content>
          <Title>Panel del Administrador</Title>
          {actions.map((item, index) => (
            <ActionButton key={index} onPress={() => {}}>
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

export default AdminOrgDashboard;