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
} from '../../styles/screens/Dashboard/ProfesionalDashboard.styles';

const ProfesionalDashboard = ({navigation, handleLogout, isPasswordTemporary}: any) => {

  const actions = [
    { icon: 'account-multiple-outline', text: 'Pacientes Asignados' },
    { icon: 'forum-outline', text: 'Grupos de Apoyo' },
    { icon: 'chart-timeline-variant', text: 'Evolución Emocional' },
    { icon: 'message-text-outline', text: 'Comunicación Profesional' },
    { icon: 'book-open-page-variant-outline', text: 'Recursos' },
  ];

  return (
    <Container>
      <Header>
        <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
      </Header>
      <Separator/>
      <ScrollView>
        <Content>
          <Title>Panel del Profesional</Title>
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

export default ProfesionalDashboard;