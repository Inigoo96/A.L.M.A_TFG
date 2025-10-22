import React from 'react';
import {
  View,
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
  ProgressCard,
  ProgressText,
  Grid,
  GridButton,
  GridButtonInner,
  GridButtonText,
  BottomNav,
  NavButton,
  NavButtonText,
  LogoutButton,
  LogoutButtonText,
  ChangePasswordButton,
  theme
} from '../../styles/screens/Dashboard/PacienteDashboard.styles';

const PacienteDashboard = ({navigation, handleLogout, isPasswordTemporary}: any) => {

  const gridItems = [
    { icon: 'notebook-edit-outline', text: 'Diario emocional', bold: true },
    { icon: 'account-group-outline', text: 'Grupos de apoyo' },
    { icon: 'meditation', text: 'Espacio personal' },
    { icon: 'lightbulb-on-outline', text: 'Consejos y recursos' },
    { icon: 'doctor', text: 'Apoyo profesional' },
    { icon: 'trophy-outline', text: 'Retos semanales', bold: true },
  ];

  return (
    <Container>
      <Header>
        <HeartIcon source={require('../../assets/images/alma_logo.png')} resizeMode="contain" />
      </Header>
      <Separator/>
      <ScrollView>
        <Content>
          <ProgressCard>
            <ProgressText>Aquí irá un resumen de tu progreso y estado emocional.</ProgressText>
          </ProgressCard>

          <Grid>
            {gridItems.map((item, index) => (
              <GridButton key={index} onPress={() => {}}>
                <GridButtonInner>
                  <Icon name={item.icon} size={40} color={theme.primaryGreen} />
                </GridButtonInner>
                <GridButtonText style={{fontWeight: item.bold ? 'bold' : 'normal'}}>{item.text}</GridButtonText>
              </GridButton>
            ))}
          </Grid>
        </Content>
      </ScrollView>
      {isPasswordTemporary && (
        <ChangePasswordButton onPress={() => navigation.navigate('ChangePassword')}>
          <LogoutButtonText>Cambiar Contraseña Temporal</LogoutButtonText>
        </ChangePasswordButton>
      )}
      <LogoutButton onPress={handleLogout}>
          <LogoutButtonText>Cerrar Sesión</LogoutButtonText>
      </LogoutButton>
      <BottomNav>
        <NavButton onPress={() => {}}>
          <Icon name="chart-line" size={24} color={theme.gray} />
          <NavButtonText>Mi progreso</NavButtonText>
        </NavButton>
        <NavButton onPress={() => {}}>
          <Icon name="forum-outline" size={24} color={theme.gray} />
          <NavButtonText>Mis grupos</NavButtonText>
        </NavButton>
        <NavButton onPress={() => {}}>
          <Icon name="phone-outline" size={24} color={theme.gray} />
          <NavButtonText>Contactar</NavButtonText>
        </NavButton>
      </BottomNav>
    </Container>
  );
};

export default PacienteDashboard;